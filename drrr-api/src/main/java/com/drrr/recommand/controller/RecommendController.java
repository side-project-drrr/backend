package com.drrr.recommand.controller;

import com.drrr.auth.payload.response.SignInResponse;
import com.drrr.recommand.dto.RecommendResponse;
import com.drrr.recommand.service.impl.ExternalRecommendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @Operation(summary = "사용자 게시물 추천 API", description = "호출 성공 시 추천해줄 게시물 리스트 반환")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시물 추천 성공", content = @Content(schema = @Schema(implementation = RecommendResponse.class)))
    })
    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/posts/{memberId}")
    public RecommendResponse recommendPost(@NonNull @PathVariable Long memberId) {
        return recommendService.execute(memberId);
    }
}
