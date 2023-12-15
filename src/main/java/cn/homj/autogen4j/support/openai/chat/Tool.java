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
public class Tool {
    /**
     * The type of the tool.
     */
    private String type;
    /**
     * The function that the model called.
     */
    private Function function;

    public static Tool of(Function function) {
        return new Tool().setType("function").setFunction(function);
    }
}
