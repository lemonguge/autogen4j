package cn.homj.autogen4j.support.openai.chat;

import com.alibaba.fastjson2.annotation.JSONField;

import cn.homj.autogen4j.support.Message;
import lombok.Data;

/**
 * @author jiehong.jh
 * @date 2023/11/23
 */
@Data
public class Choice {
    /**
     * This index of this completion in the returned list.
     */
    private Integer index;
    /**
     * The assistant message or delta (when streaming) which was generated
     */
    @JSONField(alternateNames = "delta")
    private Message message;
    /**
     * The reason the model stopped generating tokens.
     * <ul>
     *     <li>stop: if the model hit a natural stop point or a provided stop sequence</li>
     *     <li>length: if the maximum number of tokens specified in the request was reached</li>
     *     <li>content_filter: if content was omitted due to a flag from our content filters</li>
     *     <li>tool_calls: if the model called a tool</li>
     * </ul>
     */
    @JSONField(name = "finish_reason")
    private String finishReason;
}
