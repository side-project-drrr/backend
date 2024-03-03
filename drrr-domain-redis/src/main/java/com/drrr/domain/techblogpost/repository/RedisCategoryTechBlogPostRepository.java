package com.drrr.domain.techblogpost.repository;

import com.drrr.domain.techblogpost.entity.RedisCategoryTechBlogPost;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface RedisCategoryTechBlogPostRepository extends CrudRepository<RedisCategoryTechBlogPost, Long> {
    Optional<RedisCategoryTechBlogPost> findById(final Long categoryId);

}
