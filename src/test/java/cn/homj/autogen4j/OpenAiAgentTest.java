package cn.homj.autogen4j;

import cn.homj.autogen4j.support.Client;

import static cn.homj.autogen4j.Definition.enableOpenAiProxy;
import static cn.homj.autogen4j.Definition.openAiApiKey;
import static cn.homj.autogen4j.Definition.openAiProxyCompletionUrl;

/**
 * @author jiehong.jh
 * @date 2023/11/30
 */
public class OpenAiAgentTest {

    public static void main(String[] args) {
        Client client = new Client();
        if (enableOpenAiProxy) {
            client.setCompletionUrl(openAiProxyCompletionUrl);
        }

        UserProxyAgent user = new UserProxyAgent("Hom J.");
        OpenAiAgent assistant = new OpenAiAgent("Jarvis")
            .client(client)
            .model("gpt-4")
            .apiKey(openAiApiKey);

        user.initChat(assistant, "你好");
        System.exit(0);
    }
}
