package com.github.makewheels.aitools.word;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.makewheels.aitools.gpt.GptService;
import com.github.makewheels.aitools.gpt.Message;
import com.github.makewheels.aitools.gpt.ROLE;
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
            这个 API 用于查询单词的含义及常见用法例句。你可以输入一个单词数组，系统会返回每个单词的解释和几个常见的例句，
            帮助你更好地理解该单词的用法。
            请求示例：apple,banana
            返回示例：
            {
              "results": [
                 {
                   "word": "play",
                   "pronunciation": "/pleɪ/",
                   "meanings": [
                     {
                       "partOfSpeech": "v.",
                       "meaningEnglish": "to engage in an activity for enjoyment or recreation",
                       "meaningChinese": "玩，娱乐",
                       "exampleEnglish": "Let's play basketball this afternoon.",
                       "exampleChinese": "今天下午我们去打篮球吧。"
                     },
                     {
                       "partOfSpeech": "v.",
                       "meaningEnglish": "to perform music or a musical instrument",
                       "meaningChinese": "演奏（音乐或乐器）",
                       "exampleEnglish": "She plays the guitar really well.",
                       "exampleChinese": "她吉他弹得很好。"
                     },
                     {
                       "partOfSpeech": "n.",
                       "meaningEnglish": "an activity or game for entertainment",
                       "meaningChinese": "比赛，游戏",
                       "exampleEnglish": "We had a great time at the play last night.",
                       "exampleChinese": "我们昨晚在戏剧表演中度过了愉快的时光。"
                     }
                   ]
                 }
               ]
            }
            """;

    private static final String WORD_JSON_SCHEMA =
            """
            {
              "$schema": "http://json-schema.org/draft-07/schema#",
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
                      "meaningEnglish": {
                        "type": "string",
                        "description": "The meaning of the word in English"
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
                      }
                    },
                    "required": [
                      "partOfSpeech",
                      "meaningEnglish",
                      "meaningChinese",
                      "exampleEnglish",
                      "exampleChinese"
                    ]
                  },
                  "description": "A list of meanings for the word"
                }
              },
              "required": [
                "word",
                "pronunciation",
                "meanings"
              ]
            }
            """;

    public JSONArray getWordExplain(List<String> wordList) {
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
        return JSON.parseObject(responseContent).getJSONArray("results");
    }
}
