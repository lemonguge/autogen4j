package cn.homj.autogen4j.support;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import cn.homj.autogen4j.AgentFunction;
import cn.homj.autogen4j.support.openai.chat.Choice;
import cn.homj.autogen4j.support.openai.chat.CompletionChunk;
import cn.homj.autogen4j.support.openai.chat.CompletionListener;
import cn.homj.autogen4j.support.openai.chat.CompletionRequest;
import cn.homj.autogen4j.support.openai.chat.CompletionResponse;
import cn.homj.autogen4j.support.openai.chat.Tool;
import cn.homj.autogen4j.support.openai.chat.ToolCall;
import cn.homj.autogen4j.support.openai.chat.ToolChoice;
import cn.homj.autogen4j.support.openai.chat.tool.Function;
import cn.homj.autogen4j.support.openai.chat.tool.FunctionCall;
import okhttp3.sse.EventSource;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static cn.homj.autogen4j.Definition.enableOpenAiProxy;
import static cn.homj.autogen4j.Definition.openAiApiKey;
import static cn.homj.autogen4j.Definition.openAiProxyCompletionUrl;

/**
 * @author jiehong.jh
 * @date 2023/11/27
 */
public class FunctionCallTest {

    private final Client client = new Client();

    private final Tool getCurrentWeather;
    private final Tool getWeatherForecast;

    {
        if (enableOpenAiProxy) {
            client.setCompletionUrl(openAiProxyCompletionUrl);
        }
        AgentFunction function = AgentFunctions.getCurrentWeather();
        getCurrentWeather = Tool.of(new Function()
            .setName(function.name())
            .setDescription(function.description())
            .setParameters(function.parameters()));

        function = AgentFunctions.getWeatherForecast();
        getWeatherForecast = Tool.of(new Function()
            .setName(function.name())
            .setDescription(function.description())
            .setParameters(function.parameters()));
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void complete() {
        CompletionRequest request = new CompletionRequest()
            .setModel("gpt-4")
            .addSystemMessage("You are a helpful assistant.")
            .addUserMessage("What is the weather like in Boston?")
            .addTool(getCurrentWeather);
        CompletionResponse response = client.complete(openAiApiKey, request);
        System.out.println(JSON.toJSONString(response));
        Assert.assertTrue(response.isSuccess());
        Assert.assertEquals(1, response.getChoices().size());
        Choice choice = response.getChoices().get(0);
        Assert.assertEquals("tool_calls", choice.getFinishReason());
        Message message = choice.getMessage();
        Assert.assertEquals("assistant", message.getRole());
        Assert.assertNull(message.getContent());
        List<ToolCall> toolCalls = message.getToolCalls();
        Assert.assertEquals(1, toolCalls.size());
        ToolCall toolCall = toolCalls.get(0);
        FunctionCall functionCall = toolCall.getFunctionCall();
        Assert.assertEquals("get_current_weather", functionCall.getName());
        JSONObject arguments = JSON.parseObject(functionCall.getArguments());
        String location = arguments.getString("location");
        Assert.assertNotNull(location);
        String unit = arguments.getString("unit");
        if (unit == null) {
            unit = "celsius";
        }
        String result = new JSONObject()
            .fluentPut("location", location)
            .fluentPut("unit", unit)
            .fluentPut("temperature", "25.7")
            .fluentPut("description", "sunny")
            .toString();
        System.out.println(result);
        request.addMessage(message)
            .addToolMessage(result, toolCall.getId());
        response = client.complete(openAiApiKey, request);
        System.out.println(JSON.toJSONString(response));
        Assert.assertTrue(response.isSuccess());
        Assert.assertEquals(1, response.getChoices().size());
        choice = response.getChoices().get(0);
        Assert.assertEquals("stop", choice.getFinishReason());
        message = choice.getMessage();
        Assert.assertEquals("assistant", message.getRole());
        Assert.assertNull(message.getToolCalls());
        Assert.assertNotNull(message.getContent());
    }

    @Test
    public void complete2() {
        CompletionRequest request = new CompletionRequest()
            .setModel("gpt-4")
            .addSystemMessage("You are a helpful assistant.")
            .addUserMessage("What's the weather like today?")
            .addTool(getCurrentWeather)
            .addTool(getWeatherForecast);
        CompletionResponse response = client.complete(openAiApiKey, request);
        System.out.println(JSON.toJSONString(response));
        Assert.assertTrue(response.isSuccess());
        Assert.assertEquals(1, response.getChoices().size());
        Choice choice = response.getChoices().get(0);
        Assert.assertEquals("stop", choice.getFinishReason());
        Message message = choice.getMessage();
        Assert.assertEquals("assistant", message.getRole());
        Assert.assertNull(message.getToolCalls());
        Assert.assertNotNull(message.getContent());

        request.addMessage(message)
            .addUserMessage("Boston");
        response = client.complete(openAiApiKey, request);
        System.out.println(JSON.toJSONString(response));
        Assert.assertTrue(response.isSuccess());
        Assert.assertEquals(1, response.getChoices().size());
        choice = response.getChoices().get(0);
        Assert.assertEquals("tool_calls", choice.getFinishReason());
        message = choice.getMessage();
        Assert.assertEquals("assistant", message.getRole());
        Assert.assertNull(message.getContent());
        List<ToolCall> toolCalls = message.getToolCalls();
        Assert.assertEquals(1, toolCalls.size());
        ToolCall toolCall = toolCalls.get(0);
        FunctionCall functionCall = toolCall.getFunctionCall();
        Assert.assertEquals("get_current_weather", functionCall.getName());
        JSONObject arguments = JSON.parseObject(functionCall.getArguments());
        String location = arguments.getString("location");
        Assert.assertNotNull(location);
        String unit = arguments.getString("unit");
        if (unit == null) {
            unit = "celsius";
        }
        String result = new JSONObject()
            .fluentPut("location", location)
            .fluentPut("unit", unit)
            .fluentPut("temperature", "25.7")
            .fluentPut("description", "sunny")
            .toString();
        System.out.println(result);
        request.addMessage(message)
            .addToolMessage(result, toolCall.getId());
        response = client.complete(openAiApiKey, request);
        System.out.println(JSON.toJSONString(response));
        Assert.assertTrue(response.isSuccess());
        Assert.assertEquals(1, response.getChoices().size());
        choice = response.getChoices().get(0);
        Assert.assertEquals("stop", choice.getFinishReason());
        message = choice.getMessage();
        Assert.assertEquals("assistant", message.getRole());
        Assert.assertNull(message.getToolCalls());
        Assert.assertNotNull(message.getContent());
    }

    @Test
    public void complete3() {
        CompletionRequest request = new CompletionRequest()
            .setModel("gpt-4")
            .addSystemMessage("You are a helpful assistant.")
            .addUserMessage("Give me a weather report for Toronto, Canada.")
            .setToolChoice(ToolChoice.function("get_n_day_weather_forecast"))
            .addTool(getCurrentWeather)
            .addTool(getWeatherForecast);
        CompletionResponse response = client.complete(openAiApiKey, request);
        System.out.println(JSON.toJSONString(response));
        Assert.assertTrue(response.isSuccess());
        Assert.assertEquals(1, response.getChoices().size());
        Choice choice = response.getChoices().get(0);
        Assert.assertEquals("stop", choice.getFinishReason());
        Message message = choice.getMessage();
        Assert.assertEquals("assistant", message.getRole());
        Assert.assertNull(message.getContent());
        List<ToolCall> toolCalls = message.getToolCalls();
        Assert.assertEquals(1, toolCalls.size());
        ToolCall toolCall = toolCalls.get(0);
        FunctionCall functionCall = toolCall.getFunctionCall();
        Assert.assertEquals("get_n_day_weather_forecast", functionCall.getName());
        JSONObject arguments = JSON.parseObject(functionCall.getArguments());
        String location = arguments.getString("location");
        Assert.assertNotNull(location);
        String unit = arguments.getString("unit");
        if (unit == null) {
            unit = "celsius";
        }
        Integer numDays = arguments.getInteger("num_days");
        Assert.assertNotNull(numDays);
        String result = new JSONObject()
            .fluentPut("location", location)
            .fluentPut("unit", unit)
            .fluentPut("num_days", numDays)
            .fluentPut("temperature", "25.7")
            .fluentPut("description", "sunny")
            .toString();
        System.out.println(result);
        request.addMessage(message)
            .addToolMessage(result, toolCall.getId())
            .setToolChoice(ToolChoice.NONE);
        response = client.complete(openAiApiKey, request);
        System.out.println(JSON.toJSONString(response));
        Assert.assertTrue(response.isSuccess());
        Assert.assertEquals(1, response.getChoices().size());
        choice = response.getChoices().get(0);
        Assert.assertEquals("stop", choice.getFinishReason());
        message = choice.getMessage();
        Assert.assertEquals("assistant", message.getRole());
        Assert.assertNull(message.getToolCalls());
        Assert.assertNotNull(message.getContent());
    }

    @Test
    public void complete4() {
        CompletionRequest request = new CompletionRequest()
            .setModel("gpt-3.5-turbo-1106")
            .addSystemMessage("You are a helpful assistant.")
            .addUserMessage("杭州和北京的天气如何")
            .addTool(getCurrentWeather);
        CompletionResponse response = client.complete(openAiApiKey, request);
        System.out.println(JSON.toJSONString(response));
        Assert.assertTrue(response.isSuccess());
        Assert.assertEquals(1, response.getChoices().size());
        Choice choice = response.getChoices().get(0);
        Assert.assertEquals("tool_calls", choice.getFinishReason());
        Message message = choice.getMessage();
        Assert.assertEquals("assistant", message.getRole());
        Assert.assertNull(message.getContent());
        List<ToolCall> toolCalls = message.getToolCalls();
        Assert.assertEquals(2, toolCalls.size());

        request.addMessage(message);
        for (ToolCall toolCall : toolCalls) {
            FunctionCall functionCall = toolCall.getFunctionCall();
            Assert.assertEquals("get_current_weather", functionCall.getName());
            JSONObject arguments = JSON.parseObject(functionCall.getArguments());
            String location = arguments.getString("location");
            Assert.assertNotNull(location);
            String unit = arguments.getString("unit");
            if (unit == null) {
                unit = "celsius";
            }
            String result = new JSONObject()
                .fluentPut("location", location)
                .fluentPut("unit", unit)
                .fluentPut("temperature", "25.7")
                .fluentPut("description", "sunny")
                .toString();
            System.out.println(result);
            request.addToolMessage(result, toolCall.getId());
        }
        response = client.complete(openAiApiKey, request);
        System.out.println(JSON.toJSONString(response));
        Assert.assertTrue(response.isSuccess());
        Assert.assertEquals(1, response.getChoices().size());
        choice = response.getChoices().get(0);
        Assert.assertEquals("stop", choice.getFinishReason());
        message = choice.getMessage();
        Assert.assertEquals("assistant", message.getRole());
        Assert.assertNull(message.getToolCalls());
        Assert.assertNotNull(message.getContent());
    }

    @Test
    public void complete5() {
        CompletionRequest request = new CompletionRequest()
            .setModel("gpt-4")
            .addSystemMessage("You are a helpful assistant.")
            .addUserMessage("杭州的天气如何")
            .addTool(getCurrentWeather);
        CompletionResponse response = client.complete(openAiApiKey, request);
        System.out.println(JSON.toJSONString(response));
        Assert.assertTrue(response.isSuccess());
        Assert.assertEquals(1, response.getChoices().size());
        Choice choice = response.getChoices().get(0);
        Assert.assertEquals("tool_calls", choice.getFinishReason());
        Message message = choice.getMessage();
        Assert.assertEquals("assistant", message.getRole());
        Assert.assertNull(message.getContent());
        List<ToolCall> toolCalls = message.getToolCalls();
        Assert.assertEquals(1, toolCalls.size());
        ToolCall toolCall = toolCalls.get(0);
        FunctionCall functionCall = toolCall.getFunctionCall();
        Assert.assertEquals("get_current_weather", functionCall.getName());
        JSONObject arguments = JSON.parseObject(functionCall.getArguments());
        String location = arguments.getString("location");
        Assert.assertNotNull(location);

        request.addMessage(message)
            .addToolMessage("The user refused to execute.", toolCall.getId())
            .addUserMessage("北京的");
        response = client.complete(openAiApiKey, request);
        System.out.println(JSON.toJSONString(response));
        Assert.assertTrue(response.isSuccess());
        Assert.assertEquals(1, response.getChoices().size());
        choice = response.getChoices().get(0);
        Assert.assertEquals("tool_calls", choice.getFinishReason());
        message = choice.getMessage();
        Assert.assertEquals("assistant", message.getRole());
        Assert.assertNull(message.getContent());
        toolCalls = message.getToolCalls();
        Assert.assertEquals(1, toolCalls.size());
        toolCall = toolCalls.get(0);
        functionCall = toolCall.getFunctionCall();
        Assert.assertEquals("get_current_weather", functionCall.getName());
        arguments = JSON.parseObject(functionCall.getArguments());
        location = arguments.getString("location");
        Assert.assertNotNull(location);
        String unit = arguments.getString("unit");
        if (unit == null) {
            unit = "celsius";
        }
        String result = new JSONObject()
            .fluentPut("location", location)
            .fluentPut("unit", unit)
            .fluentPut("temperature", "25.7")
            .fluentPut("description", "sunny")
            .toString();
        System.out.println(result);
        request.addMessage(message)
            .addToolMessage(result, toolCall.getId());
        response = client.complete(openAiApiKey, request);
        System.out.println(JSON.toJSONString(response));
        Assert.assertTrue(response.isSuccess());
        Assert.assertEquals(1, response.getChoices().size());
        choice = response.getChoices().get(0);
        Assert.assertEquals("stop", choice.getFinishReason());
        message = choice.getMessage();
        Assert.assertEquals("assistant", message.getRole());
        Assert.assertNull(message.getToolCalls());
        Assert.assertNotNull(message.getContent());
    }

    @Test
    public void stream() throws InterruptedException {
        CompletionRequest request = new CompletionRequest()
            .setModel("gpt-4")
            .setN(2)
            .addSystemMessage("You are a helpful assistant.")
            .addUserMessage("What is the weather like in Boston?")
            .addTool(getCurrentWeather);
        AtomicReference<CompletionChunk> ref = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);
        client.stream(openAiApiKey, request, new CompletionListener() {
            @Override
            public void onParsed(EventSource eventSource, CompletionChunk chunk) {
                System.out.println("Chunk parsed");
                ref.set(chunk);
            }

            @Override
            public void onClosed(EventSource eventSource) {
                System.out.println("SSE closed");
                latch.countDown();
            }

            @Override
            public void onFailed(EventSource eventSource, Throwable t, CompletionChunk chunk) {
                if (t == null) {
                    System.err.println("SSE failed");
                } else {
                    System.err.println("SSE failed: " + t.getMessage());
                }
                ref.set(chunk);
                latch.countDown();
            }
        });
        Assert.assertTrue(latch.await(15, TimeUnit.SECONDS));
        CompletionChunk chunk = ref.get();
        System.out.println(JSON.toJSONString(chunk));
        Assert.assertTrue(chunk.isSuccess());
        Assert.assertEquals(2, chunk.getChoices().size());
        Choice choice = chunk.getChoices().get(0);
        Assert.assertEquals("tool_calls", choice.getFinishReason());
        Message message = choice.getMessage();
        Assert.assertEquals("assistant", message.getRole());
        Assert.assertNull(message.getContent());
        List<ToolCall> toolCalls = message.getToolCalls();
        Assert.assertEquals(1, toolCalls.size());
        ToolCall toolCall = toolCalls.get(0);
        FunctionCall functionCall = toolCall.getFunctionCall();
        Assert.assertEquals("get_current_weather", functionCall.getName());
        JSONObject arguments = JSON.parseObject(functionCall.getArguments());
        String location = arguments.getString("location");
        Assert.assertNotNull(location);
    }

    @Test
    public void stream2() throws InterruptedException {
        CompletionRequest request = new CompletionRequest()
            .setModel("gpt-3.5-turbo-1106")
            .addSystemMessage("You are a helpful assistant.")
            .addUserMessage("杭州和北京的天气如何")
            .addTool(getCurrentWeather);
        AtomicReference<CompletionChunk> ref = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);
        client.stream(openAiApiKey, request, new CompletionListener() {
            @Override
            public void onParsed(EventSource eventSource, CompletionChunk chunk) {
                System.out.println("Chunk parsed");
                ref.set(chunk);
            }

            @Override
            public void onClosed(EventSource eventSource) {
                System.out.println("SSE closed");
                latch.countDown();
            }

            @Override
            public void onFailed(EventSource eventSource, Throwable t, CompletionChunk chunk) {
                if (t == null) {
                    System.err.println("SSE failed");
                } else {
                    System.err.println("SSE failed: " + t.getMessage());
                }
                ref.set(chunk);
                latch.countDown();
            }
        });
        Assert.assertTrue(latch.await(15, TimeUnit.SECONDS));
        CompletionChunk chunk = ref.get();
        System.out.println(JSON.toJSONString(chunk));
        Assert.assertTrue(chunk.isSuccess());
        Assert.assertEquals(1, chunk.getChoices().size());
        Choice choice = chunk.getChoices().get(0);
        Assert.assertEquals("tool_calls", choice.getFinishReason());
        Message message = choice.getMessage();
        Assert.assertEquals("assistant", message.getRole());
        Assert.assertNull(message.getContent());
        List<ToolCall> toolCalls = message.getToolCalls();
        Assert.assertEquals(2, toolCalls.size());

        request.addMessage(message);
        for (ToolCall toolCall : toolCalls) {
            FunctionCall functionCall = toolCall.getFunctionCall();
            Assert.assertEquals("get_current_weather", functionCall.getName());
            JSONObject arguments = JSON.parseObject(functionCall.getArguments());
            String location = arguments.getString("location");
            Assert.assertNotNull(location);
            String unit = arguments.getString("unit");
            if (unit == null) {
                unit = "celsius";
            }
            String result = new JSONObject()
                .fluentPut("location", location)
                .fluentPut("unit", unit)
                .fluentPut("temperature", "25.7")
                .fluentPut("description", "sunny")
                .toString();
            System.out.println(result);
            request.addToolMessage(result, toolCall.getId());
        }
        CompletionResponse response = client.complete(openAiApiKey, request);
        System.out.println(JSON.toJSONString(response));
        Assert.assertTrue(response.isSuccess());
        Assert.assertEquals(1, response.getChoices().size());
        choice = response.getChoices().get(0);
        Assert.assertEquals("stop", choice.getFinishReason());
        message = choice.getMessage();
        Assert.assertEquals("assistant", message.getRole());
        Assert.assertNull(message.getToolCalls());
        Assert.assertNotNull(message.getContent());
    }
}
