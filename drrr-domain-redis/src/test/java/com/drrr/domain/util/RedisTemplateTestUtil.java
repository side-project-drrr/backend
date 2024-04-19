package com.drrr.domain.util;

import com.drrr.domain.recommend.service.RedisRecommendationService;
import com.drrr.domain.techblogpost.cache.payload.RedisSlicePostsContents;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RedisTemplateTestUtil {
    @Autowired
    private RedisRecommendationService redisRecommendationService;

    public List<RedisSlicePostsContents> findCacheMemberRecommendation(final Long memberId) {
        return redisRecommendationService.findMemberRecommendation(memberId);
    }
}
