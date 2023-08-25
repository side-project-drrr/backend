package com.drrr.drrrjpa.domain.techblogpost.repository;

import com.drrr.drrrjpa.domain.code.TechBlogCode;
import com.drrr.drrrjpa.domain.techblogpost.entity.TechBlogPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TechBlogPostRepository extends JpaRepository<TechBlogPost, Long> {
    boolean existsByTechBlogCodeAndUrlSuffix(TechBlogCode techBlogCode, String urlSuffix);
}