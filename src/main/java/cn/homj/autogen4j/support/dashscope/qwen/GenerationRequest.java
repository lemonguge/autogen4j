package cn.homj.autogen4j.support.dashscope.qwen;

import java.util.ArrayList;
import java.util.List;

import cn.homj.autogen4j.support.Message;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author jiehong.jh
 * @date 2023/5/24
 */
@Data
@Accessors(chain = true)
public class GenerationRequest {
    /**
     * 模型
     * <p>
     * eg: qwen-turbo, qwen-plus, qwen-max
     */
    private String model;
    /**
     * 用户与模型的对话历史
     */
    private List<Message> messages;
    /**
     * <ul>
     *     <li>text</li>
     *     <li>message</li>
     * </ul>
     */
    private String resultFormat;
    /**
     * 0 < topP < 1.0
     * <p>
     * 取值越大，生成的随机性越高；取值越低，生成的确定性越高。
     */
    private Float topP;
    /**
     * 采样候选集的大小
     * <p>
     * 取值越大，生成的随机性越高；取值越小，生成的确定性越高。
     * <p>
     * 默认值为 0，表示不启用 topK 策略，此时，仅有 topP 策略生效。
     */
    private Integer topK;
    /**
     * 随机数的种子
     * <p>
     * 如果使用相同的种子，每次运行生成的结果都将相同。
     */
    private Integer seed;
    /**
     * 0 < temperature < 2
     * <p>
     * 控制随机性和多样性的程度
     * <p>
     * 较高的 temperature 值会降低概率分布的峰值，使得更多的低概率词被选择，生成结果更加多样化；
     * <p>
     * 较低的 temperature 值会增强概率分布的峰值，使得高概率词更容易被选择，生成结果更加确定。
     */
    private Float temperature;
    /**
     * 控制生成时遇到某些内容则停止
     */
    private List<String> stop;
    /**
     * 是否参考夸克搜索的结果，默认为 false
     * <p>
     * 注意：打开搜索并不意味着一定会使用搜索结果；如果打开搜索，模型会将搜索结果作为 prompt，进而“自行判断”是否生成结合搜索结果的文本。
     */
    private Boolean enableSearch;
    /**
     * 控制流式输出模式
     */
    private Boolean incrementalOutput;

    public GenerationRequest addMessage(Message message) {
        if (messages == null) {
            messages = new ArrayList<>();
        }
        messages.add(message);
        return this;
    }

    public GenerationRequest addSystemMessage(String content) {
        return addMessage(Message.ofSystem(content));
    }

    public GenerationRequest addUserMessage(String content) {
        return addMessage(Message.ofUser(content));
    }

    public GenerationRequest addAssistantMessage(String content) {
        return addMessage(Message.ofAssistant(content));
    }
}
