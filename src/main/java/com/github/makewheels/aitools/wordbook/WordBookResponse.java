package com.github.makewheels.aitools.wordbook;

import com.github.makewheels.aitools.word.bean.Meaning;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WordBookResponse {
    private String id;

    private String content;

    private String pronunciation;
    private List<Meaning> meanings;

}
