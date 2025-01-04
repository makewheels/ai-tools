package com.github.makewheels.aitools.gpt.session;

import com.alibaba.fastjson.JSON;
import com.github.makewheels.aitools.gpt.service.GptService;
import com.github.makewheels.aitools.gpt.service.Message;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
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

    public String request(Session session) {
        return gptService.completion(JSON.toJSONString(session));
    }

    public String addAndRequest(Session session, Message message) {
        this.add(session, message);
        return this.request(session);
    }

}
