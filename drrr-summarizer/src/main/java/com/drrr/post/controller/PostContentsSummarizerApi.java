package com.drrr.post.controller;


import com.drrr.post.payload.request.SummarizerRequest;
import com.drrr.post.payload.response.SummarizeResponse;
import com.drrr.post.payload.response.SummarizerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/post")
@RequiredArgsConstructor
public class PostContentsSummarizerApi {
    private final SummarizerService summarizerService;


    @PostMapping("/summarize")
    public SummarizeResponse summarize(@RequestBody SummarizerRequest summarizerRequest) {
        return summarizerService.execute(summarizerRequest);
    }
}
