package com.github.makewheels.aitools.wechat;

import com.alibaba.fastjson.JSONObject;

public class WechatUtils {
    public static boolean checkResponse(String response) {
        JSONObject jsonObject = JSONObject.parseObject(response);
        // 不存在errcode，或者有errcode并且等于0，则认为正常
        return !jsonObject.containsKey("errcode")
                || jsonObject.getInteger("errcode") == 0;
    }
}
