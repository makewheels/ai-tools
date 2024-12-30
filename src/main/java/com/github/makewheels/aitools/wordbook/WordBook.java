package com.github.makewheels.aitools.wordbook;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@Setter
@Document("word_book")
public class WordBook {
    @Id
    private String id;

    @Indexed
    private String content;

    @Indexed
    private String userId;

    private Date createTime;
}
