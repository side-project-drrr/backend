package com.drrr.domain.techblogpost.repository;

import com.drrr.domain.category.entity.Category;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import com.drrr.domain.techblogpost.entity.TechBlogPostCategory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TechBlogPostCategoryRepository extends JpaRepository<TechBlogPostCategory, Long> {
    Optional<List<Category>> findByPostId(Long postId);
}
