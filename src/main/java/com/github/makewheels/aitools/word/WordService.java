package com.github.makewheels.aitools.word;

import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.makewheels.aitools.file.FileService;
import com.github.makewheels.aitools.gpt.GptService;
import com.github.makewheels.aitools.gpt.Message;
import com.github.makewheels.aitools.gpt.ROLE;
import com.github.makewheels.aitools.word.bean.Meaning;
import com.github.makewheels.aitools.word.bean.Word;
import com.github.makewheels.aitools.word.response.MeaningResponse;
import com.github.makewheels.aitools.word.response.WordResponse;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WordService {
    @Resource
    private GptService gptService;
    @Resource
    private WordRepository wordRepository;
    @Resource
    private FileService fileService;

    private static final String SYSTEM_PROMPT = """
            我正在做一个英语学习教程，面向初学者，需要你根据用户输入的单词，给出单词的几种含义发音例句等等信息，
            具体要求如下：
            1.注意你给出的用法一定要是日常美国人实际在用的，不要教科书上不符合实际的。
            2.对meanings的要求：如果这个词有多种含义，请给出多种，不要只给一种，那样太单调了，每种解释不重复
            3.example例句要简单，因为我们以学这个单词为主。例句要和meanings相关。
            4.词性partOfSpeech用n. adj. 这种形式表达，不要用noun。
            5.imagePrompt用来生成这个例句的插画的提示词，用来让AI生成图片插图，辅助学生理解例句，
                你要描述这个画中的内容，场景，和作画风格。作画风格要稍微简单一些，以学习英语单词为主，不要喧宾夺主。
            
            请求示例：play
            返回示例：
            {
              "results": [
                 {
                   "content": "play",
                   "pronunciation": "/pleɪ/",
                   "meanings": [
                     {
                       "partOfSpeech": "v.",
                       "meaningChinese": "玩，娱乐",
                       "exampleEnglish": "Let's play basketball this afternoon.",
                       "exampleChinese": "今天下午我们去打篮球吧。",
                       "imagePrompt": "A group of young friends playing basketball on an outdoor court during the afternoon, the sun shining brightly. They are casually dressed in T-shirts, shorts, and sneakers. The scene is lively with one player jumping for a dunk, while others are positioning themselves for a pass. The background shows a clear blue sky and trees lining the court."
                     },
                     {
                       "partOfSpeech": "v.",
                       "meaningChinese": "演奏（音乐或乐器）",
                       "exampleEnglish": "She plays the guitar really well.",
                       "exampleChinese": "她吉他弹得很好。",
                       "imagePrompt": "A young woman passionately playing the guitar on a small stage. She is wearing a casual outfit with a relaxed and confident expression. The spotlight is on her, creating a warm, intimate atmosphere. In the background, there are blurred figures of people sitting and listening attentively, with a soft glow coming from stage lights."
                     }
                   ]
                 }
               ]
            }
            """;

    private static final String WORD_JSON_SCHEMA =
            """
                    {
                      "type": "object",
                      "properties": {
                        "results": {
                          "type": "array",
                          "items": {
                            "type": "object",
                            "properties": {
                              "content": {
                                "type": "string",
                                "description": "The word being defined"
                              },
                              "pronunciation": {
                                "type": "string",
                                "description": "The phonetic pronunciation of the word"
                              },
                              "meanings": {
                                "type": "array",
                                "items": {
                                  "type": "object",
                                  "properties": {
                                    "partOfSpeech": {
                                      "type": "string",
                                      "description": "The part of speech (e.g., noun, verb, etc.)"
                                    },
                                    "meaningChinese": {
                                      "type": "string",
                                      "description": "The meaning of the word in Chinese"
                                    },
                                    "exampleEnglish": {
                                      "type": "string",
                                      "description": "An example sentence in English"
                                    },
                                    "exampleChinese": {
                                      "type": "string",
                                      "description": "The example sentence translated into Chinese"
                                    },
                                    "imagePrompt": {
                                      "type": "string",
                                      "description": "A visual description for generating an image that illustrates the example sentence"
                                    }
                                  },
                                  "required": [
                                    "partOfSpeech",
                                    "meaningChinese",
                                    "exampleEnglish",
                                    "exampleChinese",
                                    "imagePrompt"
                                  ],
                                  "additionalProperties": false
                                },
                                "description": "A list of meanings for the word"
                              }
                            },
                            "required": [
                              "content",
                              "pronunciation",
                              "meanings"
                            ],
                            "additionalProperties": false
                          }
                        }
                      },
                      "required": [
                        "results"
                      ],
                      "additionalProperties": false
                    }
                    """;

    public List<Word> getWordExplain(String wordList) {
        List<Message> messageList = new ArrayList<>();
        Message systemMessage = new Message();
        systemMessage.setRole(ROLE.SYSTEM);
        systemMessage.setContent(SYSTEM_PROMPT);

        Message userMessage = new Message();
        userMessage.setRole(ROLE.USER);
        userMessage.setContent("给我这几个单词的含义: " + wordList);

        messageList.add(systemMessage);
        messageList.add(userMessage);

        String responseContent = gptService.completionJsonSchema(messageList, WORD_JSON_SCHEMA);
        JSONArray results = JSON.parseObject(responseContent).getJSONArray("results");
        List<Word> words = JSON.parseArray(JSON.toJSONString(results), Word.class);

        for (Word word : words) {
            for (Meaning meaning : word.getMeanings()) {
                String imagePrompt = meaning.getImagePrompt();
                meaning.setImagePromptMd5(DigestUtil.md5Hex(imagePrompt));
            }
        }
        return words;
    }

    /**
     * 获取图片
     *
     * @return promptMd5 -> imageUrl
     */
    public Map<String, String> getImage(List<Word> wordList) {
        Map<String, String> map = new HashMap<>();
        for (Word word : wordList) {
            for (Meaning meaning : word.getMeanings()) {
                String imagePrompt = meaning.getImagePrompt();
                String imageUrl = gptService.generateImage(imagePrompt);

                String imagePromptMd5 = DigestUtil.md5Hex(meaning.getImagePrompt());
                meaning.setImagePromptMd5(imagePromptMd5);
                map.put(imagePromptMd5, imageUrl);
            }
        }
        return map;
    }

    private WordResponse convertToResponse(Word word) {
        return JSON.parseObject(JSON.toJSONString(word), WordResponse.class);
    }

    public WordResponse randomPick() {
        Word word = wordRepository.randomPick();
        WordResponse wordResponse = this.convertToResponse(word);
        for (MeaningResponse meaning : wordResponse.getMeanings()) {
            meaning.setImageUrl(fileService.getPresignedUrlByFileId(meaning.getImageFileId()));
        }
        return wordResponse;
    }
}
