package com.drrr.domain.techblogpost.event;

import java.util.HashSet;
import java.util.List;
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

    @Async
    @EventListener
    public void reformatRecommendation(final ReformatRecommendationEvent instance) {

        List<Long> postIds = (List<Long>) redisTemplate.opsForValue()
                .get("recommendation:member:" + instance.memberId.toString());

        HashSet<Long> postIdsSet = new HashSet<>(postIds);
        boolean containsAny = postIdsSet.stream().anyMatch(instance.postIds::contains);

        if (containsAny) {
            redisTemplate.delete("recommendation:member:" + instance.memberId);
        }
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

    public record ReformatRecommendationEvent(
            Long memberId,
            List<Long> postIds
    ) {
    }


}
