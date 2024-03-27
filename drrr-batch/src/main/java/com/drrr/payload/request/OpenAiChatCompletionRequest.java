package com.drrr.payload.request;

import com.drrr.payload.common.Message;
import java.util.List;

public record OpenAiChatCompletionRequest(String model, List<Message> messages) {
}
