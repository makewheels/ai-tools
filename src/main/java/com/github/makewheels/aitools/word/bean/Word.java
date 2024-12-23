package com.github.makewheels.aitools.word.bean;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Document("word")
public class Word {
    @Id
    private String id;

    @Indexed
    private String content;
    private String pronunciation;
    private List<Meaning> meanings;

    private Date createTime;
}
