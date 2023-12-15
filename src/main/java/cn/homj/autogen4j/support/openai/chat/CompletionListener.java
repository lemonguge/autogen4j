package cn.homj.autogen4j.support.openai.chat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.alibaba.fastjson2.JSON;

import cn.homj.autogen4j.support.Message;
import cn.homj.autogen4j.support.openai.chat.tool.FunctionCall;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;

/**
 * @author jiehong.jh
 * @date 2023/11/23
 */
public class CompletionListener extends EventSourceListener {

    private static final String DATA_DONE = "[DONE]";
    private CompletionChunk chunk;
    private final Map<Integer, Choice> map = new TreeMap<>();

    @Override
    public void onEvent(EventSource eventSource, String id, String type, String data) {
        if (DATA_DONE.equalsIgnoreCase(data)) {
            return;
        }
        if (chunk == null) {
            chunk = JSON.parseObject(data, CompletionChunk.class);
            List<Choice> c = chunk.getChoices();
            for (Choice choice : c) {
                map.put(choice.getIndex(), choice);
            }
        } else {
            CompletionChunk _chunk = JSON.parseObject(data, CompletionChunk.class);
            List<Choice> c = _chunk.getChoices();
            for (Choice _choice : c) {
                Integer index = _choice.getIndex();
                Choice choice = map.get(index);
                if (choice == null) {
                    map.put(index, _choice);
                    continue;
                }
                Message message = choice.getMessage();
                Message _message = _choice.getMessage();
                appendContent(message, _message);
                List<ToolCall> _toolCalls = _message.getToolCalls();
                if (_toolCalls != null) {
                    List<ToolCall> toolCalls = message.getToolCalls();
                    if (toolCalls == null) {
                        message.setToolCalls(_toolCalls);
                    } else {
                        for (ToolCall _toolCall : _toolCalls) {
                            ToolCall toolCall = getToolCall(toolCalls, _toolCall.getIndex());
                            if (toolCall == null) {
                                toolCalls.add(_toolCall);
                            } else {
                                appendArguments(toolCall, _toolCall);
                            }
                        }
                    }
                }
                choice.setFinishReason(_choice.getFinishReason());
            }
        }
        chunk.setChoices(new ArrayList<>(map.values()));
        onParsed(eventSource, chunk);
    }

    private ToolCall getToolCall(List<ToolCall> toolCalls, Integer index) {
        for (ToolCall toolCall : toolCalls) {
            if (index.equals(toolCall.getIndex())) {
                return toolCall;
            }
        }
        return null;
    }

    private void appendArguments(ToolCall toolCall, ToolCall _toolCall) {
        String s = _toolCall.getFunctionCall().getArguments();
        if (s != null) {
            FunctionCall functionCall = toolCall.getFunctionCall();
            String arguments = functionCall.getArguments();
            if (arguments == null) {
                functionCall.setArguments(s);
            } else {
                functionCall.setArguments(arguments + s);
            }
        }
    }

    private void appendContent(Message message, Message _message) {
        String s = _message.getContent();
        if (s != null) {
            String content = message.getContent();
            if (content == null) {
                message.setContent(s);
            } else {
                message.setContent(content + s);
            }
        }
    }

    protected void onParsed(EventSource eventSource, CompletionChunk chunk) {
    }

    @Override
    public void onFailure(EventSource eventSource, Throwable t, Response response) {
        ResponseBody body;
        if (response == null || (body = response.body()) == null) {
            onFailed(eventSource, t, null);
            return;
        }
        try {
            onFailed(eventSource, t, JSON.parseObject(body.string(), CompletionChunk.class));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void onFailed(EventSource eventSource, Throwable t, CompletionChunk chunk) {
    }
}
