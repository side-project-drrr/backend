package com.drrr.domain.techblogpost.repository;

import com.drrr.domain.techblogpost.entity.TechBlogPostCategory;
import com.drrr.domain.techblogpost.repository.custom.CustomTechBlogPostCategoryRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TechBlogPostCategoryRepository extends JpaRepository<TechBlogPostCategory, Long>,
        CustomTechBlogPostCategoryRepository {
}
