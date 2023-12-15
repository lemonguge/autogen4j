package cn.homj.autogen4j.support.dashscope.embed;

import java.util.List;

import com.alibaba.fastjson2.annotation.JSONField;

import cn.homj.autogen4j.support.dashscope.ErrorResponse;
import lombok.Data;

/**
 * @author jiehong.jh
 * @date 2023/6/14
 */
@Data
public class EmbeddingResponse implements ErrorResponse {
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
        private List<Embedding> embeddings;
    }

    @Data
    public static class Embedding {
        @JSONField(name = "text_index")
        private Integer index;
        /**
         * 向量维度 1536
         */
        @JSONField(name = "embedding")
        private float[] vector;
    }

    @Data
    public static class Usage {
        /**
         * 请求文本转换成 Token 后的长度
         */
        @JSONField(name = "total_tokens")
        private Integer totalTokens;
    }
}
