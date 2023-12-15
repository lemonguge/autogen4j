package cn.homj.autogen4j;

import cn.homj.autogen4j.support.Client;

import static cn.homj.autogen4j.Definition.enableOpenAiProxy;
import static cn.homj.autogen4j.Definition.openAiApiKey;
import static cn.homj.autogen4j.Definition.openAiProxyCompletionUrl;
import static cn.homj.autogen4j.support.AgentFunctions.BOOK_RESTAURANT;
import static cn.homj.autogen4j.support.AgentFunctions.GET_CURRENT_WEATHER;
import static cn.homj.autogen4j.support.AgentFunctions.bookRestaurant;
import static cn.homj.autogen4j.support.AgentFunctions.getCurrentWeather;

/**
 * @author jiehong.jh
 * @date 2023/11/30
 */
public class OpenAiAgentTest2 {

    public static void main(String[] args) {
        Client client = new Client();
        if (enableOpenAiProxy) {
            client.setCompletionUrl(openAiProxyCompletionUrl);
        }

        UserProxyAgent user = new UserProxyAgent("Hom J.");
        OpenAiAgent assistant = new OpenAiAgent("Jarvis");

        AgentToolkit toolkit = new AgentToolkit()
            .add(bookRestaurant().permit(user))
            .add(getCurrentWeather().permitAll(true));
        toolkit.register(user, assistant);

        assistant
            .client(client)
            .model("gpt-3.5-turbo-1106")
            .apiKey(openAiApiKey)
            .addFunction(BOOK_RESTAURANT)
            .addFunction(GET_CURRENT_WEATHER)
            .unconfirmedMessage("还有其他我可以帮您的吗？");

        user.initChat(assistant, "你好");
        System.exit(0);
    }
}
