package com.example.drrrbatch.batch.repository;

import com.example.drrrbatch.batch.entity.TechBlogPost;
import com.example.drrrbatch.batch.vo.TechBlogCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TechBlogPostRepository extends JpaRepository<TechBlogPost, Long> {
    boolean existsByTechBlogCodeAndUrlSuffix(TechBlogCode techBlogCode, String urlSuffix);
}