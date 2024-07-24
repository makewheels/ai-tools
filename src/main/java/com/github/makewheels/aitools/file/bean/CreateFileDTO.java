package com.github.makewheels.aitools.file.bean;

import lombok.Data;

@Data
public class CreateFileDTO {
    private String uploaderId;
    private String key;
    private String extension;
    private String fileType;
}
