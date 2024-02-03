package com.drrr.domain.techblogpost.repository;

import com.drrr.domain.techblogpost.dto.TechBlogPostBasicInfoDto;
import com.drrr.domain.techblogpost.dto.TechBlogPostCategoryDto;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CustomTechBlogPostRepository {
    List<Long> recommendRemain(Long memberId, int remainedPostCount);

    Slice<TechBlogPostCategoryDto> findPostsByCategory(Long categoryId, Pageable pageable);

    List<TechBlogPostBasicInfoDto> findTopLikePost(final int count);

    Slice<TechBlogPostCategoryDto> findAllPosts(final Pageable pageable);
}
