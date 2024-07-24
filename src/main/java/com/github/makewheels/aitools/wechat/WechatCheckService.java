package com.github.makewheels.aitools.wechat;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class WechatCheckService {
    public boolean checkResponse(String response) {
        JSONObject jsonObject = JSONObject.parseObject(response);
        // 不存在errcode，或者有errcode并且等于0，则认为正常
        return !jsonObject.containsKey("errcode")
                || jsonObject.getInteger("errcode") == 0;
    }
}
