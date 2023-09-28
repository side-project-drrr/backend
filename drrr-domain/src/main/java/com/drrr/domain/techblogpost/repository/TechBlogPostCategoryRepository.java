package com.drrr.domain.techblogpost.repository;

import com.drrr.domain.techblogpost.entity.TechBlogPostCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;


public interface TechBlogPostCategoryRepository extends JpaRepository<TechBlogPostCategory, Long> {
}
