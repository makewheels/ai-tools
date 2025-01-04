package com.github.makewheels.aitools.argue;

import com.github.makewheels.aitools.gpt.service.GptConstants;
import com.github.makewheels.aitools.gpt.service.Message;
import com.github.makewheels.aitools.gpt.service.Role;
import com.github.makewheels.aitools.gpt.session.Session;
import com.github.makewheels.aitools.gpt.session.SessionService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
                反方观点是：$s
                你扮演的是：%s
                你每次回答要尽可能简短，不要特别长，每次回复控制在100字以内
                不要总是基于对方的内容回复，要引入新内容，让辩论赛尽可能精彩，有看点
                """;

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

    private Session createPositiveSession(Argue argue) {
        Session positiveSession = new Session();
        positiveSession.setModel(GptConstants.MODEL);
        Message systemMessage = this.getSystemMessage(argue, ArgueSide.POSITIVE_SIDE);
        sessionService.add(positiveSession, systemMessage);
        return positiveSession;
    }

    private Session createNegativeSession(Argue argue) {
        Session negativeSession = new Session();
        negativeSession.setModel(GptConstants.MODEL);
        Message systemMessage = this.getSystemMessage(argue, ArgueSide.NEGATIVE_SIDE);
        sessionService.add(negativeSession, systemMessage);
        return negativeSession;
    }

    public void argue() {
        Argue argue = this.getArgue();

        Session positiveSession = this.createPositiveSession(argue);

        Session negativeSession = this.createNegativeSession(argue);



    }


}
