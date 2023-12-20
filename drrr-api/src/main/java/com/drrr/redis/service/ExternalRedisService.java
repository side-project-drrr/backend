package com.drrr.redis.service;

import com.drrr.domain.auth.repository.RedisAuthenticationTokenRepository;
import com.drrr.domain.category.repository.RedisCategoryRepository;
import com.drrr.domain.techblogpost.repository.RedisCategoryTechBlogPostRepository;
import com.drrr.domain.techblogpost.repository.RedisTechBlogPostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 새로운 게시물과 카테고리 등이 등록되는 배치 시간에 맞춰서 호출 데이터 최신화를 위함
 */
@Service
@Transactional
@RequiredArgsConstructor
public class ExternalRedisService {
    private final RedisTechBlogPostRepository redisTechBlogPostRepository;
    private final RedisCategoryTechBlogPostRepository redisCategoryTechBlogPostRepository;
    private final RedisCategoryRepository redisCategoryRepository;
    private final RedisAuthenticationTokenRepository redisAuthenticationTokenRepository;

    public void execute() {
        redisCategoryRepository.deleteAll();
        redisCategoryTechBlogPostRepository.deleteAll();
        redisTechBlogPostRepository.deleteAll();
        redisAuthenticationTokenRepository.deleteAll();
        ;
    }
}
