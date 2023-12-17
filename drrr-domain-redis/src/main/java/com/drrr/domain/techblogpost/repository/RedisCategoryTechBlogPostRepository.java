package com.drrr.domain.techblogpost.repository;

import com.drrr.domain.techblogpost.entity.RedisCategoryTechBlogPost;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface RedisCategoryTechBlogPostRepository extends CrudRepository<RedisCategoryTechBlogPost, Long> {
    List<RedisCategoryTechBlogPost> findByIdIn(Long categoryId);
}
