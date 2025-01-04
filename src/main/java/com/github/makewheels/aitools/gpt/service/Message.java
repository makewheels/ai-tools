package com.github.makewheels.aitools.gpt.service;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Message {
    private String role;
    private Object content;
}
