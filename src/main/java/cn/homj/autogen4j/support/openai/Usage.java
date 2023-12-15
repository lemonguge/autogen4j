package cn.homj.autogen4j.support.openai;

import com.alibaba.fastjson2.annotation.JSONField;

import lombok.Data;

/**
 * The OpenAI resources used by a request
 *
 * @author jiehong.jh
 * @date 2023/11/22
 */
@Data
public class Usage {
    /**
     * The number of prompt tokens used.
     */
    @JSONField(name = "prompt_tokens")
    private Long promptTokens;
    /**
     * The number of completion tokens used.
     */
    @JSONField(name = "completion_tokens")
    private Long completionTokens;
    /**
     * The number of total tokens used
     */
    @JSONField(name = "total_tokens")
    private Long totalTokens;
}
