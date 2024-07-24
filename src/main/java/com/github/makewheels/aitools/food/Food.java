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

    public Food() {
        createTime = new Date();
        status = FoodStatus.CREATED;
    }

    private String status;
    private Date createTime;
    private Date uploadTime;
    private Date startAnalyseTime;
    private Date finishTime;
}
