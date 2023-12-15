package cn.homj.autogen4j.support;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import com.alibaba.fastjson2.JSON;

import cn.homj.autogen4j.support.openai.chat.Choice;
import cn.homj.autogen4j.support.openai.chat.CompletionChunk;
import cn.homj.autogen4j.support.openai.chat.CompletionListener;
import cn.homj.autogen4j.support.openai.chat.CompletionRequest;
import cn.homj.autogen4j.support.openai.chat.CompletionResponse;
import okhttp3.sse.EventSource;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static cn.homj.autogen4j.Definition.enableOpenAiProxy;
import static cn.homj.autogen4j.Definition.openAiApiKey;
import static cn.homj.autogen4j.Definition.openAiProxyCompletionUrl;

public class ClientTest3 {

    private final Client client = new Client();

    {
        if (enableOpenAiProxy) {
            client.setCompletionUrl(openAiProxyCompletionUrl);
        }
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void stream() throws InterruptedException {
        CompletionRequest request = new CompletionRequest();
        request.setModel("gpt-4");
        request.setN(2);
        request.setTemperature(1.8);
        request.addUserMessage("西湖在哪里");
        AtomicReference<CompletionChunk> ref = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);
        client.stream(openAiApiKey, request, new CompletionListener() {
            @Override
            public void onParsed(EventSource eventSource, CompletionChunk chunk) {
                System.out.println("Parse chunk: " + chunk);
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
        Assert.assertEquals("stop", choice.getFinishReason());
        Assert.assertNotNull(choice.getMessage());
        Assert.assertEquals("assistant", choice.getMessage().getRole());
        choice = chunk.getChoices().get(1);
        Assert.assertEquals("stop", choice.getFinishReason());
        Assert.assertNotNull(choice.getMessage());
        Assert.assertEquals("assistant", choice.getMessage().getRole());
    }

    @Test
    public void stream2() throws InterruptedException {
        CompletionRequest request = new CompletionRequest();
        request.addUserMessage("西湖在哪里");
        AtomicReference<CompletionChunk> ref = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);
        client.stream(openAiApiKey, request, new CompletionListener() {
            @Override
            public void onParsed(EventSource eventSource, CompletionChunk chunk) {
                System.out.println("Parse chunk: " + chunk);
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
        Assert.assertFalse(chunk.isSuccess());
        Assert.assertEquals("invalid_request_error", chunk.getError().getType());
    }

    @Test
    public void stream3() throws InterruptedException {
        CompletionRequest request = new CompletionRequest();
        request.setModel("gpt-4");
        request.addUserMessage("西湖在哪里");
        AtomicReference<CompletionChunk> ref = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);
        client.stream("sk-error", request, new CompletionListener() {
            @Override
            public void onParsed(EventSource eventSource, CompletionChunk chunk) {
                System.out.println("Parse chunk: " + chunk);
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
        Assert.assertFalse(chunk.isSuccess());
        Assert.assertEquals("invalid_request_error", chunk.getError().getType());
        Assert.assertEquals("invalid_api_key", chunk.getError().getCode());
    }

    @Test
    public void complete() {
        CompletionRequest request = new CompletionRequest();
        request.setModel("gpt-4");
        request.addUserMessage("西湖在哪里");
        CompletionResponse response = client.complete(openAiApiKey, request);
        System.out.println(JSON.toJSONString(response));
        Assert.assertTrue(response.isSuccess());
        Assert.assertEquals(1, response.getChoices().size());
        Choice choice = response.getChoices().get(0);
        Assert.assertEquals("stop", choice.getFinishReason());
        Assert.assertNotNull(choice.getMessage());
        Assert.assertEquals("assistant", choice.getMessage().getRole());
    }

    @Test
    public void complete2() {
        CompletionRequest request = new CompletionRequest();
        request.addUserMessage("西湖在哪里");
        CompletionResponse response = client.complete(openAiApiKey, request);
        System.out.println(JSON.toJSONString(response));
        Assert.assertFalse(response.isSuccess());
        Assert.assertEquals("invalid_request_error", response.getError().getType());
    }

    @Test
    public void complete3() {
        CompletionRequest request = new CompletionRequest();
        request.setModel("gpt-4");
        request.addUserMessage("西湖在哪里");
        CompletionResponse response = client.complete("sk-error", request);
        System.out.println(JSON.toJSONString(response));
        Assert.assertFalse(response.isSuccess());
        Assert.assertEquals("invalid_request_error", response.getError().getType());
        Assert.assertEquals("invalid_api_key", response.getError().getCode());
    }
}