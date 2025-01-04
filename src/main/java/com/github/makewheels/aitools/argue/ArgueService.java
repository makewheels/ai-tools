package com.github.makewheels.aitools.argue;

import com.github.makewheels.aitools.gpt.service.GptConstants;
import com.github.makewheels.aitools.gpt.service.Message;
import com.github.makewheels.aitools.gpt.service.Role;
import com.github.makewheels.aitools.gpt.session.Session;
import com.github.makewheels.aitools.gpt.session.SessionService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ArgueService {
    @Resource
    private SessionService sessionService;

    private Message getSystemMessage(Argue argue, String side) {
        String content = """
                你正在参加一场辩论会赛，用户的输入就是对方的观点，你要尽可能说服对方。
                这场辩论赛的主题是：%s
                正方观点是：%s
                反方观点是：%s
                你扮演的是：%s
                你每次回答要尽可能简短，不要特别长，每次回复控制在70字以内。
                不要总是基于对方的内容回复，要引入新内容，让辩论赛尽可能精彩，有看点。
                """;
        String sideArgument = StringUtils.equals(side, ArgueSide.POSITIVE_SIDE) ? argue.getPositiveArgument() : argue.getNegativeArgument();
        side = side + ", " + sideArgument;
        Message message = new Message();
        message.setRole(Role.SYSTEM);
        message.setContent(String.format(content, argue.getTopic(),
                argue.getPositiveArgument(), argue.getNegativeArgument(), side));
        return message;
    }

    public Argue getArgue() {
        Argue argue = new Argue();
        argue.setTopic("地球是平的还是圆的？");
        argue.setPositiveArgument("地球是平的。");
        argue.setNegativeArgument("地球是圆的。");
        return argue;
    }

    private Session createSession(Argue argue, String side) {
        Session session = new Session();
        session.setModel(GptConstants.MODEL);
        Message systemMessage = this.getSystemMessage(argue, side);
        sessionService.add(session, systemMessage);
        return session;
    }

    public void argue() {
        Argue argue = this.getArgue();
        Session positiveSession = this.createSession(argue, ArgueSide.POSITIVE_SIDE);
        Session negativeSession = this.createSession(argue, ArgueSide.NEGATIVE_SIDE);

        List<String> conversation = new ArrayList<>();

        String positiveResponse;
        String negativeResponse = null;
        for (int i = 0; i < 10; i++) {
            positiveResponse = sessionService.request(positiveSession, negativeResponse);
            conversation.add(ArgueSide.POSITIVE_SIDE + ": " + positiveResponse);
            negativeResponse = sessionService.request(negativeSession, positiveResponse);
            conversation.add(ArgueSide.NEGATIVE_SIDE + ": " + negativeResponse);
        }

        conversation.forEach(log::info);
    }

}
