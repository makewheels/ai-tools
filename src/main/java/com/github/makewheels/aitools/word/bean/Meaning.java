package com.github.makewheels.aitools.word.bean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Meaning {
    private String imagePrompt;
    private String exampleChinese;
    private String exampleEnglish;
    private String partOfSpeech;
    private String meaningChinese;
}