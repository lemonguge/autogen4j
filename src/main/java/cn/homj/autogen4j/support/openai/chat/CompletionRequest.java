package cn.homj.autogen4j.support.openai.chat;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson2.annotation.JSONField;

import cn.homj.autogen4j.support.Message;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author jiehong.jh
 * @date 2023/11/22
 */
@Data
@Accessors(chain = true)
public class CompletionRequest {
    /**
     * ID of the model to use.
     * <p>
     * eg: gpt-3.5-turbo, gpt-4
     */
    private String model;
    /**
     * A list of messages comprising the conversation so far.
     */
    private List<Message> messages;
    /**
     * The maximum number of tokens to generate in the chat completion.
     */
    @JSONField(name = "max_tokens")
    private Integer maxTokens;
    /**
     * How many chat completion choices to generate for each input message.
     */
    private Integer n;
    /**
     * An object specifying the format that the model must output.
     * <p>
     * Setting to '{"type": "json_object"}' enables JSON mode
     */
    @JSONField(name = "response_format")
    private ResponseFormat responseFormat;
    /**
     * Up to 4 sequences where the API will stop generating further tokens.
     */
    private List<String> stop;
    /**
     * Enable SSE.
     */
    private Boolean stream;
    /**
     * What sampling temperature to use, between 0 and 2.
     */
    private Double temperature;
    /**
     * An alternative to sampling with temperature, called nucleus sampling, where the model considers the results of
     * the tokens with top_p probability mass. So 0.1 means only the tokens comprising the top 10% probability mass are
     * considered.
     */
    @JSONField(name = "top_p")
    private Double topP;
    /**
     * A list of tools the model may call.
     */
    private List<Tool> tools;
    /**
     * Controls which (if any) function is called by the model. 'none' means the model will not call a function and
     * instead generates a message. 'auto' means the model can pick between generating a message or calling a function.
     * Specifying a particular function via '{"type: "function", "function": {"name": "my_function"}}' forces the model
     * to call that function.
     */
    @JSONField(name = "tool_choice")
    @Setter(AccessLevel.NONE)
    private Object toolChoice;
    /**
     * A unique identifier representing your end-user, which can help OpenAI to monitor and detect abuse.
     */
    private String user;

    public CompletionRequest addMessage(Message message) {
        if (messages == null) {
            messages = new ArrayList<>();
        }
        messages.add(message);
        return this;
    }

    public CompletionRequest addSystemMessage(String content) {
        return addMessage(Message.ofSystem(content));
    }

    public CompletionRequest addUserMessage(String content) {
        return addMessage(Message.ofUser(content));
    }

    public CompletionRequest addAssistantMessage(String content) {
        return addMessage(Message.ofAssistant(content));
    }

    public CompletionRequest addToolMessage(String result, String toolCallId) {
        return addMessage(Message.ofTool(result, toolCallId));
    }

    public CompletionRequest addTool(Tool tool) {
        if (tools == null) {
            tools = new ArrayList<>();
        }
        tools.add(tool);
        return this;
    }

    public CompletionRequest setToolChoice(ToolChoice toolChoice) {
        if (ToolChoice.AUTO == toolChoice) {
            this.toolChoice = "auto";
        } else if (ToolChoice.NONE == toolChoice) {
            this.toolChoice = "none";
        } else {
            this.toolChoice = toolChoice;
        }
        return this;
    }
}
