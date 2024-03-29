package com.drrr.domain.techblogpost.repository;

import com.drrr.core.code.techblog.TopTechBlogType;
import com.drrr.domain.techblogpost.dto.TechBlogPostBasicInfoDto;
import com.drrr.domain.techblogpost.dto.TechBlogPostCategoryDto;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CustomTechBlogPostRepository {
    Slice<TechBlogPostCategoryDto> findPostsByCategory(Long categoryId, Pageable pageable);

    List<TechBlogPostBasicInfoDto> findPostsByPostIds(List<Long> postIds);

    List<Long> findTopPost(final int count, final TopTechBlogType type);

    Slice<TechBlogPostCategoryDto> findAllPosts(final Pageable pageable);

    List<TechBlogPostCategoryDto> categorizePosts(final List<Long> postIds);

    Slice<TechBlogPostCategoryDto> searchPostsTitleByKeyword(final String keyword, final Pageable pageable);
}
