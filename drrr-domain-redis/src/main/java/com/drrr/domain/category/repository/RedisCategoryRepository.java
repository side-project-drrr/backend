package com.drrr.domain.category.repository;

import com.drrr.domain.category.entity.RedisCategory;
import org.springframework.data.repository.CrudRepository;

public interface RedisCategoryRepository extends CrudRepository<RedisCategory, Long> {
}
