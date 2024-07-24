package com.github.makewheels.ai_tools.wechat;

import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;


/**
 * <a href="https://developers.weixin.qq.com/doc/offiaccount/Basic_Information/Get_access_token.html">获取access_token</a>
 */
@Service
@Slf4j
public class WechatAccessTokenService {
    private static String accessToken;
    private static Long expireAtInSeconds;

    @Value("${wechat.official-account.aivideo.app-id}")
    private String appId;
    @Value("${wechat.official-account.aivideo.app-secret}")
    private String appSecret;

    @Resource
    private WechatCheckService wechatCheckService;

    private boolean isExpired() {
        return DateUtil.currentSeconds() >= expireAtInSeconds;
    }

    public String getAccessToken() {
        // 请求过，并且没过期，直接返回缓存
        if (accessToken != null && !isExpired()) {
            return accessToken;
        }

        // 请求微信服务器
        String response = HttpUtil.get("https://api.weixin.qq.com/cgi-bin/token" +
                "?grant_type=client_credential&appid=" + appId + "&secret=" + appSecret);
        log.info("获取微信access_token，微信返回：" + response);
        Assert.isTrue(wechatCheckService.checkResponse(response),
                "获取微信access_token失败，response = " + response);

        JSONObject jsonObject = JSON.parseObject(response);
        accessToken = jsonObject.getString("access_token");
        expireAtInSeconds = DateUtil.currentSeconds() + jsonObject.getLong("expires_in") - 100;
        return accessToken;
    }
}
