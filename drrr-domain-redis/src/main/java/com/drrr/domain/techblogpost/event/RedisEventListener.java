package com.drrr.domain.techblogpost.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class RedisEventListener {

    private final RedisTemplate<String, Object> redisTemplate;

    @Async
    @EventListener
    public void increaseView(final IncreaseViewEvent instance) {
        redisTemplate.opsForHash().increment("redisPostDynamicData:" + instance.postId.toString(), "viewCount", 1);
    }

    @Async
    @EventListener
    public void increaseLike(final IncreaseLikeEvent instance) {
        redisTemplate.opsForHash().increment("redisPostDynamicData:" + instance.postId.toString(), "likeCount", 1);
    }

    @Async
    @EventListener
    public void decreaseLike(final DecreaseLikeEvent instance) {
        redisTemplate.opsForHash().increment("redisPostDynamicData:" + instance.postId.toString(), "likeCount", -1);
    }


    public record IncreaseViewEvent(
            Long postId
    ) {
    }

    public record IncreaseLikeEvent(
            Long postId
    ) {
    }

    public record DecreaseLikeEvent(
            Long postId
    ) {
    }


}
