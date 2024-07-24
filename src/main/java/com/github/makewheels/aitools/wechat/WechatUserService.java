package com.github.makewheels.aitools.wechat;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
@Slf4j
public class WechatUserService {
    @Value("${wechat.mini-program.ai-tools.app-id}")
    private String appId;
    @Value("${wechat.mini-program.ai-tools.app-secret}")
    private String appSecret;

    /**
     * 小程序登录
     * 通过接收前端的js_code换取openid
     * <a href="https://developers.weixin.qq.com/miniprogram/dev/OpenApiDoc/user-login/code2Session.html">小程序登录</a>
     */
    public JSONObject login(String code) {
        log.info("js_code = " + code);
        String response = HttpUtil.get("https://api.weixin.qq.com/sns/jscode2session" +
                "?appid=" + appId + "&secret=" + appSecret
                + "&js_code=" + code + "&grant_type=authorization_code");
        Assert.isTrue(WechatUtils.checkResponse(response),
                "获取微信access_token失败，response = " + response);
        log.info("response: {}", response);
        return JSONObject.parseObject(response);
    }

}