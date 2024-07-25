package com.github.makewheels.aitools.gpt;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
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
        String response = HttpUtil.createPost("https://api.claudeshop.top/v1/chat/completions")
                .body(body).execute().body();
        return JSONObject.parseObject(response);
    }
}
