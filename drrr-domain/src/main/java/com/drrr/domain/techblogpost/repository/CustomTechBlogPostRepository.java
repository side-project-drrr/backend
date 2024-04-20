package com.drrr.domain.techblogpost.repository;

import com.drrr.core.code.techblog.TopTechBlogType;
import com.drrr.domain.category.dto.CategoryDto;
import com.drrr.domain.techblogpost.dto.TechBlogPostBasicInfo;
import com.drrr.domain.techblogpost.dto.TechBlogPostCategoryDto;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CustomTechBlogPostRepository {
    Slice<TechBlogPostCategoryDto> findPostsByCategory(final Long categoryId, final Pageable pageable);

    List<TechBlogPostBasicInfo> findPostsByPostIds(final List<Long> postIds);

    List<Long> findTopPost(final int count, final TopTechBlogType type);

    Slice<TechBlogPostCategoryDto> findAllPosts(final Pageable pageable);

    Map<Long, List<CategoryDto>> categorizePosts(final List<Long> postIds);

    Slice<TechBlogPostCategoryDto> searchPostsTitleByKeyword(final String keyword, final Pageable pageable);
}
