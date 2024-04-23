package com.drrr.post.payload.request;

import com.drrr.post.payload.common.Message;
import java.util.List;

public record OpenAiChatCompletionRequest(String model, List<Message> messages) {
}
