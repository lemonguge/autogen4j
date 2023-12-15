package cn.homj.autogen4j;

import cn.homj.autogen4j.support.Message;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 对话记录
 *
 * @author jiehong.jh
 * @date 2023/11/28
 */
@Data
@Accessors(chain = true)
public class AgentRecord {
    /**
     * The name of the agent.
     */
    private String name;
    private Message message;
    /**
     * 执行确认，仅消息的角色为 tool 时有效
     */
    private boolean confirmed = true;

    public static AgentRecord of(String name, Message message) {
        return new AgentRecord().setName(name).setMessage(message);
    }
}
