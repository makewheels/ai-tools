package com.github.makewheels.ai_tools.user;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document
public class User {
    @Id
    private String id;

    @Indexed
    private String openid;

    @Indexed
    private Date createTime;

    @Indexed
    private String token;

    public User() {
        this.createTime = new Date();
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
