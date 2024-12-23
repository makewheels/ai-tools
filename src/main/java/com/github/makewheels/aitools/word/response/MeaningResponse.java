package com.github.makewheels.aitools.word.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MeaningResponse {
    private String partOfSpeech;
    private String meaningChinese;
    private String exampleChinese;
    private String exampleEnglish;

    private String imageFileId;
    private String imageUrl;
}
