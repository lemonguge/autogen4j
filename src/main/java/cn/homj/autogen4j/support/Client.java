package cn.homj.autogen4j.support;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson2.JSON;

import cn.homj.autogen4j.support.dashscope.ErrorResponse;
import cn.homj.autogen4j.support.dashscope.embed.EmbeddingRequest;
import cn.homj.autogen4j.support.dashscope.embed.EmbeddingResponse;
import cn.homj.autogen4j.support.dashscope.qwen.GenerationRequest;
import cn.homj.autogen4j.support.dashscope.qwen.GenerationResponse;
import cn.homj.autogen4j.support.openai.chat.CompletionRequest;
import cn.homj.autogen4j.support.openai.chat.CompletionResponse;
import lombok.Getter;
import lombok.Setter;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;

import static java.util.Collections.singletonMap;

/**
 * @author jiehong.jh
 * @date 2023/11/22
 */
public class Client {
    /**
     * application/json
     */
    private static final MediaType APPLICATION_JSON = MediaType.parse("application/json");
    /**
     * OpenAI API
     */
    private static final String OPEN_AI_URL = "https://api.openai.com/v1";
    /**
     * Create chat completion
     */
    private static final String COMPLETION_URL = OPEN_AI_URL + "/chat/completions";
    /**
     * DashScope API
     */
    private static final String DASH_SCOPE_URL = "https://dashscope.aliyuncs.com/api/v1/services";
    /**
     * 通义千问
     */
    private static final String QIAN_WEN_URL = DASH_SCOPE_URL + "/aigc/text-generation/generation";
    /**
     * 通用文本向量
     */
    private static final String EMBEDDING_URL = DASH_SCOPE_URL + "/embeddings/text-embedding/text-embedding";
    /**
     * Http Client
     */
    @Getter
    private final OkHttpClient client;
    @Setter
    @Getter
    private String qianWenUrl = QIAN_WEN_URL;
    @Setter
    @Getter
    private String embeddingUrl = EMBEDDING_URL;
    @Setter
    @Getter
    private String completionUrl = COMPLETION_URL;

    public Client() {
        this(new OkHttpClient.Builder().readTimeout(15, TimeUnit.SECONDS).build());
    }

    public Client(OkHttpClient client) {
        this.client = client;
    }

    private static void put(Map<String, Object> map, String key, Object value) {
        if (value == null) {
            return;
        }
        if (value instanceof Map) {
            if (!((Map<?, ?>)value).isEmpty()) {
                map.put(key, value);
            }
        } else if (value instanceof Collection) {
            if (!((Collection<?>)value).isEmpty()) {
                map.put(key, value);
            }
        } else {
            map.put(key, value);
        }
    }

    private static Request postRequest(GenerationRequest request, Builder builder) {
        Map<String, Object> input = new HashMap<>();
        put(input, "messages", request.getMessages());

        Map<String, Object> parameters = new HashMap<>();
        put(parameters, "incremental_output", request.getIncrementalOutput());
        put(parameters, "enable_search", request.getEnableSearch());
        put(parameters, "result_format", request.getResultFormat());
        put(parameters, "temperature", request.getTemperature());
        put(parameters, "top_p", request.getTopP());
        put(parameters, "top_k", request.getTopK());
        put(parameters, "seed", request.getSeed());
        put(parameters, "stop", request.getStop());

        Map<String, Object> data = new HashMap<>();
        put(data, "model", request.getModel());
        put(data, "input", input);
        put(data, "parameters", parameters);
        return builder.post(RequestBody.create(JSON.toJSONString(data), APPLICATION_JSON)).build();
    }

    /**
     * 流式输出
     *
     * @param apiKey
     * @param request
     * @param listener
     */
    public void stream(String apiKey, GenerationRequest request, EventSourceListener listener) {
        Builder builder = new Builder().url(qianWenUrl)
            .addHeader("Accept", "text/event-stream")
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer " + apiKey);
        EventSources.createFactory(client)
            .newEventSource(postRequest(request, builder), listener);
    }

    /**
     * 执行请求
     *
     * @param apiKey
     * @param request
     * @return
     */
    public GenerationResponse generate(String apiKey, GenerationRequest request) {
        Request.Builder builder = new Builder().url(qianWenUrl)
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer " + apiKey);
        return execute(postRequest(request, builder), GenerationResponse.class);
    }

    /**
     * 执行请求
     *
     * @param apiKey
     * @param request
     * @return
     */
    public EmbeddingResponse embed(String apiKey, EmbeddingRequest request) {
        Map<String, Object> data = new HashMap<>();
        data.put("model", request.getModel());
        data.put("input", singletonMap("texts", request.getTexts()));
        data.put("parameters", singletonMap("text_type", request.getTextType()));
        Request r = new Builder().url(embeddingUrl)
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer " + apiKey)
            .post(RequestBody.create(JSON.toJSONString(data), APPLICATION_JSON)).build();
        return execute(r, EmbeddingResponse.class);
    }

    private <T extends ErrorResponse> T execute(Request request, Class<T> clazz) {
        try (Response response = client.newCall(request).execute()) {
            if (response.code() == 200) {
                ResponseBody body = response.body();
                if (body == null) {
                    return clazz.newInstance();
                }
                return JSON.parseObject(body.string(), clazz);
            }
            ResponseBody body = response.body();
            if (body == null) {
                T unknown = clazz.newInstance();
                unknown.setErrorCode("Unknown");
                return unknown;
            }
            String s = response.header("Content-Type");
            if (s != null && s.toLowerCase().contains("application/json")) {
                return JSON.parseObject(body.string(), clazz);
            }
            T unknown = clazz.newInstance();
            unknown.setErrorCode("Unknown");
            unknown.setErrorMessage(body.string());
            return unknown;
        } catch (Exception e) {
            throw new RuntimeException("Call request error", e);
        }
    }

    /**
     * 流式输出
     *
     * @param apiKey
     * @param request
     * @param listener
     */
    public void stream(String apiKey, CompletionRequest request, EventSourceListener listener) {
        request.setStream(true);
        Request r = new Builder().url(completionUrl)
            .addHeader("Accept", "text/event-stream")
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer " + apiKey)
            .post(RequestBody.create(JSON.toJSONString(request), APPLICATION_JSON)).build();
        EventSources.createFactory(client).newEventSource(r, listener);
    }

    /**
     * 执行请求
     *
     * @param apiKey
     * @param request
     * @return
     */
    public CompletionResponse complete(String apiKey, CompletionRequest request) {
        request.setStream(false);
        Request r = new Builder().url(completionUrl)
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer " + apiKey)
            .post(RequestBody.create(JSON.toJSONString(request), APPLICATION_JSON)).build();
        try (Response response = client.newCall(r).execute()) {
            ResponseBody body = response.body();
            if (body == null) {
                return null;
            }
            return JSON.parseObject(body.string(), CompletionResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Call request error", e);
        }
    }
}
