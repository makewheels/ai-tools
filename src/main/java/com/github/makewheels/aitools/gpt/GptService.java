package com.github.makewheels.aitools.gpt;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class GptService {
    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    private static final String BASE_URL = "https://api.open-proxy.cn";
    private static final String CHAT_COMPLETION_URL = BASE_URL + "/v1/chat/completions";
    private static final String IMAGE_GENERATION_URL = BASE_URL + "/v1/images/generations";

    public static final String MODEL = "gpt-4o-2024-11-20";

    /**
     * 发送post请求到OpenAI
     */
    private String postRequest(String url, String body) {
        body = JSON.toJSONString(JSONObject.parseObject(body));

        log.info("请求GPT url = " + url);
        log.info("请求GPT body = " + body);
        HttpResponse httpResponse = HttpUtil.createPost(url)
                .bearerAuth(apiKey)
                .body(body)
                .execute();

        String response = httpResponse.body();
        httpResponse.close();

        log.info("GPT响应 " + JSON.toJSONString(JSONObject.parseObject(response)));

        return response;
    }

    /**
     * 提取响应内容
     */
    private String extractContentFromResponse(JSONObject responseFromOpenAI) {
        String content = responseFromOpenAI.getJSONArray("choices").getJSONObject(0)
                .getJSONObject("message").getString("content");
        log.info("从GPT响应中提取到content " + content);
        return content;
    }

    /**
     * 向gpt发请求
     */
    public String completion(String body) {
        String response = this.postRequest(CHAT_COMPLETION_URL, body);
        return this.extractContentFromResponse(JSONObject.parseObject(response));
    }

    /**
     * 限定json格式返回
     */
    public String completionJsonSchema(List<Message> messageList, String jsonSchema) {
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
        return completion(body);
    }

    /**
     * 生成图片
     */
    public String generateImage(String prompt) {
        String json = """
                {
                    "model": "dall-e-3",
                    "prompt": "%s",
                    "n": 1,
                    "size": "1024x1024"
                }
                """;
        String body = String.format(json, prompt);
        String response = this.postRequest(IMAGE_GENERATION_URL, body);
        return JSON.parseObject(response).getJSONArray("data").getJSONObject(0).getString("url");
    }

}
