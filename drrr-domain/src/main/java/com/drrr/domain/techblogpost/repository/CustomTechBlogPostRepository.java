package com.drrr.domain.techblogpost.repository;

import com.drrr.domain.techblogpost.dto.TechBlogPostBasicInfoDto;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CustomTechBlogPostRepository {
    List<Long> recommendRemain(Long memberId, int remainedPostCount);

    Slice<TechBlogPostBasicInfoDto> findPostsByCategory(Long categoryId, Pageable pageable);

    List<TechBlogPost> findTopLikePost(final int count);
}
