package com.github.makewheels.aitools.word;

import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.makewheels.aitools.gpt.GptService;
import com.github.makewheels.aitools.gpt.Message;
import com.github.makewheels.aitools.gpt.ROLE;
import com.github.makewheels.aitools.word.bean.Meaning;
import com.github.makewheels.aitools.word.bean.Word;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class WordService {
    @Resource
    private GptService gptService;

    private static final String SYSTEM_PROMPT = """
            我正在做一个英语学习教程，面向初学者，需要你根据用户输入的单词，给出单词的含义发音例句等等信息，
            具体要求如下：
            1.注意你给出的用法一定要是日常美国人实际在用的，不要教科书上不符合实际的，
            2.例句要简单，因为我们以学这个单词为主。
            3.词性partOfSpeech用n. adj. 这种形式表达，不要用noun
            4.imagePrompt用来生成这个例句的插画的提示词，用来让AI生成图片插图，辅助学生理解例句，
                你要描述这个画中的内容，场景，和作画风格。作画风格要稍微简单一些，以学习英语单词为主，不要喧宾夺主
            
            请求示例：play,banana
            返回示例：
            {
              "results": [
                 {
                   "word": "play",
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
                     },
                     {
                       "partOfSpeech": "n.",
                       "meaningChinese": "比赛，游戏",
                       "exampleEnglish": "We had a great time at the play last night.",
                       "exampleChinese": "我们昨晚在戏剧表演中度过了愉快的时光。",
                       "imagePrompt": "A cozy theater scene with actors performing a dramatic play on stage. The actors are dressed in elegant, vintage costumes, and the stage is beautifully lit with dramatic lighting. The audience, sitting in rows, watches with rapt attention, some with their hands resting on the armrests. The overall atmosphere is warm and inviting, with soft red curtains framing the stage."
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
                      "word": {
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
                      "word",
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

    public List<Word> getWordExplain(List<String> wordList) {
        List<Message> messageList = new ArrayList<>();
        Message systemMessage = new Message();
        systemMessage.setRole(ROLE.SYSTEM);
        systemMessage.setContent(SYSTEM_PROMPT);

        Message userMessage = new Message();
        userMessage.setRole(ROLE.USER);
        userMessage.setContent("给我这几个单词的含义: " + StringUtils.join(wordList, ","));

        messageList.add(systemMessage);
        messageList.add(userMessage);

        String responseContent = gptService.completionJsonSchema(messageList, WORD_JSON_SCHEMA);
        JSONArray results = JSON.parseObject(responseContent).getJSONArray("results");

        // 生成图片
        List<Word> words = JSON.parseArray(JSON.toJSONString(results), Word.class);
        for (Word word : words) {
            for (Meaning meaning : word.getMeanings()) {
                String imagePrompt = meaning.getImagePrompt();
                meaning.setImagePromptMd5(DigestUtil.md5Hex(meaning.getImagePrompt()));
                meaning.setImageUrl(gptService.generateImage(imagePrompt));
            }
        }

        return words;
    }
}
