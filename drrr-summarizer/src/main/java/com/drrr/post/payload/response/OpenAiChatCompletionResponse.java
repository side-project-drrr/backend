package com.drrr.post.payload.response;

import com.drrr.post.payload.common.Message;
import java.util.List;

public record OpenAiChatCompletionResponse(
        List<Choice> choices
) {

    public String getFirstResult() {
        return choices.get(0).content();
    }


    record Choice(int index, Message message) {
        String content() {
            return message.content();
        }
    }
}
