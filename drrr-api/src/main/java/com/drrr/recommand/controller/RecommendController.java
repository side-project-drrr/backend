package com.drrr.recommand.controller;

import com.drrr.recommand.dto.RecommendResponse;
import com.drrr.recommand.service.impl.ExternalRecommendService;
import com.drrr.web.annotation.MemberId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class RecommendController {
    private final ExternalRecommendService recommendService;

    @Operation(summary = "사용자 게시물 추천 API - [JWT TOKEN REQUIRED]", description = "호출 성공 시 추천해줄 게시물 리스트 반환, 추천 게시물 중 최근에 작성된 순으로 정렬됨")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시물 추천 성공", content = @Content(schema = @Schema(implementation = RecommendResponse.class)))
    })
    @Secured("USER")
    @GetMapping("/recommendation/posts")
    public RecommendResponse recommendPost(@MemberId final Long memberId) {
        return recommendService.execute(memberId);
    }
}
