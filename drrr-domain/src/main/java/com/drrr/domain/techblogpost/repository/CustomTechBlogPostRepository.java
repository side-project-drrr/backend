package com.drrr.domain.techblogpost.repository;

import com.drrr.domain.techblogpost.dto.TechBlogPostOuterDto;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CustomTechBlogPostRepository {
    List<Long> recommendRemain(Long memberId, int remainedPostCount);

    Slice<TechBlogPostOuterDto> findPostsByCategory(Long categoryId, Pageable pageable);

    List<TechBlogPost> findTopLikePost(final int count);
}
