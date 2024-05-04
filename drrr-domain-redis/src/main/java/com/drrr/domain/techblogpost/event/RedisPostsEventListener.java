package com.drrr.domain.techblogpost.event;

import com.drrr.domain.techblogpost.constant.RedisMemberConstants;
import com.drrr.domain.util.MapperUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class RedisPostsEventListener {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final MapperUtils mapperUtils;
    private final String REDIS_POST_DYNAMIC_DATA = "redisPostDynamicData:%s";
    private final String VIEW_COUNT = "viewCount";
    private final String LIKE_COUNT = "likeCount";
    private final String RECOMMENDATION_MEMBER = "recommendation:member:%s";
    private final String REDIS_MEMBER_POST_DYNAMIC_DATA = "memberIdㅇ:%s";

    @Async
    @EventListener
    public void increaseView(final IncreaseViewEvent instance) {
        redisTemplate.opsForHash()
                .increment(String.format(REDIS_POST_DYNAMIC_DATA, instance.postId), VIEW_COUNT, 1);
    }

    @Async
    @EventListener
    public void increaseLike(final IncreaseLikeEvent instance) {
        redisTemplate.opsForHash()
                .increment(String.format(REDIS_POST_DYNAMIC_DATA, instance.postId), LIKE_COUNT, 1);

        if(!instance.memberId.equals(RedisMemberConstants.GUEST.getId())){
            redisTemplate.opsForSet().add(String.format(REDIS_MEMBER_POST_DYNAMIC_DATA, instance.memberId), instance.postId);
        }
    }

    @Async
    @EventListener
    public void decreaseLike(final DecreaseLikeEvent instance) {
        redisTemplate.opsForHash()
                .increment(String.format(REDIS_POST_DYNAMIC_DATA, instance.postId.toString()), LIKE_COUNT, -1);

        if(!instance.memberId.equals(RedisMemberConstants.GUEST.getId())){
            redisTemplate.opsForSet().remove(String.format(REDIS_MEMBER_POST_DYNAMIC_DATA, instance.memberId), instance.postId);
        }
    }

    @Async
    @EventListener
    public void reformatRecommendation(final ReformatRecommendationEvent instance) {

        final List<Long> postIds = objectMapper.convertValue(
                redisTemplate.opsForValue().get(String.format(RECOMMENDATION_MEMBER, instance.memberId)),
                mapperUtils.mapType(List.class, Long.class)
        );

        final Set<Long> postIdsSet = new HashSet<>(postIds);
        boolean containsAny = postIdsSet.stream().anyMatch(instance.postIds::contains);

        if (containsAny) {
            redisTemplate.delete(String.format(RECOMMENDATION_MEMBER, instance.memberId));
        }
    }

    public record IncreaseViewEvent(
            Long postId
    ) {
    }

    public record IncreaseLikeEvent(
            Long memberId,
            Long postId
    ) {
    }

    public record DecreaseLikeEvent(
            Long memberId,
            Long postId
    ) {
    }

    public record ReformatRecommendationEvent(
            Long memberId,
            List<Long> postIds
    ) {
    }


}
