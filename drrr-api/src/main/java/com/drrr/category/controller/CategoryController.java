package com.drrr.category.controller;

import com.drrr.category.request.CategoryIndexSliceRequest;
import com.drrr.category.request.CategoryRangeRequest;
import com.drrr.category.request.CategoryRequest;
import com.drrr.category.request.CategorySearchWordRequest;
import com.drrr.category.service.impl.ExternalCategoryService;
import com.drrr.category.service.impl.ExternalFindCategoryService;
import com.drrr.category.service.impl.ExternalSearchCategoryService;
import com.drrr.domain.category.dto.CategoryDto;
import com.drrr.domain.category.dto.CategoryRangeDto;
import com.drrr.domain.category.repository.CategoryRepository;
import com.drrr.domain.category.repository.impl.CustomCategoryRepositoryImpl.CategoriesKeyDto;
import com.drrr.recommand.service.impl.ExternalMemberPreferredCategoryModificationService;
import com.drrr.web.annotation.MemberId;
import com.drrr.web.page.request.CategoryIndexPageableRequest;
import com.drrr.web.page.request.PageableRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
    private final ExternalFindCategoryService externalFindCategoryService;
    private final ExternalSearchCategoryService externalSearchCategoryService;
    private final CategoryRepository categoryRepository;

    @Operation(summary = "Index에 따른 카테고리 정보 가져오는 API", description = """
            호출 성공 시 Index에 따른 카테고리 정보 반환 [page 값은 0부터 시작 
            size는 한 page에 담길 게시물의 개수
             index는 시작하는 캐릭터 값(ex ["A", "B", "C"] - 대소문자 둘다 가능, ["가", "나"] 등)
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "request 정보를 참고하여 카테고리 정보를 반환",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CategoryDto.class))))
    })
    //기타 안 들어가 있음
    @GetMapping("/categories/index-search")
    public Slice<CategoryDto> findIndexCategory(@ModelAttribute @Valid CategoryIndexSliceRequest request,
                                                @ModelAttribute @Valid CategoryIndexPageableRequest pageableRequest) {
        return externalFindCategoryService.execute(request, pageableRequest);
    }

    @Operation(summary = "Index에 따른 카테고리 정보 가져오는 API", description = """
            호출 성공 시 Index에 따른 카테고리 정보 반환 [page 값은 0부터 시작 
            size는 한 page에 담길 게시물의 개수
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "request 정보를 참고하여 카테고리 정보를 반환",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CategoryDto.class))))
    })
    //기타 안 들어가 있음
    @GetMapping("/categories/search-etc")
    public Slice<CategoriesKeyDto> findEtcCategory(@Valid @ModelAttribute CategoryIndexPageableRequest pageRequest) {
        return externalFindCategoryService.execute(pageRequest);
    }

    @Operation(summary = "Index에 따른 카테고리 정보 가져오는 API", description = """
               호출 성공 시 Range에 따른 카테고리 정보 반환 
               한글 사용 예시) api/v1/categories/range?startIdx=가&endIdx=라&language=english&size=10
               영어 사용 예시) api/v1/categories/range?startIdx=A&endIdx=C&language=korean&size=10
               한글과 영어 동시에 쓰지 말 것! 둘다 필요한 경우 2번 요청
               startIdx와 endIdx는 시작하는 캐릭터 값(ex ["A", "B", "C"] - 대소문자 둘다 가능, ["가", "나"] 등)
               language는 어떤 언어로 가져올지 결정 (ex "KOREAN", "ENGLISH" - 대소문자 둘다 가능)
               size는 각 keyIndex마다 가져올 사이즈를 명시
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "request 정보를 참고하여 카테고리 정보를 반환",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CategoryRangeDto.class))))
    })
    @GetMapping("/categories/range")
    public CategoryRangeDto findCategoryRange(
            @Valid @ModelAttribute final CategoryRangeRequest request) {
        return externalFindCategoryService.execute(request);
    }

    @Operation(summary = "기타 카테고리 정보 가져오는 API", description = """
               호출 성공 시 size에 따른 기타 카테고리 정보 반환 
               사용 예시) api/v1/categories/range/etc?size=10
               size는 가져올 기타 카테고리의 사이즈를 명시
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "request 정보를 참고하여 카테고리 정보를 반환",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CategoryRangeDto.class))))
    })
    @GetMapping("/categories/range/etc")
    public CategoryRangeDto findEtcCategoryRange(@NotNull @RequestParam("size") final int size) {
        return externalFindCategoryService.execute(size);
    }

    @Operation(summary = "카테고리 검색 API", description = """
            호출 성공 시 request 정보를 참고하여 keyword가 들어가는 카테고리 정보 반환 [page 값은 0부터 시작 
            size는 한 page에 담길 게시물의 개수, keyWord는 앞 뒤로 like로 검색함
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "request 정보를 참고하여 keyword가 들어가는 카테고리 정보를 반환",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CategoryDto.class))))
    })
    @GetMapping("/categories/keyword-search")
    public Slice<CategoryDto> searchCategory(@Valid @ModelAttribute final CategorySearchWordRequest request,
                                             @Valid @ModelAttribute final PageableRequest pageableRequest) {
        return externalSearchCategoryService.execute(request, pageableRequest);
    }

    @Operation(summary = "특정 기술 블로그의 카테고리 정보 반환 API - 올림차순 반환", description = "호출 성공 시 특정 기술 블로그의 카테고리 정보 반환")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "특정 블로그의 카테고리 정보를 반환함",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CategoryDto.class))))
    })
    @GetMapping("/categories/posts/{postId}")
    public List<CategoryDto> findPostCategories(@PathVariable("postId") final Long postId) {
        return externalFindCategoryService.execute(postId);
    }

    @Operation(summary = "모든 카테고리를 가져오는 API", description = """
            호출 성공 시 모든 카테고리 정보 반환 [page 값은 0부터 시작 
            size는 한 page에 담길 게시물의 개수]
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "request 정보를 참고하여 모든 카테고리 정보 반환",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CategoryDto.class))))
    })
    @GetMapping("/categories/all")
    public Slice<CategoryDto> findAllCategory(@Valid @ModelAttribute final PageableRequest request) {
        return externalCategoryService.execute(request);
    }

    @Operation(summary = "특정 카테고리 정보를 가져오는 API - 올림차순 반환", description = """
            호출 성공 시 요청한 카테고리 정보 반환 - 사용 예시) /categories?categoryIds=1,2,3
            """,
            parameters = {
                    @Parameter(name = "categoryIds", description = "가져오려는 카테고리 ids 파라미터", in = ParameterIn.PATH
                            , array = @ArraySchema(schema = @Schema(type = "Long")))
            })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "request로 넘겨준 category ids에 해당하는 카테고리 정보 반환",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CategoryDto.class))))
    })
    @GetMapping("/categories")
    public List<CategoryDto> findSelectedCategory(@NotNull @RequestParam("ids") final List<Long> categoryIds) {
        return externalCategoryService.execute(categoryIds);
    }

    @Operation(summary = "사용자의 선호 카테고리를 가져오는 API(올림차순 반환) - [JWT TOKEN REQUIRED]", description = "호출 성공 시 사용자의 선호 카테고리 정보 반환")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자의 선호 카테고리 정보 반환",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CategoryDto.class))))
    })
    @Secured("USER")
    @GetMapping("/members/me/category-preference")
    public List<CategoryDto> findMemberCategory(@MemberId final Long memberId) {
        return categoryRepository.findCategoriesByMemberId(memberId)
                .stream().map(category -> CategoryDto.builder()
                        .id(category.id())
                        .name(category.name())
                        .build())
                .toList();
    }

    @Operation(summary = "사용자 선호 카테고리 변경 API - [JWT TOKEN REQUIRED]", description = """
            호출 성공 시 사용자의 선호하는 카테고리 변경(올림차순 반환) 
            - 사용 예시) /members/me/modify/category-preference?categoryIds=1,2,3
            """)
    @Secured("USER")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자의 선호 카테고리 변경"
                    , content = @Content(schema = @Schema(implementation = HttpStatus.class)))
    })
    @PutMapping("/members/me/modify/category-preference")
    public void modifyCategory(@MemberId final Long memberId, @RequestBody @NotNull final CategoryRequest request) {
        modificationService.execute(memberId, request.categoryIds());
    }

    @Operation(summary = "사용자들이 가장 많이 선호하는 top 카테고리 정보 반환 API - 인기 카테고리 순", description = """
            호출 성공 시 count만큼의 사용자가 선호하는 카테고리 정보 반환 - 올림차순 반환
            """,
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
