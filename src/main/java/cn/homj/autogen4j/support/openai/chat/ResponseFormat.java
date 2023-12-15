package cn.homj.autogen4j.support.openai.chat;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author jiehong.jh
 * @date 2023/11/27
 */
@Data
@Accessors(chain = true)
public class ResponseFormat {
    /**
     * eg: json_object
     */
    private String type;

    public static ResponseFormat ofText() {
        return new ResponseFormat().setType("text");
    }

    public static ResponseFormat ofJson() {
        return new ResponseFormat().setType("json_object");
    }
}
