package com.drrr.domain.techblogpost.repository;

import com.drrr.domain.techblogpost.cache.entity.RedisPostDynamicData;
import org.springframework.data.repository.CrudRepository;

public interface RedisPostDynamicDataRepository extends CrudRepository<RedisPostDynamicData, Long> {
}
