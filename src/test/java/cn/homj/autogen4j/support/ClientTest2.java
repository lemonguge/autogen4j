package cn.homj.autogen4j.support;

import java.util.List;

import com.alibaba.fastjson2.JSON;

import cn.homj.autogen4j.support.dashscope.embed.EmbeddingRequest;
import cn.homj.autogen4j.support.dashscope.embed.EmbeddingResponse;
import cn.homj.autogen4j.support.dashscope.embed.EmbeddingResponse.Embedding;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static cn.homj.autogen4j.Definition.embeddingApiKey;

public class ClientTest2 {

    private final Client client = new Client();

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void embed() {
        EmbeddingRequest request = new EmbeddingRequest();
        request.setModel("text-embedding-v1");
        request.addText("hello world");
        EmbeddingResponse response = client.embed(embeddingApiKey, request);
        System.out.println(JSON.toJSONString(response));
        Assert.assertTrue(response.isSuccess());
        List<Embedding> embeddings = response.getOutput().getEmbeddings();
        Assert.assertEquals(1, embeddings.size());
        Embedding embedding = embeddings.get(0);
        Assert.assertEquals(0, (int)embedding.getIndex());
        float[] vector = embedding.getVector();
        Assert.assertEquals(1536, vector.length);
    }

    @Test
    public void embed2() {
        EmbeddingRequest request = new EmbeddingRequest();
        request.addText("hello world");
        EmbeddingResponse response = client.embed(embeddingApiKey, request);
        System.out.println(JSON.toJSONString(response));
        Assert.assertFalse(response.isSuccess());
        Assert.assertEquals("BadRequest.EmptyModel", response.getErrorCode());
        Assert.assertNotNull(response.getErrorMessage());
    }

    @Test
    public void embed3() {
        EmbeddingRequest request = new EmbeddingRequest();
        request.setModel("text-embedding-v1");
        request.addText("hello");
        request.addText("world");
        EmbeddingResponse response = client.embed(embeddingApiKey, request);
        System.out.println(JSON.toJSONString(response));
        Assert.assertTrue(response.isSuccess());
        List<Embedding> embeddings = response.getOutput().getEmbeddings();
        Assert.assertEquals(2, embeddings.size());
        Embedding embedding = embeddings.get(0);
        Assert.assertEquals(0, (int)embedding.getIndex());
        float[] vector = embedding.getVector();
        Assert.assertEquals(1536, vector.length);
        embedding = embeddings.get(1);
        Assert.assertEquals(1, (int)embedding.getIndex());
        vector = embedding.getVector();
        Assert.assertEquals(1536, vector.length);
    }
}