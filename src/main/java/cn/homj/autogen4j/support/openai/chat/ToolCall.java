package cn.homj.autogen4j.support.openai.chat;

import com.alibaba.fastjson2.annotation.JSONField;

import cn.homj.autogen4j.support.openai.chat.tool.FunctionCall;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author jiehong.jh
 * @date 2023/11/27
 */
@Data
@Accessors(chain = true)
public class ToolCall {
    /**
     * This index of this completion in the returned list.
     */
    private Integer index;
    /**
     * The ID of the tool call.
     */
    private String id;
    /**
     * The type of the tool.
     */
    private String type;
    /**
     * The function that the model called.
     */
    @JSONField(name = "function")
    private FunctionCall functionCall;
}
