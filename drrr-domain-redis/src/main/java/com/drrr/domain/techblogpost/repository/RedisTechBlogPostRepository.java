package com.drrr.domain.techblogpost.repository;

import com.drrr.domain.techblogpost.cache.RedisTechBlogPost;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface RedisTechBlogPostRepository extends CrudRepository<RedisTechBlogPost, Long> {
    Optional<List<RedisTechBlogPost>> findByIdIn(final List<Long> postIds);

    Optional<RedisTechBlogPost> findById(final Long postIds);

}
