package cn.homj.autogen4j;

import cn.homj.autogen4j.support.Client;

import static cn.homj.autogen4j.Definition.qianWenApiKey;

/**
 * @author jiehong.jh
 * @date 2023/11/29
 */
public class QianWenAgentTest {

    public static void main(String[] args) {
        UserProxyAgent user = new UserProxyAgent("Hom J.");
        QianWenAgent assistant = new QianWenAgent("Jarvis")
            .client(new Client())
            .model("qwen-plus")
            .apiKey(qianWenApiKey);

        user.initChat(assistant, "你好");
        System.exit(0);
    }
}
