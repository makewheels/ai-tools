package com.github.makewheels.aitools.gpt;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class GptService {
    @Value("${spring.ai.openai.api-key}")
    private String apiKey;
    private static final String BASE_URL = "https://api.open-proxy.cn";
    private static final String COMPLETION_URL = BASE_URL + "/v1/chat/completions";
    private static final String MODEL = "gpt-4o-2024-11-20";

    /**
     * 向gpt发请求
     */
    private JSONObject request(String url, String body) {
        body = JSON.toJSONString(JSONObject.parseObject(body));

        log.info("请求GPT url = " + url);
        log.info("请求GPT body = " + body);
        HttpResponse httpResponse = HttpUtil.createPost(COMPLETION_URL)
                .bearerAuth(apiKey)
                .body(body)
                .execute();

        String response = httpResponse.body();
        httpResponse.close();
        log.info("GPT响应 " + JSON.toJSONString(JSONObject.parseObject(response)));
        return JSONObject.parseObject(response);
    }

    /**
     * 提取响应内容
     */
    private String extractContentFromResponse(JSONObject responseFromOpenAI) {
        String content = responseFromOpenAI.getJSONArray("choices").getJSONObject(0)
                .getJSONObject("message")
                .getString("content");
        log.info("从GPT响应中提取content " + content);
        return content;
    }

    /**
     * 分析食物图片
     */
    public String analyzeImage(String userContent, String imageUrl) {
        String json = """
                {
                    "model": "%s",
                    "messages": [
                        {
                            "role": "user",
                            "content": [
                                {
                                    "type": "text",
                                    "text": "%s"
                                },
                                {
                                    "type": "image_url",
                                    "image_url": {
                                        "url": "%s"
                                    }
                                }
                            ]
                        }
                    ]
                }
                """;
        String body = String.format(json, MODEL, userContent, imageUrl);

        JSONObject response = request(COMPLETION_URL, body);
        return this.extractContentFromResponse(response);
    }

    /**
     * 限定json格式返回
     */
    public JSONObject jsonModel(List<Message> messageList, String jsonSchema) {
        String json = """
                {
                  "model": "%s",
                  "messages": %s,
                  "response_format": {
                      "type": "json_schema",
                      "json_schema": {
                          "name": "json_schema_response",
                          "strict": true,
                          "schema": %s
                      }
                  }
                }
                """;
        String body = String.format(json, MODEL, JSON.toJSONString(messageList), jsonSchema);
        JSONObject response = request(COMPLETION_URL, body);
        return JSON.parseObject(this.extractContentFromResponse(response));
    }

    private static String wordSchema = """
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

    public static void main(String[] args) {
        List<Message> messageList = new ArrayList<>();
        Message systemMessage = new Message();
        systemMessage.setRole(ROLE.SYSTEM);
        systemMessage.setContent("""
                这个 API 用于查询单词的含义及常见用法例句。你可以输入一个单词数组，系统会返回每个单词的解释和几个常见的例句，
                帮助你更好地理解该单词的用法。
                请求示例：["apple", "banana"]，
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
                """);

        Message userMessage = new Message();
        userMessage.setRole(ROLE.USER);
        userMessage.setContent("""
                给我这几个单词的含义:
                ["complicated","bizarre","eloquent","meticulous","resilient","versatile","vivid","intricate","profound","genuine"]
                """);

        messageList.add(systemMessage);
        messageList.add(userMessage);

        GptService gptService = new GptService();
        gptService.jsonModel(messageList, wordSchema);
    }
}
