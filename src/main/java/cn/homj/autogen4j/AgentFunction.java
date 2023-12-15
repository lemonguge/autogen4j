package cn.homj.autogen4j;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author jiehong.jh
 * @date 2023/11/28
 */
@Data
@Accessors(fluent = true)
public class AgentFunction {
    /**
     * The name of the function.
     */
    private String name;
    private String description;
    private Object parameters;

    private Function<String, ?> parser;
    private Function<?, ?> action;

    private boolean permitAll;
    private List<Agent> permitted;

    public AgentFunction permit(Agent agent) {
        if (permitted == null) {
            permitted = new ArrayList<>();
        }
        permitted.add(agent);
        return this;
    }

    public boolean isPermit(Agent agent) {
        if (permitAll) {
            return true;
        }
        if (permitted == null) {
            return false;
        }
        return permitted.contains(agent);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public Object run(String input) {
        try {
            Object args;
            if (parser == null) {
                args = input;
            } else {
                args = parser.apply(input);
            }
            return ((Function)action).apply(args);
        } catch (Exception e) {
            return "Execution failed: " + e.getMessage();
        }
    }
}
