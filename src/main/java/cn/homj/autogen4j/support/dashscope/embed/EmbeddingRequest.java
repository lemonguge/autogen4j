package cn.homj.autogen4j.support.dashscope.embed;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author jiehong.jh
 * @date 2023/6/14
 */
@Data
@Accessors(chain = true)
public class EmbeddingRequest {
    /**
     * 模型
     * <p>
     * eg: text-embedding-v1
     */
    private String model;
    /**
     * 文本
     */
    private List<String> texts;
    /**
     * 文本类型
     * <ul>
     *     <li>query</li>
     *     <li>document</li>
     * </ul>
     */
    private String textType;

    public EmbeddingRequest addText(String text) {
        if (texts == null) {
            texts = new ArrayList<>();
        }
        texts.add(text);
        return this;
    }
}
