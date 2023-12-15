package cn.homj.autogen4j;

import cn.homj.autogen4j.support.Client;

import static cn.homj.autogen4j.Definition.enableOpenAiProxy;
import static cn.homj.autogen4j.Definition.openAiApiKey;
import static cn.homj.autogen4j.Definition.openAiProxyCompletionUrl;

/**
 * @author jiehong.jh
 * @date 2023/12/1
 */
public class GroupChatManagerTest2 {

    public static void main(String[] args) {
        Client client = new Client();
        if (enableOpenAiProxy) {
            client.setCompletionUrl(openAiProxyCompletionUrl);
        }

        UserProxyAgent user = new UserProxyAgent("Hom J.");
        OpenAiAgent taylor = new OpenAiAgent("Taylor");
        OpenAiAgent jarvis = new OpenAiAgent("Jarvis");

        GroupChat groupChat = new GroupChat()
            .addAgent(user, "正在寻求帮助的用户")
            .addAgent(taylor, "温柔的知心姐姐，当你遇到问题时，总是会给予鼓励和支持。")
            .addAgent(jarvis, "旅游达人，可以提供目的地建议、行程规划。");
        GroupChatManager manager = taylor.newManager("manager").groupChat(groupChat);

        taylor
            .client(client)
            .model("gpt-3.5-turbo")
            .apiKey(openAiApiKey)
            .temperature(0.01)
            .systemMessage("你是我的知心姐姐，无论我遇到什么问题，你会耐心倾听并给予鼓励和支持。");

        jarvis
            .client(client)
            .model("gpt-3.5-turbo")
            .apiKey(openAiApiKey)
            .temperature(0.01)
            .systemMessage("你是一名旅游达人，可以提供目的地建议、行程规划。");

        user.initChat(manager, "你好");
        System.exit(0);
    }
}
