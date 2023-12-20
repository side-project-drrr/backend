package com.drrr.techblogpost.controller;

import com.drrr.domain.category.service.CategoryService.CategoryDto;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import com.drrr.techblogpost.dto.TechBlogPostLikeDto;
import com.drrr.techblogpost.service.ExternalTechBlogPostLikeService;
import com.drrr.techblogpost.service.ExternalTechBlogPostService;
import com.drrr.web.security.annotation.UserAuthority;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@UserAuthority
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class TechBlogPostController {
    private final ExternalTechBlogPostService externalTechBlogPostService;
    private final ExternalTechBlogPostLikeService externalTechBlogPostLikeService;

    @Operation(summary = "모든 기술 블로그를 가져오는 API", description = "호출 성공 시 모든 기술 블로그 정보 반환")
    @GetMapping("/posts")
    public List<TechBlogPost> findAllPosts() {
        return externalTechBlogPostService.execute();
    }

    @Operation(summary = "특정 카테고리에 해당하는 기술블로그를 가져오는 API", description = "호출 성공 시 특정 카테고리 id에 해당하는 기술 블로그 정보 반환")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "특정 카테고리 id에 해당하는 기술 블로그 정보 반환", content = @Content(schema = @Schema(implementation = CategoryDto.class)))
    })
    @GetMapping("/posts/category/{id}")
    public List<TechBlogPost> findPostsByCategory(@NonNull @PathVariable("id") final Long id) {
        return externalTechBlogPostService.execute(id);
    }

    @Operation(summary = "사용자가 기술 블로그에 좋아요를 누를 때 사용하는 api", description = "호출 성공 시 게시물 좋아요 증가")
    @PostMapping("/post/like")
    public ResponseEntity<String> addPostLike(@RequestBody @NonNull final TechBlogPostLikeDto request) {
        externalTechBlogPostService.execute(request, "ADD");
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "사용자가 기술 블로그에 좋아요 해제할 때 사용하는 api", description = "호출 성공 시 게시물 좋아요 감소")
    @DeleteMapping("/post/like")
    public ResponseEntity<String> deletePostLike(@RequestBody @NonNull final TechBlogPostLikeDto request) {
        externalTechBlogPostService.execute(request, "DELETE");
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "조회수가 가장 높은 기술 블로그를 반환 api", description = "호출 성공 시 넘겨준 개수만큼 조회수가 가장 높은 기술 블로그 반환")
    @DeleteMapping("/post/top/{topN}")
    public ResponseEntity<String> findTopNPosts(@PathVariable("topN") final int topN) {
        externalTechBlogPostLikeService.execute(topN);
        return ResponseEntity.ok().build();
    }
}
