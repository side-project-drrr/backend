package com.drrr.domain.category.repository;


import com.drrr.domain.category.dto.CategoryDto;
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

    Slice<CategoryDto> findCategoryByNameLike(final String index, final Pageable pageable);

}
