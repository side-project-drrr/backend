package com.drrr.domain.techblogpost.repository;

import com.drrr.domain.techblogpost.cache.entity.RedisCategoryPosts;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface RedisCategoryTechBlogPostRepository extends
        CrudRepository<RedisCategoryPosts, Long> {
    Optional<RedisCategoryPosts> findById(final Long categoryId);

}
