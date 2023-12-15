package cn.homj.autogen4j.support.dashscope.qwen;

import java.io.IOException;

import com.alibaba.fastjson2.JSON;

import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;

/**
 * @author jiehong.jh
 * @date 2023/11/23
 */
public class GenerationListener extends EventSourceListener {

    @Override
    public void onEvent(EventSource eventSource, String id, String type, String data) {
        onParsed(eventSource, id, type, JSON.parseObject(data, GenerationResponse.class));
    }

    protected void onParsed(EventSource eventSource, String id, String type, GenerationResponse chunk) {
    }

    @Override
    public void onFailure(EventSource eventSource, Throwable t, Response response) {
        if (response == null) {
            onFailed(eventSource, t, null);
            return;
        }
        ResponseBody body = response.body();
        if (body == null) {
            GenerationResponse r = new GenerationResponse();
            r.setErrorCode("Unknown");
            onFailed(eventSource, t, r);
            return;
        }
        try {
            String s = response.header("Content-Type");
            if (s != null && s.toLowerCase().contains("application/json")) {
                onFailed(eventSource, t, JSON.parseObject(body.string(), GenerationResponse.class));
            } else {
                GenerationResponse r = new GenerationResponse();
                r.setErrorCode("Unknown");
                r.setErrorMessage(body.string());
                onFailed(eventSource, t, r);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void onFailed(EventSource eventSource, Throwable t, GenerationResponse response) {
    }
}
