package cn.homj.autogen4j;

import java.util.List;

import cn.homj.autogen4j.support.LogUtils;
import cn.homj.autogen4j.support.Message;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author jiehong.jh
 * @date 2023/11/28
 */
@Setter
@Accessors(fluent = true)
public class GroupChatManager extends Agent {

    protected GroupChat groupChat;

    public GroupChatManager(String name) {
        super(name);
    }

    @Override
    protected AgentRecord generateReply(
        Agent sender, AgentRecord record) {
        int maxRound = groupChat.maxRound();
        for (int i = 0; i < maxRound; i++) {
            groupChat.addRecord(record);
            List<Agent> agents = groupChat.agents();
            int size = agents.size(), index = 0;
            for (int j = 0; j < size; j++) {
                Agent agent = agents.get(j);
                if (agent == sender) {
                    index = j;
                } else {
                    agent.receive(this, record, false);
                }
            }
            Agent speaker = selectNext();
            if (speaker == null) {
                if (index == size - 1) {
                    speaker = agents.get(0);
                } else {
                    speaker = agents.get(index + 1);
                }
            }
            AgentRecord reply = speaker.generateReply(this, record);
            if (reply == null) {
                break;
            }
            Message message = reply.getMessage();
            LogUtils.info(loggerName, "{}({}): {}", message.getRole(), speaker.name, message.getContent());
            speaker.addConversation(this, reply);
            receive(speaker, reply, false);
            // next round
            sender = speaker;
            record = reply;
        }
        return null;
    }

    protected Agent selectNext() {
        return null;
    }
}
