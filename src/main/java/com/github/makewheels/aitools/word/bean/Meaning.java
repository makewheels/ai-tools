package com.github.makewheels.aitools.word.bean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Meaning {
    private String partOfSpeech;
    private String meaningChinese;
    private String exampleChinese;
    private String exampleEnglish;

    private String imagePrompt;

    private String imageUrl;
    private String imagePromptMd5;
    private String imageFilePath;

}