package com.drrr.domain.techblogpost.repository;

import com.drrr.domain.techblogpost.entity.RedisTechBlogPost;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface RedisTechBlogPostRepository extends CrudRepository<RedisTechBlogPost, Long> {
    Optional<List<RedisTechBlogPost>> findByIdIn(List<Long> postIds);

    Optional<RedisTechBlogPost> findById(Long postIds);

}
