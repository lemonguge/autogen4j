package cn.homj.autogen4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson2.JSON;

import cn.homj.autogen4j.support.LogUtils;
import cn.homj.autogen4j.support.Message;
import cn.homj.autogen4j.support.openai.chat.ToolCall;
import cn.homj.autogen4j.support.openai.chat.tool.FunctionCall;
import lombok.Getter;

import static cn.homj.autogen4j.support.Message.ofTool;

/**
 * @author jiehong.jh
 * @date 2023/11/28
 */
public abstract class Agent {

    private static final AgentToolkit EMPTY = new AgentToolkit();

    protected String loggerName = getClass().getName();
    /**
     * The name of the agent.
     */
    @Getter
    protected final String name;
    protected final Map<String, List<AgentRecord>> conversations = new HashMap<>();
    protected AgentToolkit toolkit = EMPTY;

    public Agent(String name) {
        this.name = name;
    }

    void setToolkit(AgentToolkit toolkit) {
        this.toolkit = toolkit;
    }

    protected void addConversation(Agent agent, AgentRecord record) {
        List<AgentRecord> c = conversations.get(agent.name);
        if (c == null) {
            c = conversations.computeIfAbsent(agent.name, k -> new ArrayList<>());
        }
        c.add(record);
    }

    /**
     * Receive a record from another agent.
     *
     * @param sender
     * @param record
     * @param giveReply
     */
    public void receive(Agent sender, AgentRecord record, boolean giveReply) {
        Message message = record.getMessage();
        String role = message.getRole();
        if ("assistant".equals(role)) {
            List<ToolCall> toolCalls = message.getToolCalls();
            if (toolCalls != null && !toolCalls.isEmpty()) {
                throw new IllegalArgumentException("Unexpected assistant message");
            }
        } else if (!"user".equals(role)) {
            throw new IllegalArgumentException("Unexpected role: " + role);
        }
        addConversation(sender, record);
        if (giveReply) {
            AgentRecord reply = generateReply(sender, record);
            if (reply != null) {
                message = reply.getMessage();
                LogUtils.info(loggerName, "{}({}): {}", message.getRole(), name, message.getContent());
                addConversation(sender, reply);
                sender.receive(this, reply, true);
            }
        }
    }

    /**
     * Reply based on the conversation history and the sender.
     *
     * @param sender
     * @param record
     * @return
     */
    protected abstract AgentRecord generateReply(Agent sender, AgentRecord record);

    /**
     * Run tool.
     *
     * @param toolCall
     * @return
     */
    protected AgentRecord runToolCall(ToolCall toolCall) {
        return runToolCall(toolCall, Confirmation.ALWAYS_TRUE);
    }

    protected AgentRecord runToolCall(ToolCall toolCall, Confirmation confirmation) {
        String toolCallId = toolCall.getId();
        // only function is supported
        FunctionCall functionCall = toolCall.getFunctionCall();
        String functionName = functionCall.getName();
        String arguments = functionCall.getArguments();
        AgentFunction function = toolkit.getFunction(functionName);
        LogUtils.info(loggerName, "+-----------------------------------------");
        LogUtils.info(loggerName, "function: {}, arguments:\n{}", functionName, arguments);
        LogUtils.info(loggerName, "+-----------------------------------------");
        Message message;
        boolean confirmed = true;
        if (confirmation.get()) {
            Object result = function.run(arguments);
            if (result == null) {
                LogUtils.info(loggerName, "function executed.");
                message = ofTool("Function executed.", toolCallId);
            } else {
                String content;
                if (result instanceof String) {
                    content = (String)result;
                } else {
                    content = JSON.toJSONString(result);
                }
                message = ofTool(content, toolCallId);
                LogUtils.info(loggerName, "function execution result: {}", content);
            }
        } else {
            confirmed = false;
            LogUtils.info(loggerName, "[{}] refuses to execute.", name);
            message = ofTool("User refuses to execute.", toolCallId);
        }
        return AgentRecord.of(name, message).setConfirmed(confirmed);
    }
}
