package com.github.makewheels.aitools.gpt.service;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class GptService {
    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    /**
     * 发送post请求到OpenAI
     */
    @Retryable
    private String postRequest(String url, String body) {
        try {
            body = JSON.toJSONString(JSONObject.parseObject(body));
        } catch (Exception e) {
            log.error("JSON转换错误", e);
            log.error(body);
            throw new IllegalArgumentException("JSON转换错误", e);
        }

        log.info("请求GPT url = {}, body = {}", url, body);
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
        log.info("从GPT响应中提取到content: " + content);
        return content;
    }

    /**
     * 请求GPT：完整body
     */
    public String completionWithCompleteBody(String body) {
        String response = this.postRequest(GptConstants.CHAT_COMPLETIONS_URL, body);
        return this.extractContentFromResponse(JSONObject.parseObject(response));
    }

    /**
     * 请求GPT：简单content内容
     */
    public String completionWithSimpleContent(String content) {
        JSONObject body = new JSONObject();
        body.put("model", GptConstants.MODEL);
        Message message = new Message();
        message.setRole(Role.USER);
        message.setContent(content);
        body.put("messages", List.of(message));
        return this.completionWithCompleteBody(body.toJSONString());
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
        String body = String.format(json, GptConstants.MODEL, JSON.toJSONString(messageList), jsonSchema);
        return this.completionWithCompleteBody(body);
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
                    "size": "1024x1024",
                    "response_format": "url"
                }
                """;
        String body = String.format(json, prompt);
        String response = this.postRequest(GptConstants.IMAGES_GENERATIONS_URL, body);
        return JSON.parseObject(response).getJSONArray("data").getJSONObject(0).getString("url");
    }

}
