package cn.homj.autogen4j.support.openai.chat.tool;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author jiehong.jh
 * @date 2023/11/27
 */
@Data
@Accessors(chain = true)
public class Function {
    /**
     * The name of the function to be called.
     */
    private String name;
    /**
     * A description of what the function does, used by the model to choose when and how to call the function.
     */
    private String description;
    /**
     * The parameters the functions accepts, described as a JSON Schema object.
     * <p>
     * To describe a function that accepts no parameters, provide the value '{"type": "object", "properties": {}}'.
     */
    private Object parameters;
}
