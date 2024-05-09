package com.drrr.redis.controller;

import com.drrr.domain.util.CacheFlushUtils;
import com.drrr.redis.service.ExternalRedisService;
import com.drrr.web.security.annotation.UserAuthority;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@UserAuthority
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class RedisController {

    private CacheFlushUtils cacheFlushUtils;
    private final ExternalRedisService externalRedisService;

    @Operation(summary = "게시물과 카테고리 등이 새로 입력되는 배치의 실행 후 호출되는 API")
    @DeleteMapping("/redis/cache")
    public ResponseEntity<String> deleteAllRedisCaches() throws InterruptedException {
        externalRedisService.execute();
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/redis/flushall")
    public void flushAllCache(){
        cacheFlushUtils.flushAll();
    }
}
