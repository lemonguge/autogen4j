package cn.homj.autogen4j;

import java.util.Scanner;

import cn.homj.autogen4j.support.LogUtils;
import cn.homj.autogen4j.support.openai.chat.ToolCall;
import lombok.Setter;
import lombok.experimental.Accessors;

import static cn.homj.autogen4j.support.Message.ofUser;

/**
 * @author jiehong.jh
 * @date 2023/11/28
 */
@Setter
@Accessors(fluent = true)
public class UserProxyAgent extends Agent {

    private String exit = "exit";

    public UserProxyAgent(String name) {
        super(name);
    }

    public void initChat(Agent recipient, String content) {
        LogUtils.info(loggerName, "user({}): {}", name, content);
        AgentRecord record = AgentRecord.of(name, ofUser(content));
        addConversation(recipient, record);
        recipient.receive(this, record, true);
    }

    @Override
    protected AgentRecord runToolCall(ToolCall toolCall) {
        return runToolCall(toolCall, () -> {
            LogUtils.info(loggerName, "please type 'y' to agree or 'N' to refuse.");
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine().trim();
            while (!input.equals("y") && !input.equals("N")) {
                input = scanner.nextLine().trim();
            }
            return input.equals("y");
        });
    }

    @Override
    protected AgentRecord generateReply(
        Agent sender, AgentRecord record) {
        LogUtils.info(loggerName, "{}, please continue typing your input.", name);
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine().trim();
        while (input.length() == 0) {
            input = scanner.nextLine().trim();
        }
        if (exit.equals(input)) {
            return null;
        }
        return AgentRecord.of(name, ofUser(input));
    }
}
