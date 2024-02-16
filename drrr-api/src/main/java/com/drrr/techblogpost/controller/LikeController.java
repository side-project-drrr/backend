package com.drrr.techblogpost.controller;

import com.drrr.techblogpost.service.ExternalPostDislikeService;
import com.drrr.techblogpost.service.ExternalPostLikeService;
import com.drrr.web.annotation.MemberId;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Operation(summary = "사용자가 기술 블로그에 좋아요를 누를 때 사용하는 api - [JWT TOKEN REQUIRED]", description = "호출 성공 시 게시물 좋아요 증가")
    @Secured("USER")
    @PostMapping("/post/{postId}/like")
    public void addPostLike(@NotNull @MemberId final Long memberId,
                            @NotNull @PathVariable("postId") final Long postId) {
        externalPostLikeService.execute(memberId, postId);
    }

    @Operation(summary = "사용자가 기술 블로그에 좋아요 해제할 때 사용하는 api - [JWT TOKEN REQUIRED]", description = "호출 성공 시 게시물 좋아요 감소")
    @Secured("USER")
    @DeleteMapping("/post/{postId}/like")
    public void deletePostLike(@NotNull @MemberId final Long memberId,
                               @NotNull @PathVariable("postId") final Long postId) {
        externalPostDislikeService.execute(memberId, postId);
    }
}
