package com.drrr.post.payload.request;

import java.util.List;

public record SummarizerRequest(
        List<String> texts
) {
}
