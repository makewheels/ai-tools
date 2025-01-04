package com.github.makewheels.aitools.argue;

import com.github.makewheels.aitools.gpt.service.GptConstants;
import com.github.makewheels.aitools.gpt.service.GptService;
import com.github.makewheels.aitools.gpt.service.Message;
import com.github.makewheels.aitools.gpt.service.Role;
import com.github.makewheels.aitools.gpt.session.Session;
import com.github.makewheels.aitools.gpt.session.SessionService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ArgueService {
    @Resource
    private SessionService sessionService;
    @Resource
    private GptService gptService;

    private Message getSystemMessage(Argue argue, String side) {
        String content = """
                你正在参加一场辩论会赛，用户的输入就是对方的观点，你直接回复内容就行了，不要说别的。
                要尽可能有理有据有节的说服对方，让辩论赛尽可能精彩，有看点。
                你每次回答要尽可能简短，不要特别长，不要超过130字。
                这场辩论赛的主题是：%s
                正方观点是：%s
                反方观点是：%s
                你扮演的是：%s
                """;
        String sideArgument = side.equals(ArgueSide.POSITIVE_SIDE) ? argue.getPositiveArgument() : argue.getNegativeArgument();
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

    /**
     * 获取评委结论
     */
    private String getComment(List<String> conversation) {
        String prompt = """
                这是一场辩论赛的正反方发言记录，你作为评委请给出评价，并决定哪一方胜利，并说明理由。
                你的回复要尽可能剪短，直接回复内容就行了，不要说别的
                %s
                """;
        prompt = String.format(prompt, String.join("\n\n", conversation));
        return gptService.completionWithSimpleContent(prompt);
    }

    public void argue() {
        Argue argue = this.getArgue();
        Session positiveSession = this.createSession(argue, ArgueSide.POSITIVE_SIDE);
        Session negativeSession = this.createSession(argue, ArgueSide.NEGATIVE_SIDE);

        List<String> conversation = new ArrayList<>();
        conversation.add("本场辩论赛的主题是：" + argue.getTopic());
        conversation.add("正方观点：" + argue.getPositiveArgument());
        conversation.add("反方观点：" + argue.getNegativeArgument());
        conversation.add("Let's start the debate!");

        String positiveResponse;
        String negativeResponse = null;
        for (int i = 0; i < 3; i++) {
            positiveResponse = sessionService.request(positiveSession, negativeResponse);
            conversation.add(ArgueSide.POSITIVE_SIDE + "发言" + argue.getPositiveArgument() + ": " + positiveResponse);
            negativeResponse = sessionService.request(negativeSession, positiveResponse);
            conversation.add(ArgueSide.NEGATIVE_SIDE + "发言" + argue.getNegativeArgument() + ": " + negativeResponse);
        }

        // 获取评委结论
        String comment = this.getComment(conversation);
        conversation.add("评委结论: " + comment);

        for (int i = 0; i < 3; i++) {
            log.info("===========================");
        }

        System.out.println();
        System.out.println();
        conversation.forEach(System.out::println);
        System.out.println();
        System.out.println();

        // 统计所有字数
        int totalCharacters = 0;
        for (String s : conversation) {
            totalCharacters += s.length();
        }
        log.info("总字数: " + totalCharacters);
    }

}
