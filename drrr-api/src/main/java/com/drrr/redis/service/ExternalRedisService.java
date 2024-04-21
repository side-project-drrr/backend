package com.drrr.redis.service;

import com.drrr.domain.util.JitterUtils;
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
    private final JitterUtils jitterUtils;

    public void execute() throws InterruptedException {
        jitterUtils.deleteKeysWithJitter();
    }
}
