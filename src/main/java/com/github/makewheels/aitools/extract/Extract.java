package com.github.makewheels.aitools.extract;

import com.github.makewheels.aitools.food.TaskStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Document("extract")
public class Extract {
    @Id
    private String id;

    private String userId;

    private String originalImageFileId;

    private List<String> resultWordList;

    private String status;
    private Date createTime;
    private Date startTime;
    private Date finishTime;

    public Extract() {
        createTime = new Date();
        status = TaskStatus.CREATED;
    }
}
