package com.github.makewheels.aitools.wechat;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import jakarta.annotation.Resource;
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
    @Resource
    private WechatAccessTokenService wechatAccessTokenService;
    @Resource
    private WechatCheckService wechatCheckService;

    /**
     * 根据openid获取用户信息
     * <a href="https://developers.weixin.qq.com/doc/offiaccount/User_Management/Get_users_basic_information_UnionID.html#UinonId">获取用户基本信息(UnionID机制)</a>
     */
    public JSONObject getUserInfoByOpenid(String openid) {
        String url = "https://api.weixin.qq.com/cgi-bin/user/info" +
                "?access_token=" + wechatAccessTokenService.getAccessToken()
                + "&openid=" + openid + "&lang=zh_CN";
        String response = HttpUtil.get(url);
        log.info("根据openid获取用户信息，openid = " + openid + ", 微信返回: " + response);
        Assert.isTrue(wechatCheckService.checkResponse(response),
                "根据openid获取用户信息异常，微信返回, response = " + response);
        return JSON.parseObject(response);
    }

    /**
     * oauth登录
     */
    public JSONObject oauth(String code) {
        log.info("oauth code: {}", code);
        String response = HttpUtil.get("https://api.weixin.qq.com/sns/oauth2/access_token" +
                "?appid=" + appId + "&secret=" + appSecret
                + "&code=" + code + "&grant_type=authorization_code");
        log.info("response: {}", response);
        return JSONObject.parseObject(response);
    }

}