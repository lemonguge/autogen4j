package cn.homj.autogen4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author jiehong.jh
 * @date 2023/11/28
 */
@Setter
@Getter
@Accessors(fluent = true)
public class GroupChat {

    private int maxRound = 100;
    private List<Agent> agents = new ArrayList<>();
    private List<AgentRecord> conversation = new ArrayList<>();
    private Map<String, String> descriptionOfAgent = new HashMap<>();

    public GroupChat addAgent(Agent agent, String description) {
        agents.add(agent);
        descriptionOfAgent.put(agent.getName(), description);
        return this;
    }

    public String getDescription(Agent agent) {
        return descriptionOfAgent.get(agent.getName());
    }

    public void addRecord(AgentRecord record) {
        conversation.add(record);
    }

    public Agent getAgent(String name) {
        for (Agent agent : agents) {
            if (agent.getName().equals(name)) {
                return agent;
            }
        }
        return null;
    }

    public String selectSpeakerMessage() {
        int size = agents.size();
        StringBuilder buf = new StringBuilder();
        buf.append("You are in a role play game. The following roles are available:\n");
        buf.append("```\n");
        List<String> c = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            Agent agent = agents.get(i);
            String name = agent.getName();
            c.add(name);
            buf.append(i + 1);
            buf.append(". ");
            buf.append(name);
            String description = getDescription(agent);
            if (description != null) {
                buf.append(": ");
                buf.append(description);
            }
            buf.append("\n");
        }
        buf.append("```\n");
        buf.append("Read the following conversation.\n");
        buf.append("Then select the next role from [");
        buf.append(String.join(", ", c));
        buf.append("] to play. Only return the role.");
        return buf.toString();
    }
}
