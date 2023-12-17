package com.drrr.domain.category.repository;

import com.drrr.domain.category.entity.RedisCategory;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface RedisCategoryRepository extends CrudRepository<RedisCategory, Long> {
    List<RedisCategory> findByIdIn(List<Long> categoryIds);
}
