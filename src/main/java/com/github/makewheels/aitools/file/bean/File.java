package com.github.makewheels.aitools.file.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.makewheels.aitools.file.constants.FileStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@Setter
@Document("file")
@JsonIgnoreProperties("objectInfo")
public class File {
    @Id
    private String id;
    @Indexed
    private String uploaderId;

    private String fileStatus;

    @Indexed
    private String filename;
    private String fileType;

    @Indexed
    private String key;
    private String extension;
    @Indexed
    private Long size;
    private String storageClass;

    @Indexed
    private Date createTime;
    @Indexed
    private Date uploadTime;

    public File() {
        fileStatus = FileStatus.CREATED;
        createTime = new Date();
    }

}
