package com.drrr.techblogpost.controller;

import com.drrr.category.dto.CategoryRequest;
import com.drrr.domain.category.service.CategoryService.CategoryDto;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import com.drrr.domain.techblogpost.service.TechBlogPostService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@UserAuthority
@RequestMapping("/api/v1")
public class TechBlogPostController {
    private final ExternalTechBlogPostService externalTechBlogPostService;
    @Operation(summary = "모든 기술 블로그를 가져오는 API", description = "호출 성공 시 모든 기술 블로그 정보 반환")
    @GetMapping("/posts")
    public List<TechBlogPost> findAllPosts(){
        return externalTechBlogPostService.execute();
    }
    @Operation(summary = "특정 카테고리에 해당하는 기술블로그를 가져오는 API", description = "호출 성공 시 특정 카테고리 id에 해당하는 기술 블로그 정보 반환")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "특정 카테고리 id에 해당하는 기술 블로그 정보 반환", content = @Content(schema = @Schema(implementation = CategoryDto.class)))
    })
    @GetMapping("/posts/category/{id}")
    public List<TechBlogPost> findPostsByCategory(@NonNull @PathVariable("id") final Long id){
        return externalTechBlogPostService.execute(id);
    }
}
