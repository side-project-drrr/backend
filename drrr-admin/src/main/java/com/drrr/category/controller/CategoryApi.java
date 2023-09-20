package com.drrr.category.controller;


import com.drrr.domain.category.service.SearchCategoryService;
import com.drrr.domain.category.service.SearchCategoryService.SearchCategoryResultDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "category", description = "카테고리 API")
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/category")
public class CategoryApi {
    private final SearchCategoryService searchCategoryService;


    @Operation(description = "카테고리 조회")
    @GetMapping
    public ResponseEntity<List<SearchCategoryResponse>> searchCategory(
            Pageable pageable,
            @RequestParam(value = "text") @NotBlank String text
    ) {
        return ResponseEntity.ok(searchCategoryService.execute(text, pageable)
                .stream().map(SearchCategoryResponse::from)
                .toList());
    }

    @Builder
    public record SearchCategoryResponse(
            Long id,
            String uniqueName,
            String displayName
    ) {
        public static SearchCategoryResponse from(SearchCategoryResultDto searchCategoryResultDto) {
            return SearchCategoryResponse.builder()
                    .id(searchCategoryResultDto.id())
                    .displayName(searchCategoryResultDto.displayName())
                    .uniqueName(searchCategoryResultDto.uniqueName())
                    .build();
        }
    }
}
