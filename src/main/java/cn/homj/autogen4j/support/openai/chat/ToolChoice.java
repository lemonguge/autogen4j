package cn.homj.autogen4j.support.openai.chat;

import cn.homj.autogen4j.support.openai.chat.tool.Function;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author jiehong.jh
 * @date 2023/11/27
 */
@Data
@Accessors(chain = true)
public class ToolChoice {
    /**
     * The model can pick between generating a message or calling a function.
     */
    public static ToolChoice AUTO = new ToolChoice();
    /**
     * The model will not call a function and instead generates a message.
     */
    public static ToolChoice NONE = new ToolChoice();
    /**
     * The type of the tool.
     */
    private String type;
    /**
     * Force the model to call that function.
     */
    private Function function;

    public static ToolChoice function(String name) {
        return new ToolChoice().setType("function").setFunction(new Function().setName(name));
    }
}
