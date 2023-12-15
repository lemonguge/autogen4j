package cn.homj.autogen4j;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jiehong.jh
 * @date 2023/11/29
 */
public class AgentToolkit {

    private final Map<String, AgentFunction> functionMap = new HashMap<>();

    public void register(GroupChat groupChat) {
        groupChat.agents().forEach(agent -> agent.setToolkit(this));
    }

    public void register(Agent... agents) {
        for (Agent agent : agents) {
            agent.setToolkit(this);
        }
    }

    public AgentToolkit add(AgentFunction function) {
        functionMap.put(function.name(), function);
        return this;
    }

    public AgentFunction getFunction(String name) {
        return functionMap.get(name);
    }
}
