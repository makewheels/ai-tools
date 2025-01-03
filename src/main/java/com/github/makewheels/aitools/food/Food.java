package com.github.makewheels.aitools.food;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document("food")
public class Food {
    @Id
    private String id;
    @Indexed
    private String userId;

    private String prompt;
    private String originalImageFileId;
    private String result;

    private String status;
    private Date createTime;
    private Date startTime;
    private Date finishTime;

    public Food() {
        createTime = new Date();
        status = TaskStatus.CREATED;
    }
}
