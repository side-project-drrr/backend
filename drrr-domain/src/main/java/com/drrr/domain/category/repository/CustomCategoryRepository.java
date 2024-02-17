package com.drrr.domain.category.repository;


import com.drrr.core.category.constant.IndexConstants;
import com.drrr.core.category.constant.LanguageConstants;
import com.drrr.domain.category.dto.CategoryDto;
import com.drrr.domain.category.dto.CategoryPostDto;
import com.drrr.domain.category.entity.Category;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CustomCategoryRepository {
    List<Category> findIds(final List<Long> ids);

    List<CategoryDto> findCategoriesByMemberId(final Long memberId);

    List<Category> findByNameContaining(final String text, final Pageable pageable);

    List<Category> findTopCategories(final Long count);

    List<CategoryDto> findCategoriesByPostId(final Long postId);

    Slice<CategoryDto> findCategoryByNameLike(final LanguageConstants language, final IndexConstants indexConstants,
                                              final Pageable pageable);

    Slice<CategoryDto> searchCategoriesByKeyWord(final String keyword, final Pageable pageable);

    List<CategoryPostDto> findEachPostCategoriesByPostIds(final List<Long> postIds);


}
