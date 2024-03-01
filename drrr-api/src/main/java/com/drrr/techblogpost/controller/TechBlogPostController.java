package com.drrr.techblogpost.controller;

import com.drrr.core.code.techblog.TopTechBlogType;
import com.drrr.domain.techblogpost.dto.TechBlogPostBasicInfoDto;
import com.drrr.domain.techblogpost.dto.TechBlogPostCategoryDto;
import com.drrr.domain.techblogpost.dto.TechBlogPostDetailedInfoDto;
import com.drrr.techblogpost.service.ExternalTechBlogPostLikeService;
import com.drrr.techblogpost.service.ExternalTechBlogPostSearchService;
import com.drrr.techblogpost.service.ExternalTechBlogPostService;
import com.drrr.web.page.request.PageableRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class TechBlogPostController {
    private final ExternalTechBlogPostService externalTechBlogPostService;
    private final ExternalTechBlogPostLikeService externalTechBlogPostLikeService;
    private final ExternalTechBlogPostSearchService externalTechBlogPostSearchService;

    @Operation(summary = "모든 기술 블로그 정보를 가져오는 API", description = """
            호출 성공 시 모든 기술 블로그 정보 반환 [page 값은 0부터 시작 
            size는 한 page에 담길 게시물의 개수 - 작성일자 기준 내림차순 반환]
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "request 정보를 참고하여 모든 블로그 정보 반환",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TechBlogPostCategoryDto.class))))
    })
    @GetMapping("/posts/all")
    public Slice<TechBlogPostCategoryDto> findAllPosts(@Valid @ModelAttribute final PageableRequest pageableRequest) {
        return externalTechBlogPostService.execute(pageableRequest);
    }

    @Operation(summary = "Keyword가 제목에 들어간 블로그 정보 가져오는 API", description = """
            호출 성공 시 keyword가 제목에 들어간 기술 블로그 정보 반환 [page 값은 0부터 시작 
            size는 한 page에 담길 게시물의 개수 - 작성일자 기준 내림차순 반환]
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "keyword가 제목에 들어간 블로그 정보 반환",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TechBlogPostCategoryDto.class))))
    })
    @GetMapping("/posts/title/keyword-search")
    public Slice<TechBlogPostCategoryDto> searchPosts(
            @Valid @RequestParam("keyword") final String keyword,
            @Valid @ModelAttribute final PageableRequest pageableRequest) {
        return externalTechBlogPostSearchService.execute(keyword, pageableRequest);
    }

    @Operation(summary = "특정 카테고리에 해당하는 기술블로그의 기본정보를 가져오는 API", description = """
            호출 성공 시 특정 카테고리 id에 해당하는 기술 블로그 기본정보 반환 [page 값은 0부터 시작 
            size는 한 page에 담길 게시물의 개수 - 작성일자 기준 내림차순 반환]
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "호출 성공 시 특정 카테고리 id에 해당하는 기술 블로그 기본정보 반환",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TechBlogPostCategoryDto.class))))
    })
    @GetMapping("/posts/categories/{categoryId}")
    public Slice<TechBlogPostCategoryDto> findPostsByCategory(@PathVariable("categoryId") final Long id,
                                                              @Valid @ModelAttribute final PageableRequest pageableRequest) {
        return externalTechBlogPostService.execute(id, pageableRequest);
    }

    @Operation(summary = "특정 게시물에 대한 상세보기 API - [JWT TOKEN REQUIRED]", description = "호출 성공 시 특정 게시물에 대한 상세 정보 반환")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "특정 게시물에 대한 상세 정보 반환", content = @Content(schema = @Schema(implementation = TechBlogPostDetailedInfoDto.class)))
    })
    @Secured("USER")
    @GetMapping("/posts/{postId}")
    public TechBlogPostDetailedInfoDto findPostDetail(@NotNull @PathVariable("postId") final Long id) {
        return externalTechBlogPostService.executeFindPostDetail(id);
    }

    @Operation(summary = "Request로 보낸 Type(VIEWS or LIKES)이 가장 높은 탑 기술 블로그를 반환 API",
            description = " 호출 성공 시 count만큼 Type(VIEWS or LIKES)이 가장 높은 기술 블로그 반환(작성일 내림차순)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회수가 가장 높은 기술 블로그를 반환",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TechBlogPostBasicInfoDto.class))))
    })
    @GetMapping("/posts/top/{count}/{type}")
    public List<TechBlogPostCategoryDto> findTopNPosts(@NotNull @PathVariable("count") final int count,
                                                       @NotNull @PathVariable("type") final TopTechBlogType type) {
        return externalTechBlogPostLikeService.execute(count, type);
    }
}
