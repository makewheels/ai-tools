package com.github.makewheels.aitools.word.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WordResponse {
    private String id;
    private String content;
    private String pronunciation;
    private List<MeaningResponse> meanings;
}
