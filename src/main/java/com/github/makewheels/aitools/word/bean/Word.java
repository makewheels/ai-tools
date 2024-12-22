package com.github.makewheels.aitools.word.bean;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Word {
    private String word;
    private String pronunciation;
    private List<Meaning> meanings;
}
