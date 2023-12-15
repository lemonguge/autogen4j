package cn.homj.autogen4j.support.dashscope.qwen;

import java.util.List;

import com.alibaba.fastjson2.annotation.JSONField;

import cn.homj.autogen4j.support.Message;
import cn.homj.autogen4j.support.dashscope.ErrorResponse;
import lombok.Data;

/**
 * @author jiehong.jh
 * @date 2023/5/24
 */
@Data
public class GenerationResponse implements ErrorResponse {
    /**
     * 请求 ID
     */
    @JSONField(name = "request_id")
    private String requestId;
    /**
     * 错误码
     */
    @JSONField(name = "code")
    private String errorCode;
    /**
     * 错误信息
     */
    @JSONField(name = "message")
    private String errorMessage;
    /**
     * 结果信息
     */
    private Output output;
    /**
     * 计量信息
     */
    private Usage usage;

    /**
     * 是否成功
     *
     * @return
     */
    public boolean isSuccess() {
        return errorCode == null;
    }

    @Data
    public static class Output {
        /**
         * 模型生成回复
         * <p>
         * result_format: text
         */
        private String text;
        /**
         * result_format: text
         */
        @JSONField(name = "finish_reason")
        private String finishReason;
        /**
         * result_format: message
         */
        private List<Choice> choices;
    }

    @Data
    public static class Choice {
        private Message message;
        @JSONField(name = "finish_reason")
        private String finishReason;
    }

    @Data
    public static class Usage {
        /**
         * 用户输入文本转换成 Token 后的长度
         */
        @JSONField(name = "input_tokens")
        private Integer inputTokens;
        /**
         * 模型生成回复转换为 Token 后的长度
         */
        @JSONField(name = "output_tokens")
        private Integer outputTokens;
    }
}
