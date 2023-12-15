package cn.homj.autogen4j.support;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import com.alibaba.fastjson2.JSON;

import cn.homj.autogen4j.support.dashscope.qwen.GenerationListener;
import cn.homj.autogen4j.support.dashscope.qwen.GenerationRequest;
import cn.homj.autogen4j.support.dashscope.qwen.GenerationResponse;
import cn.homj.autogen4j.support.dashscope.qwen.GenerationResponse.Choice;
import okhttp3.sse.EventSource;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static cn.homj.autogen4j.Definition.qianWenApiKey;

public class ClientTest {

    private final Client client = new Client();

    private static void assertOutputText(GenerationResponse response) {
        Assert.assertNotNull(response.getOutput().getFinishReason());
        Assert.assertNotNull(response.getOutput().getText());
        Assert.assertNull(response.getOutput().getChoices());
    }

    private static Choice assertOutputOneChoice(GenerationResponse response) {
        Assert.assertNull(response.getOutput().getFinishReason());
        Assert.assertNull(response.getOutput().getText());
        Assert.assertNotNull(response.getOutput().getChoices());

        Assert.assertEquals(1, response.getOutput().getChoices().size());
        Choice choice = response.getOutput().getChoices().get(0);
        Assert.assertNotNull(choice.getFinishReason());
        Assert.assertNotNull(choice.getMessage());
        return choice;
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void stream() throws InterruptedException {
        GenerationRequest request = new GenerationRequest();
        request.setModel("qwen-plus");
        request.addUserMessage("西湖在哪里");
        AtomicReference<GenerationResponse> ref = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);
        client.stream(qianWenApiKey, request, new GenerationListener() {
            @Override
            public void onParsed(EventSource eventSource, String id, String type, GenerationResponse chunk) {
                System.out.println("Parse id: " + id + ", type: " + type + ", chunk: " + chunk);
                ref.set(chunk);
            }

            @Override
            public void onClosed(EventSource eventSource) {
                System.out.println("SSE closed");
                latch.countDown();
            }

            @Override
            public void onFailed(EventSource eventSource, Throwable t, GenerationResponse response) {
                if (t == null) {
                    System.err.println("SSE failed");
                } else {
                    System.err.println("SSE failed: " + t.getMessage());
                }
                ref.set(response);
                latch.countDown();
            }
        });
        Assert.assertTrue(latch.await(5, TimeUnit.SECONDS));
        GenerationResponse response = ref.get();
        Assert.assertTrue(response.isSuccess());
        assertOutputText(response);
    }

    @Test
    public void stream2() throws InterruptedException {
        GenerationRequest request = new GenerationRequest();
        request.setModel("qwen-plus");
        request.setResultFormat("message");
        request.addUserMessage("西湖在哪里");
        AtomicReference<GenerationResponse> ref = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);
        client.stream(qianWenApiKey, request, new GenerationListener() {
            @Override
            public void onParsed(EventSource eventSource, String id, String type, GenerationResponse chunk) {
                System.out.println("Parse id: " + id + ", type: " + type + ", chunk: " + chunk);
                ref.set(chunk);
            }

            @Override
            public void onClosed(EventSource eventSource) {
                System.out.println("SSE closed");
                latch.countDown();
            }

            @Override
            public void onFailed(EventSource eventSource, Throwable t, GenerationResponse response) {
                if (t == null) {
                    System.err.println("SSE failed");
                } else {
                    System.err.println("SSE failed: " + t.getMessage());
                }
                ref.set(response);
                latch.countDown();
            }
        });
        Assert.assertTrue(latch.await(5, TimeUnit.SECONDS));
        GenerationResponse response = ref.get();
        Assert.assertTrue(response.isSuccess());
        Choice choice = assertOutputOneChoice(response);
        Assert.assertEquals("assistant", choice.getMessage().getRole());
    }

    @Test
    public void stream3() throws InterruptedException {
        GenerationRequest request = new GenerationRequest();
        request.setModel("qwen-plus");
        request.addUserMessage("西湖在哪里");
        AtomicReference<GenerationResponse> ref = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);
        client.stream("sk-error", request, new GenerationListener() {
            @Override
            public void onParsed(EventSource eventSource, String id, String type, GenerationResponse chunk) {
                System.out.println("Parse id: " + id + ", type: " + type + ", chunk: " + chunk);
                ref.set(chunk);
            }

            @Override
            public void onClosed(EventSource eventSource) {
                System.out.println("SSE closed");
                latch.countDown();
            }

            @Override
            public void onFailed(EventSource eventSource, Throwable t, GenerationResponse response) {
                if (t == null) {
                    System.err.println("SSE failed");
                } else {
                    System.err.println("SSE failed: " + t.getMessage());
                }
                ref.set(response);
                latch.countDown();
            }
        });
        Assert.assertTrue(latch.await(5, TimeUnit.SECONDS));
        GenerationResponse response = ref.get();
        System.out.println(JSON.toJSONString(response));
        Assert.assertFalse(response.isSuccess());
        Assert.assertEquals("InvalidApiKey", response.getErrorCode());
        Assert.assertNotNull(response.getErrorMessage());
    }

    @Test
    public void stream4() throws InterruptedException {
        GenerationRequest request = new GenerationRequest();
        request.setModel("qwen-plus");
        request.addAssistantMessage("西湖在哪里");
        AtomicReference<GenerationResponse> ref = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);
        client.stream(qianWenApiKey, request, new GenerationListener() {
            @Override
            public void onParsed(EventSource eventSource, String id, String type, GenerationResponse chunk) {
                System.out.println("Parse id: " + id + ", type: " + type + ", chunk: " + chunk);
                ref.set(chunk);
            }

            @Override
            public void onClosed(EventSource eventSource) {
                System.out.println("SSE closed");
                latch.countDown();
            }

            @Override
            public void onFailed(EventSource eventSource, Throwable t, GenerationResponse response) {
                if (t == null) {
                    System.err.println("SSE failed");
                } else {
                    System.err.println("SSE failed: " + t.getMessage());
                }
                ref.set(response);
                latch.countDown();
            }
        });
        Assert.assertTrue(latch.await(5, TimeUnit.SECONDS));
        GenerationResponse response = ref.get();
        Assert.assertFalse(response.isSuccess());
        Assert.assertEquals("InvalidParameter", response.getErrorCode());
        Assert.assertNotNull(response.getErrorMessage());
    }

    @Test
    public void generate() {
        GenerationRequest request = new GenerationRequest();
        request.setModel("qwen-plus");
        request.addUserMessage("西湖在哪里");
        GenerationResponse response = client.generate(qianWenApiKey, request);
        System.out.println(JSON.toJSONString(response));
        Assert.assertTrue(response.isSuccess());
        assertOutputText(response);

        request.setResultFormat("message");
        response = client.generate(qianWenApiKey, request);
        System.out.println(JSON.toJSONString(response));
        Assert.assertTrue(response.isSuccess());
        Choice choice = assertOutputOneChoice(response);
        Assert.assertEquals("assistant", choice.getMessage().getRole());
    }

    @Test
    public void generate2() {
        GenerationRequest request = new GenerationRequest();
        request.setModel("qwen-plus");
        request.addUserMessage("西湖在哪里");
        GenerationResponse response = client.generate("sk-error", request);
        System.out.println(JSON.toJSONString(response));
        Assert.assertFalse(response.isSuccess());
        Assert.assertEquals("InvalidApiKey", response.getErrorCode());
        Assert.assertNotNull(response.getErrorMessage());
    }

    @Test
    public void generate3() {
        GenerationRequest request = new GenerationRequest();
        request.setModel("qwen-plus");
        request.setResultFormat("message");
        request.addUserMessage("如何做炒西红柿鸡蛋？");
        GenerationResponse response = client.generate(qianWenApiKey, request);
        System.out.println(JSON.toJSONString(response));
        Assert.assertTrue(response.isSuccess());
        Choice choice = assertOutputOneChoice(response);
        Assert.assertEquals("assistant", choice.getMessage().getRole());

        request.addMessage(choice.getMessage());
        request.addUserMessage("可以不放糖吗？");
        response = client.generate(qianWenApiKey, request);
        System.out.println(JSON.toJSONString(response));
        Assert.assertTrue(response.isSuccess());
        choice = assertOutputOneChoice(response);
        Assert.assertEquals("assistant", choice.getMessage().getRole());
    }

    @Test
    public void generate4() {
        GenerationRequest request = new GenerationRequest();
        request.setModel("qwen-plus");
        request.addAssistantMessage("西湖在哪里");
        GenerationResponse response = client.generate(qianWenApiKey, request);
        System.out.println(JSON.toJSONString(response));
        Assert.assertFalse(response.isSuccess());
        Assert.assertEquals("InvalidParameter", response.getErrorCode());
        Assert.assertNotNull(response.getErrorMessage());
    }
}