package com.drrr.recommand.controller;

import com.drrr.recommand.dto.RecommendRequest;
import com.drrr.recommand.dto.RecommendResponse;
import com.drrr.recommand.service.impl.ExternalRecommendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/recommendation")
public class RecommendController {
    private final ExternalRecommendService recommendService;

    @PreAuthorize("hasAuthority('USER')")
    @PostMapping("/posts/{memberId}")
    public RecommendResponse recommendPost(@Validated @RequestBody RecommendRequest request) {
        return recommendService.execute(request);
    }
}
