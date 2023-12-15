package cn.homj.autogen4j;

import java.util.ArrayList;
import java.util.List;

import cn.homj.autogen4j.support.Client;
import cn.homj.autogen4j.support.LogUtils;
import cn.homj.autogen4j.support.Message;
import cn.homj.autogen4j.support.openai.chat.CompletionRequest;
import cn.homj.autogen4j.support.openai.chat.CompletionResponse;
import cn.homj.autogen4j.support.openai.chat.Tool;
import cn.homj.autogen4j.support.openai.chat.ToolCall;
import cn.homj.autogen4j.support.openai.chat.tool.Function;
import cn.homj.autogen4j.support.openai.chat.tool.FunctionCall;
import lombok.Setter;
import lombok.experimental.Accessors;

import static cn.homj.autogen4j.support.Message.ofAssistant;
import static cn.homj.autogen4j.support.Message.ofSystem;
import static cn.homj.autogen4j.support.Message.ofTool;

/**
 * @author jiehong.jh
 * @date 2023/11/29
 */
@Setter
@Accessors(fluent = true)
public class OpenAiAgent extends Agent {

    private Client client;
    private String model;
    private String apiKey;
    private Integer maxTokens;
    private Integer n = 1;
    private List<String> stop;
    private Double temperature;
    private Double topP;
    private List<Tool> tools;
    private String systemMessage = "You are a helpful assistant.";
    private String unconfirmedMessage = "Anything else I can do for you?";

    public OpenAiAgent(String name) {
        super(name);
    }

    public OpenAiAgent addFunction(String name) {
        AgentFunction function = toolkit.getFunction(name);
        if (function == null) {
            throw new IllegalArgumentException("Function is not in toolkit: " + name);
        }
        if (tools == null) {
            tools = new ArrayList<>();
        }
        tools.add(Tool.of(new Function()
            .setName(name)
            .setDescription(function.description())
            .setParameters(function.parameters())));
        return this;
    }

    @Override
    protected AgentRecord generateReply(Agent sender, AgentRecord record) {
        while (true) {
            CompletionRequest request = new CompletionRequest()
                .setModel(model)
                .setMaxTokens(maxTokens)
                .setN(n)
                .setStop(stop)
                .setTemperature(temperature)
                .setTopP(topP)
                .setTools(tools);
            List<AgentRecord> c = conversations.get(sender.name);
            if (sender.name.equals(record.getName())) {
                request.addSystemMessage(systemMessage);
                c.forEach(e -> request.addMessage(e.getMessage()));
            } else {
                // in group chat
                request.addMessage(ofSystem(systemMessage).setName(name));
                c.stream()
                    .map(e -> e.getMessage().clone().setName(e.getName()))
                    .forEach(request::addMessage);
            }
            CompletionResponse response;
            try {
                response = client.complete(apiKey, request);
            } catch (Exception e) {
                LogUtils.error(loggerName, "response failed", e);
                return AgentRecord.of(name, ofAssistant("Service interrupted."));
            }
            if (!response.isSuccess()) {
                LogUtils.warn(loggerName, response.getError().toString());
                return AgentRecord.of(name, ofAssistant("Service temporarily unavailable."));
            }
            Message message = response.getChoices().get(0).getMessage();
            List<ToolCall> toolCalls = message.getToolCalls();
            if (toolCalls == null || toolCalls.isEmpty()) {
                return AgentRecord.of(name, message);
            }
            addConversation(sender, AgentRecord.of(name, message));
            if (callTools(sender, toolCalls) == 0) {
                return AgentRecord.of(name, ofAssistant(unconfirmedMessage));
            }
        }
    }

    private int callTools(Agent sender, List<ToolCall> toolCalls) {
        int numberOfConfirmed = 0;
        for (ToolCall toolCall : toolCalls) {
            // only function is supported
            FunctionCall functionCall = toolCall.getFunctionCall();
            String functionName = functionCall.getName();
            AgentFunction function = toolkit.getFunction(functionName);
            if (function == null) {
                numberOfConfirmed++;
                LogUtils.info(loggerName, "function does not exist: {}", functionName);
                Message message = ofTool("Function does not exist.", toolCall.getId());
                addConversation(sender, AgentRecord.of(name, message));
                continue;
            }
            if (function.isPermit(this)) {
                AgentRecord record = runToolCall(toolCall);
                if (record.isConfirmed()) {
                    numberOfConfirmed++;
                }
                addConversation(sender, record);
                continue;
            }
            List<Agent> permitted = function.permitted();
            if (permitted == null || permitted.isEmpty()) {
                numberOfConfirmed++;
                LogUtils.info(loggerName, "unable to execute function: {}", functionName);
                Message message = ofTool("Unable to execute function.", toolCall.getId());
                addConversation(sender, AgentRecord.of(name, message));
            } else {
                Agent agent = permitted.get(0);
                AgentRecord record = agent.runToolCall(toolCall);
                if (record.isConfirmed()) {
                    numberOfConfirmed++;
                }
                addConversation(sender, record);
            }
        }
        return numberOfConfirmed;
    }

    public GroupChatManager manager() {
        return newManager(name);
    }

    public GroupChatManager newManager(String name) {
        return new OpenAiManager(name);
    }

    public class OpenAiManager extends GroupChatManager {

        public OpenAiManager(String name) {
            super(name);
        }

        @Override
        protected Agent selectNext() {
            CompletionRequest request = new CompletionRequest()
                .setModel(model)
                .setMaxTokens(maxTokens)
                .setN(1)
                .setStop(stop)
                .setTemperature(temperature)
                .setTopP(topP)
                .addSystemMessage(groupChat.selectSpeakerMessage());
            List<AgentRecord> c = groupChat.conversation();
            c.stream()
                .map(e -> e.getMessage().clone().setName(e.getName()))
                .forEach(request::addMessage);
            request.addUserMessage("Read the above conversation. Then select the next role.");
            CompletionResponse response;
            try {
                response = client.complete(apiKey, request);
            } catch (Exception e) {
                LogUtils.error(loggerName, "response failed", e);
                return null;
            }
            if (response.isSuccess()) {
                Message message = response.getChoices().get(0).getMessage();
                return groupChat.getAgent(message.getContent());
            }
            LogUtils.warn(loggerName, response.getError().toString());
            return null;
        }
    }
}
