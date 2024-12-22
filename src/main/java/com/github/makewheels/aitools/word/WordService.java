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
                  "word": "apple",
                  "meanings": [
                    "a round fruit with red or green skin and a whitish interior",
                    "a tech company that produces electronics and software"
                  ],
                  "example_sentences": [
                    "I ate an apple for breakfast.",
                    "Apple is releasing a new iPhone next week."
                  ]
                },
                {
                  "word": "banana",
                  "meanings": [
                    "a long, curved fruit with a yellow skin",
                    "a person who is overly enthusiastic or eccentric"
                  ],
                  "example_sentences": [
                    "I had a banana with my cereal this morning.",
                    "He is acting like a banana today, very funny!"
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
                      "word": {
                        "type": "string",
                        "description": "单词"
                      },
                      "meanings": {
                        "type": "array",
                        "items": {
                          "type": "string",
                          "description": "单词的含义"
                        },
                        "description": "单词的含义数组"
                      },
                      "example_sentences": {
                        "type": "array",
                        "items": {
                          "type": "string",
                          "description": "常用例句"
                        },
                        "description": "常用用法例句数组"
                      }
                    },
                    "required": ["word", "meanings", "example_sentences"],
                    "additionalProperties": false
                  },
                  "description": "返回的单词含义和例句数组"
                }
              },
              "required": ["results"],
              "additionalProperties": false
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
