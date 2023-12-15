package cn.homj.autogen4j.support.openai.chat;

import java.util.List;

import com.alibaba.fastjson2.annotation.JSONField;

import cn.homj.autogen4j.support.openai.ErrorInfo;
import lombok.Data;

/**
 * @author jiehong.jh
 * @date 2023/11/23
 */
@Data
public class CompletionChunk {
    /**
     * A unique identifier for the chat completion.
     */
    private String id;
    /**
     * The model used for the chat completion.
     */
    private String model;
    /**
     * The type of object returned, should be "chat.completion"
     */
    private String object;
    /**
     * The Unix timestamp (in seconds) of when the chat completion was created.
     */
    private Long created;
    /**
     * This fingerprint represents the backend configuration that the model runs with.
     */
    @JSONField(name = "system_fingerprint")
    private String systemFingerprint;
    /**
     * A list of chat completion choices. Can be more than one if 'n' is greater than 1.
     */
    private List<Choice> choices;

    private ErrorInfo error;

    /**
     * 是否成功
     *
     * @return
     */
    public boolean isSuccess() {
        return error == null;
    }
}
