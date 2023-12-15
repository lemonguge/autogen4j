package cn.homj.autogen4j;

import java.util.List;

import cn.homj.autogen4j.support.Client;
import cn.homj.autogen4j.support.LogUtils;
import cn.homj.autogen4j.support.Message;
import cn.homj.autogen4j.support.dashscope.qwen.GenerationRequest;
import cn.homj.autogen4j.support.dashscope.qwen.GenerationResponse;
import lombok.Setter;
import lombok.experimental.Accessors;

import static cn.homj.autogen4j.support.Message.ofAssistant;

/**
 * @author jiehong.jh
 * @date 2023/11/28
 */
@Setter
@Accessors(fluent = true)
public class QianWenAgent extends Agent {

    private Client client;
    private String model;
    private String apiKey;
    private Float topP;
    private Integer topK;
    private Integer seed;
    private Float temperature;
    private List<String> stop;
    private String systemMessage = "You are a helpful assistant.";

    public QianWenAgent(String name) {
        super(name);
    }

    @Override
    protected AgentRecord generateReply(Agent sender, AgentRecord record) {
        GenerationRequest request = new GenerationRequest()
            .setModel(model)
            .setResultFormat("message")
            .setTopP(topP)
            .setTopK(topK)
            .setSeed(seed)
            .setTemperature(temperature)
            .setStop(stop)
            .addSystemMessage(systemMessage);
        List<AgentRecord> c = conversations.get(sender.name);
        if (sender.name.equals(record.getName())) {
            c.forEach(e -> request.addMessage(e.getMessage()));
        } else {
            // in group chat
            StringBuilder buf = new StringBuilder();
            c.forEach(e -> buf.append(e.getName())
                .append(": ").append(e.getMessage().getContent()).append("\n"));
            buf.append(name).append(": ");
            request.addUserMessage(buf.toString());
        }
        GenerationResponse response;
        try {
            response = client.generate(apiKey, request);
        } catch (Exception e) {
            LogUtils.error(loggerName, "response failed", e);
            return AgentRecord.of(name, ofAssistant("Service interrupted."));
        }
        Message message;
        if (response.isSuccess()) {
            message = response.getOutput().getChoices().get(0).getMessage();
        } else {
            LogUtils.warn(loggerName, response.toString());
            message = ofAssistant("Service temporarily unavailable.");
        }
        return AgentRecord.of(name, message);
    }

    public GroupChatManager manager() {
        return newManager(name);
    }

    public GroupChatManager newManager(String name) {
        return new QianWenManager(name);
    }

    public class QianWenManager extends GroupChatManager {

        public QianWenManager(String name) {
            super(name);
        }

        @Override
        protected Agent selectNext() {
            GenerationRequest request = new GenerationRequest()
                .setModel(model)
                .setResultFormat("text")
                .setTopP(topP)
                .setTopK(topK)
                .setSeed(seed)
                .setTemperature(temperature)
                .setStop(stop)
                .addSystemMessage(groupChat.selectSpeakerMessage());
            StringBuilder buf = new StringBuilder();
            List<AgentRecord> c = groupChat.conversation();
            c.forEach(e -> buf.append(e.getName())
                .append(": ").append(e.getMessage().getContent()).append("\n"));
            request.addUserMessage(buf.toString());
            GenerationResponse response;
            try {
                response = client.generate(apiKey, request);
            } catch (Exception e) {
                LogUtils.error(loggerName, "response failed", e);
                return null;
            }
            if (response.isSuccess()) {
                return groupChat.getAgent(response.getOutput().getText());
            }
            LogUtils.warn(loggerName, response.toString());
            return null;
        }
    }
}
