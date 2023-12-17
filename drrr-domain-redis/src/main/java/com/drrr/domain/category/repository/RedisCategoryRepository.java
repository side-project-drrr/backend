package com.drrr.domain.category.repository;

import com.drrr.domain.category.entity.RedisCategory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface RedisCategoryRepository extends CrudRepository<RedisCategory, Long> {
    Optional<List<RedisCategory>> findByIdIn(List<Long> categoryIds);

    List<RedisCategory> findAll();
}
