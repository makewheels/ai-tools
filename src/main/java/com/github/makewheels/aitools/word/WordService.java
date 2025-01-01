package com.github.makewheels.aitools.word;

import cn.hutool.core.io.FileUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.makewheels.aitools.file.FileService;
import com.github.makewheels.aitools.file.bean.CreateFileDTO;
import com.github.makewheels.aitools.file.constants.FileType;
import com.github.makewheels.aitools.gpt.GptService;
import com.github.makewheels.aitools.gpt.Message;
import com.github.makewheels.aitools.gpt.ROLE;
import com.github.makewheels.aitools.utils.IdService;
import com.github.makewheels.aitools.utils.OssPathUtil;
import com.github.makewheels.aitools.word.bean.Meaning;
import com.github.makewheels.aitools.word.bean.Word;
import com.github.makewheels.aitools.word.response.MeaningResponse;
import com.github.makewheels.aitools.word.response.WordResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

@Service
@Slf4j
public class WordService {
    @Resource
    private GptService gptService;
    @Resource
    private WordRepository wordRepository;
    @Resource
    private FileService fileService;
    @Resource
    private IdService idService;

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

    private static final String WORD_JSON_SCHEMA = """
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
     * 添加新单词释义
     */
    public void addNewWords(List<String> wordContentList) {
        List<Word> wordList = this.getWordExplain(StringUtils.join(wordContentList, ","));
        log.info("开始添加新单词释义, 单词列表: {}", JSON.toJSONString(wordContentList));

        // 生成单词图片
        Map<String, String> imageMap = this.getImage(wordList);

        // 添加到词库
        for (Word word : wordList) {
            if (wordRepository.exist(word.getContent())) {
                log.info("添加新单词释义, 单词已存在, 跳过 {}", word.getContent());
                continue;
            }

            log.info("单词 {} 不存在, 开始添加到词库", word.getContent());
            word.setId(idService.getWordId());
            word.setCreateTime(new Date());
            wordRepository.save(word);

            // 处理图片
            File imageFolder = new File(FileUtil.getTmpDir(), "ai-tools/images/" + System.currentTimeMillis());
            for (Meaning meaning : word.getMeanings()) {
                String imagePromptMd5 = meaning.getImagePromptMd5();
                File imageFile = new File(imageFolder, imagePromptMd5 + ".png");

                // 下载图片
                HttpUtil.downloadFile(imageMap.get(imagePromptMd5), imageFile);

                // 上传对象存储
                CreateFileDTO createFileDTO = new CreateFileDTO();
                createFileDTO.setFileType(FileType.WORD_IMAGE);
                createFileDTO.setExtension("png");
                createFileDTO.setKey(OssPathUtil.getWordImageFile(word.getContent(), imagePromptMd5));
                String imageFileId = fileService.createNewFile(createFileDTO).getId();
                meaning.setImageFileId(imageFileId);
                wordRepository.save(word);
                fileService.uploadFile(imageFileId, imageFile);
                FileUtil.del(imageFile);
                fileService.uploadFinish(imageFileId);
            }

            FileUtil.del(imageFolder);
        }

        log.info("添加新单词释义完成");
    }

    /**
     * 获取图片
     *
     * @return map: promptMd5 -> imageUrl
     */
    public Map<String, String> getImage(List<Word> wordList) {
        Map<String, String> map = new HashMap<>();
        for (Word word : wordList) {
            for (Meaning meaning : word.getMeanings()) {
                String imagePrompt = meaning.getImagePrompt();
                log.info("生成单词释义图片, 单词: {}, 释义: {}", word.getContent(), meaning.getMeaningChinese());
                String imageUrl = gptService.generateImage(imagePrompt);

                String imagePromptMd5 = DigestUtil.md5Hex(meaning.getImagePrompt());
                meaning.setImagePromptMd5(imagePromptMd5);
                map.put(imagePromptMd5, imageUrl);
            }
        }
        return map;
    }

    public WordResponse convertWordToResponse(Word word) {
        if (word == null) {
            return null;
        }
        WordResponse wordResponse = JSON.parseObject(JSON.toJSONString(word), WordResponse.class);
        for (MeaningResponse meaning : wordResponse.getMeanings()) {
            meaning.setImageUrl(fileService.getPresignedUrlByFileId(meaning.getImageFileId()));
        }
        return wordResponse;
    }

    public WordResponse randomPick() {
        Word word = wordRepository.randomPick();
        return this.convertWordToResponse(word);
    }

}
