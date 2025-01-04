package com.github.makewheels.aitools.gpt.session;

import com.alibaba.fastjson.JSON;
import com.github.makewheels.aitools.gpt.service.GptService;
import com.github.makewheels.aitools.gpt.service.Message;
import com.github.makewheels.aitools.gpt.service.Role;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class SessionService {
    @Resource
    private GptService gptService;

    public void add(Session session, Message message) {
        List<Message> messages = session.getMessages();
        if (messages == null) {
            messages = new ArrayList<>();
        }
        messages.add(message);
        session.setMessages(messages);
    }

    private void addMessage(Session session, String role, String content) {
        if (StringUtils.isEmpty(content)) {
            return;
        }

        Message message = new Message();
        message.setRole(role);
        message.setContent(content);

        this.add(session, message);
    }

    public String request(Session session, String userContent) {
        this.addMessage(session, Role.USER, userContent);

        String assistantContent = gptService.completion(JSON.toJSONString(session));
        this.addMessage(session, Role.ASSISTANT, assistantContent);

        return assistantContent;
    }

}
