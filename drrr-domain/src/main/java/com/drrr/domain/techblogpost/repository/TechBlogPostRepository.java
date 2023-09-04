package com.drrr.domain.techblogpost.repository;

import com.drrr.core.code.TechBlogCode;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TechBlogPostRepository extends JpaRepository<TechBlogPost, Long> {
    boolean existsByTechBlogCodeAndUrlSuffix(TechBlogCode techBlogCode, String urlSuffix);
}