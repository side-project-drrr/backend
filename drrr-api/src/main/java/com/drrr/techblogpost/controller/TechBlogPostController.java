package com.drrr.techblogpost.controller;

import com.drrr.domain.techblogpost.dto.TechBlogPostBasicInfoDto;
import com.drrr.domain.techblogpost.dto.TechBlogPostDetailedInfoDto;
import com.drrr.techblogpost.dto.TechBlogPostLikeDto;
import com.drrr.techblogpost.request.TechBlogPostSliceRequest;
import com.drrr.techblogpost.service.ExternalTechBlogPostLikeService;
import com.drrr.techblogpost.service.ExternalTechBlogPostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class TechBlogPostController {
    private static final String ADD = "ADD";
    private static final String DELETE = "REMOVE";
    private final ExternalTechBlogPostService externalTechBlogPostService;
    private final ExternalTechBlogPostLikeService externalTechBlogPostLikeService;

    @Operation(summary = "모든 기술 블로그를 가져오는 API", description = "호출 성공 시 모든 기술 블로그 정보 반환 [ page 값은 0부터 시작, "
            + "size는 한 page에 담길 게시물의 개수, sort는 어떤 필드 기준으로 정렬을 할지 결정, direction은 오름차순(ASC), 내림차순(DESC)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "query param에 담긴 정보를 기반으로 기술 블로그의 기본 정보를 반환",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TechBlogPostBasicInfoDto.class))))
    })
    @GetMapping("/posts")
    public Slice<TechBlogPostBasicInfoDto> findAllPosts(@RequestBody final TechBlogPostSliceRequest request) {
        return externalTechBlogPostService.execute(request);
    }

    @Operation(summary = "특정 카테고리에 해당하는 기술블로그의 기본정보를 가져오는 API", description = "호출 성공 시 특정 카테고리 id에 해당하는 기술 블로그 기본정보 반환")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "호출 성공 시 특정 카테고리 id에 해당하는 기술 블로그 기본정보 반환",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TechBlogPostBasicInfoDto.class))))
    })
    @GetMapping("/posts/category/{id}")
    public Slice<TechBlogPostBasicInfoDto> findPostsByCategory(@PathVariable("id") final Long id,
                                                               @RequestBody final TechBlogPostSliceRequest request) {
        return externalTechBlogPostService.execute(id, request);
    }

    @Operation(summary = "특정 게시물에 대한 상세보기 API - [JWT TOKEN REQUIRED]", description = "호출 성공 시 특정 게시물에 대한 상세 정보 반환")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "특정 게시물에 대한 상세 정보 반환", content = @Content(array = @ArraySchema(schema = @Schema(implementation = TechBlogPostDetailedInfoDto.class))))
    })
    @Secured("USER")
    @GetMapping("/post/{id}")
    public TechBlogPostDetailedInfoDto findPostDetail(@NotNull @PathVariable("id") final Long id) {
        return externalTechBlogPostService.executeFindPostDetail(id);
    }

    @Operation(summary = "사용자가 기술 블로그에 좋아요를 누를 때 사용하는 api - [JWT TOKEN REQUIRED]", description = "호출 성공 시 게시물 좋아요 증가")
    @Secured("USER")
    @PostMapping("/post/like")
    public void addPostLike(@RequestBody @NotNull final TechBlogPostLikeDto request) {
        externalTechBlogPostService.execute(request, ADD);
    }

    @Operation(summary = "사용자가 기술 블로그에 좋아요 해제할 때 사용하는 api - [JWT TOKEN REQUIRED]", description = "호출 성공 시 게시물 좋아요 감소")
    @Secured("USER")
    @DeleteMapping("/post/like")
    public void deletePostLike(@RequestBody @NotNull final TechBlogPostLikeDto request) {
        externalTechBlogPostService.execute(request, DELETE);
    }

    @Operation(summary = "조회수가 가장 높은 기술 블로그를 반환 api", description = "호출 성공 시 넘겨준 개수만큼 조회수가 가장 높은 기술 블로그 반환")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회수가 가장 높은 기술 블로그를 반환",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TechBlogPostBasicInfoDto.class))))
    })
    @GetMapping("/post/top/{count}")
    public List<TechBlogPostBasicInfoDto> findTopNPosts(@NotNull @PathVariable("count") final int count) {
        return externalTechBlogPostLikeService.execute(count);
    }

}
