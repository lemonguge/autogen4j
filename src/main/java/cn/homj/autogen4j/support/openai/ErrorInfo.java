package cn.homj.autogen4j.support.openai;

import lombok.Data;

/**
 * @author jiehong.jh
 * @date 2023/11/23
 */
@Data
public class ErrorInfo {
    /**
     * eg: invalid_request_error
     */
    private String type;
    private String message;
    /**
     * eg: invalid_api_key
     */
    private String code;
    private String param;
}
