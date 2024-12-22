package com.github.makewheels.aitools.word.bean;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;

@Getter
@Setter
public class Meaning {
    private String partOfSpeech;
    private String meaningChinese;
    private String exampleChinese;
    private String exampleEnglish;

    private String imagePrompt;
    private String imagePromptMd5;
    @Indexed
    private String imageFileId;
}