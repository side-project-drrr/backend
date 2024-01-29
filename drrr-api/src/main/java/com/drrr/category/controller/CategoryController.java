package com.drrr.category.controller;

import com.drrr.category.dto.CategoryRequest;
import com.drrr.category.service.impl.ExternalCategoryService;
import com.drrr.domain.category.dto.CategoryDto;
import com.drrr.domain.category.repository.CategoryRepository;
import com.drrr.recommand.service.impl.ExternalMemberPreferredCategoryModificationService;
import com.drrr.web.jwt.util.JwtProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class CategoryController {
    private final ExternalCategoryService externalCategoryService;
    private final ExternalMemberPreferredCategoryModificationService modificationService;
    private final JwtProvider jwtProvider;
    private final CategoryRepository categoryRepository;

    @Operation(summary = "모든 카테고리를 가져오는 API - 올림차순 반환", description = "호출 성공 시 모든 카테고리 정보 반환")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "현재 존재하는 모든 카테고리 정보를 반환함",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CategoryDto.class))))
    })
    @GetMapping("/categories")
    public List<CategoryDto> findAllCategory() {
        return externalCategoryService.execute();
    }

    @Operation(summary = "특정 카테고리를 가져오는 API - 올림차순 반환", description = "호출 성공 시 카테고리 정보 반환 사용 예시) /categories/selection?categoryIds=1,2,3")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "request로 넘겨준 category ids에 해당하는 카테고리 정보 반환",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CategoryDto.class))))
    })
    @GetMapping("/categories/selection")
    public List<CategoryDto> findSelectedCategory(@NotNull @RequestParam("ids") final List<Long> categoryIds) {
        return externalCategoryService.execute(categoryIds);
    }

    @Operation(summary = "사용자의 선호 카테고리를 가져오는 API - 올림차순 반환", description = "사용자의 선호 카테고리 정보 반환")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자의 선호 카테고리 정보 반환",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CategoryDto.class))))
    })
    @GetMapping("/categories/member/{memberId}")
    public List<CategoryDto> findMemberCategory(@NotNull @PathVariable("memberId") final Long memberId) {
        return categoryRepository.findCategoriesByMemberId(memberId)
                .stream().map(category -> CategoryDto.builder()
                        .id(category.id())
                        .name(category.name())
                        .build())
                .toList();
    }


    @Operation(summary = "사용자가 선호카테고리를 바꿀 때 호출하는 API - [JWT TOKEN REQUIRED]", description = "호출 성공 시 사용자의 선호나는 카테고리 변경 - 올림차순 반환",
            parameters = {
                    @Parameter(name = "memberId", description = "사용자 ID", in = ParameterIn.PATH, schema = @Schema(type = "string")),
                    @Parameter(name = "categoryIds", description = "카테고리 ID 리스트, body 안에 Json 형태로 \"category\" : [1,2,3] 이렇게 body에 넣어줄 것", schema = @Schema(type = "array", implementation = Long.class))
            })
    @Secured("USER")
    @PutMapping("/member/preferences/categories")
    public void modifyCategory(@RequestBody @NotNull final CategoryRequest request) {
        Long memberId = jwtProvider.getMemberIdFromAuthorizationToken();
        modificationService.execute(memberId, request.categoryIds());
    }

    @Operation(summary = "사용자가 가장 많이 선호하는 top 카테고리들을 가져오는 API - 인기 카테고리 순", description = "호출 성공 시 사용자가 선호하는 카테고리들 반환 - 올림차순 반환",
            parameters = {
                    @Parameter(name = "count", description = "탑 카테고리 개수", in = ParameterIn.PATH, schema = @Schema(type = "Long"))
            })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "모든 사용자가 선호하는 카테고리 중 가장 많이 선호하는 카테고리 순으로 반환",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CategoryDto.class))))
    })
    @GetMapping("/top/categories/{count}")
    public List<CategoryDto> findTopCategories(@NotNull @PathVariable("count") @NotNull final Long count) {
        return externalCategoryService.execute(count);
    }
}
