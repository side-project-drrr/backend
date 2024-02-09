package com.drrr.domain.redis.category;

import com.drrr.domain.category.repository.CategoryRepository;
import com.drrr.domain.redis.annotation.EnableRepositoryTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

@Import(CategoryRepository.class)
@EnableRepositoryTest
public class RedisRepositoryTest {
    @Autowired
    private CategoryRepository categoryRepository;
}
