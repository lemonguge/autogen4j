package cn.homj.autogen4j;

/**
 * @author jiehong.jh
 * @date 2022/12/8
 */
public class Definition {

    public static String qianWenApiKey;
    public static String embeddingApiKey;
    public static String openAiApiKey;
    /**
     * 启用 OpenAI 代理
     */
    public static boolean enableOpenAiProxy = false;
    /**
     * OpenAI 代理地址
     */
    public static String openAiProxyCompletionUrl;

    static {
        try {
            Class.forName("cn.homj.autogen4j.Initializer");
        } catch (Exception e) {
            System.err.println("Initializer not found");
        }
    }
}
