package com.drrr.domain.techblogpost.cache.payload;


import com.drrr.domain.category.cache.RedisCategory;
import com.drrr.domain.recommend.cache.entity.RedisMemberRecommendation;
import com.drrr.domain.recommend.cache.entity.RedisPostsCategoriesStaticData;
import com.drrr.domain.techblogpost.cache.entity.RedisMemberPostDynamicData;
import com.drrr.domain.techblogpost.cache.entity.RedisPostDynamicData;
import com.drrr.domain.techblogpost.cache.entity.RedisTechBlogPostStaticData;
import com.drrr.domain.techblogpost.cache.entity.RedisTechBlogPostsCategoriesStaticData;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Builder;

//Slice Post 반환할 때 사용
@Builder
public record RedisSlicePostsContents(
        RedisTechBlogPostStaticData redisTechBlogPostStaticData,
        RedisPostDynamicData redisTechBlogPostDynamicData,
        List<RedisCategory> redisCategories,
        boolean hasMemberLikedPost
) {


    public static List<RedisSlicePostsContents> from(final RedisMemberRecommendation recommendation,
                                                     final Map<Long, RedisPostDynamicData> redisPostDynamicDataSet,
                                                     final Set<Long> memberLikedPostIdSet) {
        return recommendation.staticData().stream()
                .map((staticData) -> {
                    RedisPostDynamicData redisPostDynamicData = redisPostDynamicDataSet.get(
                            staticData.redisTechBlogPostStaticData().id());

                    return RedisSlicePostsContents.builder()
                            .redisTechBlogPostStaticData(staticData.redisTechBlogPostStaticData())
                            .redisTechBlogPostDynamicData(redisPostDynamicData)
                            .hasMemberLikedPost(memberLikedPostIdSet.contains(staticData.postId()))
                            .redisCategories(staticData.redisCategories())
                            .build();
                })
                .toList();
    }

    public static List<RedisSlicePostsContents> from(final List<RedisPostsCategoriesStaticData> recommendation,
                                                     final Map<Long, RedisPostDynamicData> redisPostDynamicDataSet,
                                                     final Set<Long> memberLikedPostIdSet) {
        return recommendation.stream()
                .map((staticData) -> {
                    RedisPostDynamicData redisPostDynamicData = redisPostDynamicDataSet.get(
                            staticData.redisTechBlogPostStaticData().id());


                    return RedisSlicePostsContents.builder()
                            .redisTechBlogPostStaticData(staticData.redisTechBlogPostStaticData())
                            .redisTechBlogPostDynamicData(redisPostDynamicData)
                            .hasMemberLikedPost(memberLikedPostIdSet.contains(staticData.postId()))
                            .redisCategories(staticData.redisCategories())
                            .build();
                })
                .toList();
    }

    public static List<RedisSlicePostsContents> fromRedisData(final List<RedisTechBlogPostsCategoriesStaticData> staticData,
                                                     final Map<Long, RedisPostDynamicData> postDynamicDataMap,
                                                     final Set<Long> memberLikedPostIdSet) {
        return staticData.stream()
                .map(data -> RedisSlicePostsContents.builder()
                        .redisTechBlogPostDynamicData(postDynamicDataMap.get(data.redisTechBlogPostStaticData().id()))
                        .redisTechBlogPostStaticData(data.redisTechBlogPostStaticData())
                        .hasMemberLikedPost(memberLikedPostIdSet.contains(data.redisTechBlogPostStaticData().id()))
                        .redisCategories(data.redisCategories())
                        .build())
                .toList();
    }
}
