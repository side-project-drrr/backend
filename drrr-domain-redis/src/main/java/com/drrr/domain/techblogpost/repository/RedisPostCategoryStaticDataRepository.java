package com.drrr.domain.techblogpost.repository;

import com.drrr.domain.techblogpost.cache.entity.RedisTechBlogPostsCategoriesStaticData;
import org.springframework.data.repository.CrudRepository;

public interface RedisPostCategoryStaticDataRepository extends
        CrudRepository<RedisTechBlogPostsCategoriesStaticData, Long> {
}
