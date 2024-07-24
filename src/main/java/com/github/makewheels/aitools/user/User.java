package com.github.makewheels.aitools.user;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document("user")
public class User {
    @Id
    private String id;

    @Indexed
    private String openid;
    @Indexed
    private String token;
    @Indexed
    private Date createTime;

    public User() {
        this.createTime = new Date();
    }

}
