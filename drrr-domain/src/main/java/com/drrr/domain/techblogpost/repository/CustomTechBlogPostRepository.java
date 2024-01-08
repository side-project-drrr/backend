package com.drrr.domain.techblogpost.repository;

import com.drrr.domain.techblogpost.entity.TechBlogPost;
import java.util.List;

public interface CustomTechBlogPostRepository {
    List<Long> recommendRemain(Long memberId, int remainedPostCount);

    List<TechBlogPost> findPostsByCategory(final Long postId);

    List<TechBlogPost> findTopLikePost(final int count);
}
