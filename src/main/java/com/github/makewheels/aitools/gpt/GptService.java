package com.github.makewheels.aitools.gpt;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GptService {
    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    public JSONObject analyzeImage(String prompt, String imageUrl) {
        String body = String.format(
                """
                        {
                            "model": "gpt-4o-mini",
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
                        """,
                prompt,
                imageUrl
        );

        log.info("请求GPT body = " + JSON.toJSONString(JSONObject.parseObject(body)));
//        String url = "https://api.claudeshop.top/v1/chat/completions";
        String url = "https://api.claude-plus.top/v1/chat/completions";
        String response = HttpUtil.createPost(url)
                .bearerAuth(apiKey).body(body).execute().body();
        log.info("GPT响应 = " + JSON.toJSONString(JSONObject.parseObject(response)));
        return JSONObject.parseObject(response);
    }
}
