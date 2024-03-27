package com.drrr.recommand.controller;

import com.drrr.domain.techblogpost.dto.TechBlogPostCategoryDto;
import com.drrr.recommand.service.impl.ExternalRecommendService;
import com.drrr.web.annotation.MemberId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class RecommendController {
    private final ExternalRecommendService recommendService;

    @Operation(summary = "사용자의 추천 게시물 정보 반환 API - [JWT TOKEN REQUIRED]"
            , description = "호출 성공 시 추천해줄 게시물 리스트 반환, 추천 게시물 중 최근에 작성된 순으로 정렬됨")
    @ApiResponse(responseCode = "200", description = "추천해줄 게시물 리스트 반환, 추천 게시물 중 최근에 작성된 순으로 정렬",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = TechBlogPostCategoryDto.class))))
    @Secured("USER")
    @GetMapping("/members/me/post-recommendation/{count}")
    public List<TechBlogPostCategoryDto> recommendPost(@NotNull @PathVariable("count") final int count, @MemberId final Long memberId) {
        return recommendService.execute(memberId, count);
    }
}
