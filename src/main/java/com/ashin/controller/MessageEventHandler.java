package com.ashin.controller;

import com.ashin.util.BotUtil;
import com.ashin.util.OpenAiUtils;
import com.theokanning.openai.completion.CompletionChoice;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.ListenerHost;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.SingleMessage;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.List;

/**
 * 事件处理
 *
 * @author ashinnotfound
 * @date 2023/2/1
 */
@Component
public class MessageEventHandler implements ListenerHost {


    /**
     * 监听消息并把ChatGPT的回答发送到对应qq/群
     * 注：如果是在群聊则需@
     *
     * @param event 事件 ps:此处是MessageEvent 故所有的消息事件都会被监听
     */
    @EventHandler
    public void onMessage(@NotNull MessageEvent event) {
        System.out.println("监听到消息");
        if(event.getBot().getGroups().contains(event.getSubject().getId())) {
            //如果是在群聊
            //遍历收到的消息元素
            for (SingleMessage singleMessage : event.getMessage()) {
                if (singleMessage.equals(new At(event.getBot().getId()))) {
                    //存在@机器人的消息就向ChatGPT提问
                    //去除@再提问
                    String problem = event.getMessage().contentToString().replace("@" + event.getBot().getId(), "").trim();
                    StringBuilder answer = new StringBuilder();
                    System.out.println("开始提问:"+problem);
                    List<CompletionChoice> questionAnswer = OpenAiUtils.getQuestionAnswer(problem);
                    for (CompletionChoice completionChoice : questionAnswer) {
                        answer.append(completionChoice.getText());
                    }
                    System.out.println("回答:"+answer);
                    event.getSubject().sendMessage(answer.toString());
                    break;
                }
            }
        }else {
            //不是在群聊 则直接回复
            String problem = event.getMessage().contentToString().trim();
            System.out.println("开始提问:"+problem);
            StringBuilder answer = new StringBuilder();
            List<CompletionChoice> questionAnswer = OpenAiUtils.getQuestionAnswer(problem);
            for (CompletionChoice completionChoice : questionAnswer) {
                answer.append(completionChoice.getText());
            }
            System.out.println("回答:"+answer);
            event.getSubject().sendMessage(answer.toString());
        }
    }
}