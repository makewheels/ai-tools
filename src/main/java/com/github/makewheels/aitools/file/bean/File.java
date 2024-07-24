package com.github.makewheels.aitools.file.bean;

import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectMetadata;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.makewheels.aitools.file.constants.FileStatus;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FilenameUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@Setter
@Document
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

    public void setObjectInfo(OSSObject object) {
        key = object.getKey();
        ObjectMetadata metadata = object.getObjectMetadata();
        size = metadata.getContentLength();
        filename = FilenameUtils.getName(key);
        extension = FilenameUtils.getExtension(key);
        storageClass = metadata.getObjectStorageClass().toString();
        uploadTime = metadata.getLastModified();
    }

    public void setObjectInfo(OSSObjectSummary objectSummary) {
        key = objectSummary.getKey();
        size = objectSummary.getSize();
        filename = FilenameUtils.getName(key);
        extension = FilenameUtils.getExtension(key);
        storageClass = objectSummary.getStorageClass();
        uploadTime = objectSummary.getLastModified();
    }

}
