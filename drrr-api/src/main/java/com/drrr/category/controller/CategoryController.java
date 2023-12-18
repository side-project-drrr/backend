package com.drrr.category.controller;

import com.drrr.category.dto.CategoryRequest;
import com.drrr.category.service.impl.ExternalCategoryService;
import com.drrr.domain.category.service.CategoryService.CategoryDto;
import com.drrr.recommand.service.impl.ExternalMemberPreferredCategoryModificationService;
import com.drrr.web.security.annotation.UserAuthority;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@UserAuthority
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class CategoryController {
    private final ExternalCategoryService externalCategoryService;
    private final ExternalMemberPreferredCategoryModificationService modificationService;

    @Operation(summary = "모든 카테고리를 가져오는 API", description = "호출 성공 시 모든 카테고리 정보 반환")
    @GetMapping("/categories")
    public List<CategoryDto> findAllCategory() {
        return externalCategoryService.execute();
    }

    @Operation(summary = "특정 카테고리를 가져오는 API", description = "호출 성공 시 body 안에 명시한 카테고리 정보 반환 - 올림차순 반환")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "body 안에 명시한 카테고리 정보 반환 - 내림차순", content = @Content(schema = @Schema(implementation = CategoryDto.class)))
    })
    @GetMapping("/categories/selection")
    public List<CategoryDto> findSelectedCategory(@NonNull @RequestParam("ids") final List<Long> ids) {
        return externalCategoryService.execute(new CategoryRequest(ids));
    }

    @Operation(summary = "사용자가 선호카테고리를 바꿀 때 호출하는 API", description = "호출 성공 시 사용자의 선호나는 카테고리 변경 - 올림차순 반환",
            parameters = {
                    @Parameter(name = "memberId", description = "사용자 ID", in = ParameterIn.PATH, schema = @Schema(type = "string")),
                    @Parameter(name = "categoryIds", description = "카테고리 ID 리스트, body 안에 Json 형태로 \"category\" : [1,2,3] 이렇게 body에 넣어줄 것", schema = @Schema(type = "array", implementation = Long.class))
            })
    @PutMapping("/member/{memberId}/preferences/categories")
    public ResponseEntity<String> modifyCategory(@NonNull @PathVariable(name = "memberId") final Long memberId,
                                                 @RequestBody @NonNull final CategoryRequest request) {
        modificationService.execute(memberId, request.categoryIds());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "사용자가 가장 많이 선호하는 top 카테고리들을 가져오는 API", description = "호출 성공 시 사용자가 선호하는 카테고리들 반환 - 올림차순 반환",
            parameters = {
                    @Parameter(name = "memberId", description = "사용자 ID", in = ParameterIn.PATH, schema = @Schema(type = "string")),
                    @Parameter(name = "categoryIds", description = "카테고리 ID 리스트, body 안에 Json 형태로 \"category\" : [1,2,3] 이렇게 body에 넣어줄 것", schema = @Schema(type = "array", implementation = Long.class))
            })
    @GetMapping("/top/categories/{topN}")
    public List<CategoryDto> findTopCategories(@PathVariable("topN") @NonNull final Long topN) {
        return externalCategoryService.execute(topN);
    }
}
