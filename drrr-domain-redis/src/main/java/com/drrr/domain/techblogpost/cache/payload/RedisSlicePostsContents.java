package com.drrr.domain.techblogpost.cache.payload;


import com.drrr.domain.category.cache.RedisCategory;
import com.drrr.domain.recommend.cache.entity.RedisMemberRecommendation;
import com.drrr.domain.recommend.cache.entity.RedisPostsCategoriesStaticData;
import com.drrr.domain.techblogpost.cache.entity.RedisPostDynamicData;
import com.drrr.domain.techblogpost.cache.entity.RedisTechBlogPostStaticData;
import java.util.List;
import java.util.Map;
import lombok.Builder;

//Slice Post 반환할 때 사용
@Builder
public record RedisSlicePostsContents(
        RedisTechBlogPostStaticData redisTechBlogPostStaticData,
        RedisPostDynamicData redisTechBlogPostDynamicData,
        List<RedisCategory> redisCategories
) {


    public static List<RedisSlicePostsContents> from(RedisMemberRecommendation recommendation,
                                                     Map<Long, RedisPostDynamicData> redisPostDynamicDataSet) {
        return recommendation.staticData().stream()
                .map((staticData) -> {
                    RedisPostDynamicData redisPostDynamicData = redisPostDynamicDataSet.get(
                            staticData.redisTechBlogPostStaticData().id());
                    return RedisSlicePostsContents.builder()
                            .redisTechBlogPostStaticData(staticData.redisTechBlogPostStaticData())
                            .redisTechBlogPostDynamicData(redisPostDynamicData)
                            .redisCategories(staticData.redisCategories())
                            .build();
                })
                .toList();
    }

    public static List<RedisSlicePostsContents> from(List<RedisPostsCategoriesStaticData> recommendation,
                                                     Map<Long, RedisPostDynamicData> redisPostDynamicDataSet) {
        return recommendation.stream()
                .map((staticData) -> {
                    RedisPostDynamicData redisPostDynamicData = redisPostDynamicDataSet.get(
                            staticData.redisTechBlogPostStaticData().id());
                    return RedisSlicePostsContents.builder()
                            .redisTechBlogPostStaticData(staticData.redisTechBlogPostStaticData())
                            .redisTechBlogPostDynamicData(redisPostDynamicData)
                            .redisCategories(staticData.redisCategories())
                            .build();
                })
                .toList();
    }
}
