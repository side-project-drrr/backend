package com.drrr.techblogpost.controller;

import com.drrr.domain.exception.DomainExceptionCode;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import com.drrr.domain.techblogpost.repository.TechBlogPostRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class TechBlogPostViewController {
    private final TechBlogPostRepository techBlogPostRepository;

    @Operation(summary = "비회원이 원 게시물을 읽는 경우 조회수 증가 API"
            , description = "호출 성공 시 비회원이 조회한 기술 블로그의 조회수 증가")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비회원이 조회한 기술 블로그의 조회수 증가"
                    , content = @Content(schema = @Schema(implementation = HttpStatus.class)))
    })
    @PostMapping("/view-post/{postId}")
    public void UnsignedMemberPostViewIncrement(@NotNull @PathVariable(name = "postId") final Long postId) {
        final TechBlogPost post = techBlogPostRepository.findByIdWithPessimisticLock(postId)
                .orElseThrow(() -> {
                    log.error("기술블로그를 찾을 수 없습니다 -> postId: {}", postId);
                    return DomainExceptionCode.TECH_BLOG_NOT_FOUND.newInstance();
                });

        //조회수 증가
        post.increaseViewCount();
    }
}
