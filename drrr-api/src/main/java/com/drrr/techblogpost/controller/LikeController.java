package com.drrr.techblogpost.controller;

import com.drrr.techblogpost.service.ExternalPostDislikeService;
import com.drrr.techblogpost.service.ExternalPostLikeService;
import com.drrr.web.annotation.MemberId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class LikeController {
    private final ExternalPostLikeService externalPostLikeService;
    private final ExternalPostDislikeService externalPostDislikeService;

    @Operation(summary = "사용자의 기술 블로그 좋아요 증가 API - [JWT TOKEN REQUIRED]", description = "호출 성공 시 게시물 좋아요 증가")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "기술 블로그 좋아요 증가"
                    , content = @Content(schema = @Schema(implementation = HttpStatus.class)))
    })
    @Secured("USER")
    @PostMapping("/post/{postId}/like")
    public void addPostLike(
            @Parameter(
                    in = ParameterIn.HEADER, name = "Authorization",
                    required = true,
                    description = "JWT Token",
                    schema = @Schema(type = "string")
            )
            @MemberId final Long memberId,
            @NotNull @PathVariable("postId") final Long postId
    ) {
        externalPostLikeService.execute(memberId, postId);
    }

    @Operation(summary = "사용자의 기술 블로그 좋아요 감소 API - [JWT TOKEN REQUIRED]", description = "호출 성공 시 게시물 좋아요 감소")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "기술 블로그 좋아요 감소"
                    , content = @Content(schema = @Schema(implementation = HttpStatus.class)))
    })
    @Secured("USER")
    @DeleteMapping("/post/{postId}/like")
    public void deletePostLike(
            @Parameter(
                    in = ParameterIn.HEADER, name = "Authorization",
                    required = true,
                    description = "JWT Token",
                    schema = @Schema(type = "string")
            )
            @MemberId final Long memberId,
            @NotNull @PathVariable("postId") final Long postId
    ) {
        externalPostDislikeService.execute(memberId, postId);
    }
}
