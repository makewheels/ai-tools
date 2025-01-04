package com.github.makewheels.aitools.gpt.session;

import com.github.makewheels.aitools.gpt.service.GptConstants;
import com.github.makewheels.aitools.gpt.service.Message;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Session {
    private String model = GptConstants.MODEL;
    private List<Message> messages = new ArrayList<>();
}
