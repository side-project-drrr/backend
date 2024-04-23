package com.drrr.payload.response;

import java.util.List;

public record SummarizeResponse(
        String aiSummarizedText,
        List<String> categoryNames
) {
}
