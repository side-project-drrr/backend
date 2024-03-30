package com.drrr.payload.common;

import java.util.List;

public record Message(String role, String content) {

    public static List<Message> createSingle(String role, String content) {
        return List.of(new Message(role, content));
    }

}
