package com.drrr.domain.recommend.cache.entity;

import java.util.List;
import lombok.Builder;

@Builder
public record RedisMemberRecommendation(
        Long memberId,
        List<RedisPostsCategoriesStaticData> staticData
) {

    public static RedisMemberRecommendation from(final Long memberId,
                                                 final List<RedisPostsCategoriesStaticData> staticData) {

        return RedisMemberRecommendation.builder()
                .memberId(memberId)
                .staticData(staticData)
                .build();
    }
}
