package com.drrr.domain.techblogpost.repository;

import java.util.List;

public interface CustomTechBlogPostRepository {
    List<Long> recommendRemain(Long memberId, int remainedPostCount);
}
