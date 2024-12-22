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
    private static final String COMPLETION_URL = BASE_URL + "/v1/chat/completions";
    public static final String MODEL = "gpt-4o-2024-11-20";

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
        body = JSON.toJSONString(JSONObject.parseObject(body));

        log.info("请求GPT url = " + COMPLETION_URL);
        log.info("请求GPT body = " + body);
        HttpResponse httpResponse = HttpUtil.createPost(COMPLETION_URL)
                .bearerAuth(apiKey)
                .body(body)
                .execute();

        String response = httpResponse.body();
        httpResponse.close();
        log.info("GPT响应 " + JSON.toJSONString(JSONObject.parseObject(response)));

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

}
