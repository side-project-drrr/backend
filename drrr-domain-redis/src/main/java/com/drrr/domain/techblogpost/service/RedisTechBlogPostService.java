package com.drrr.domain.techblogpost.service;

import com.drrr.core.code.redis.RedisTTL;
import com.drrr.domain.category.dto.CategoryDto;
import com.drrr.domain.category.entity.RedisCategory;
import com.drrr.domain.techblogpost.cache.RedisKeywordPostRequest;
import com.drrr.domain.techblogpost.cache.RedisTechBlogPost;
import com.drrr.domain.techblogpost.cache.RedisTechBlogPostBasicInfo;
import com.drrr.domain.techblogpost.cache.RedisTechBlogPostCategory;
import com.drrr.domain.techblogpost.cache.entity.RedisCategoryPosts;
import com.drrr.domain.techblogpost.cache.entity.RedisCategoryPosts.CompoundCategoriesPostId;
import com.drrr.domain.techblogpost.cache.entity.RedisPostCategories;
import com.drrr.domain.techblogpost.cache.entity.RedisPostCategories.CompoundPostCategoriesId;
import com.drrr.domain.techblogpost.cache.request.RedisPageRequest;
import com.drrr.domain.techblogpost.dto.TechBlogPostBasicInfoDto;
import com.drrr.domain.techblogpost.dto.TechBlogPostCategoryDto;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import com.drrr.domain.techblogpost.repository.RedisTechBlogPostRepository;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RedisTechBlogPostService {
    private final RedisTechBlogPostRepository redisTechBlogPostRepository;
    private final RedisTemplate<String, RedisTechBlogPost> redisTechBlogPostTemplate;
    private final RedisTemplate<Object, Object> redisTemplate;

    public <T> Boolean hasCachedKey(final T id) {
        return redisTemplate.hasKey(id);
    }

    public RedisPostCategories findCachePostsInRedis(final int page, final int size) {
        final CompoundPostCategoriesId key = CompoundPostCategoriesId.builder()
                .redisPageRequest(RedisPageRequest.from(page, size))
                .build();

        final RedisPostCategories value = (RedisPostCategories) redisTemplate.opsForValue().get(key);

        redisTemplate.expire(key, RedisTTL.EXPIRE_CACHE.getSeconds(), TimeUnit.SECONDS);

        return value;
    }

    public void savePostCategoriesInRedis(final int page, final int size, final boolean hasNext,
                                          final List<TechBlogPostCategoryDto> posts) {
        final CompoundPostCategoriesId key = CompoundPostCategoriesId.builder()
                .redisPageRequest(RedisPageRequest.from(page, size)).build();
        final List<RedisTechBlogPostCategory> value = posts.stream()
                .map((entity) -> {
                    RedisTechBlogPostBasicInfo redisTechBlogPostBasicInfo = RedisTechBlogPostBasicInfo.from(
                            entity.techBlogPostBasicInfoDto());
                    List<RedisCategory> redisCategories = RedisCategory.from(entity.categoryDto());

                    return RedisTechBlogPostCategory.builder()
                            .redisTechBlogPostBasicInfo(redisTechBlogPostBasicInfo)
                            .redisCategories(redisCategories)
                            .build();
                })
                .toList();
        final RedisPostCategories redisPostCategories = RedisPostCategories.builder()
                .id(key)
                .redisTechBlogPostCategories(value)
                .hasNext(hasNext)
                .build();

        redisTemplate.opsForValue().set(key, redisPostCategories, 3600, TimeUnit.SECONDS);
    }

    public RedisCategoryPosts findPostsInRedisByCategory(final int page, final int size, final Long categoryId) {
        final CompoundCategoriesPostId key = CompoundCategoriesPostId.builder()
                .redisPageRequest(RedisPageRequest.from(page, size))
                .categoryId(categoryId)
                .build();

        final RedisCategoryPosts value = (RedisCategoryPosts) redisTemplate.opsForValue().get(key);

        redisTemplate.expire(key, RedisTTL.EXPIRE_CACHE.getSeconds(), TimeUnit.SECONDS);

        return value;
    }

    public void savePostsByCategory(final RedisKeywordPostRequest redisKeywordPostRequest) {
        final CompoundCategoriesPostId key = CompoundCategoriesPostId.builder()
                .redisPageRequest(redisKeywordPostRequest.fromPageRequest())
                .categoryId(redisKeywordPostRequest.categoryId())
                .build();

        final List<RedisTechBlogPostCategory> value = redisKeywordPostRequest.posts().stream()
                .map((entity) -> {
                    RedisTechBlogPostBasicInfo redisTechBlogPostBasicInfo = RedisTechBlogPostBasicInfo.from(
                            entity.techBlogPostBasicInfoDto());
                    List<RedisCategory> redisCategories = RedisCategory.from(entity.categoryDto());

                    return RedisTechBlogPostCategory.builder()
                            .redisTechBlogPostBasicInfo(redisTechBlogPostBasicInfo)
                            .redisCategories(redisCategories)
                            .build();
                })
                .toList();

        final RedisCategoryPosts redisCategoryPosts = RedisCategoryPosts.builder()
                .id(key)
                .redisTechBlogPostCategories(value)
                .hasNext(redisKeywordPostRequest.hasNext())
                .build();

        redisTemplate.opsForValue().set(key, redisCategoryPosts, 3600, TimeUnit.SECONDS);
    }


    public List<TechBlogPost> findPostsByIdsInRedis(final List<Long> postIds) {
        //redis repository에서는 찾고자하는 데이터가 없으면 빈 리스트 대신 null를 반환함
        final List<String> keys = postIds.stream()
                .map(Object::toString)
                .toList();

        final List<RedisTechBlogPost> redisTechBlogPosts = redisTechBlogPostTemplate.opsForValue().multiGet(keys);

        return redisTechBlogPosts.stream()
                .filter(Objects::nonNull)
                .map(RedisTechBlogPost::techBlogPost)
                .toList();
    }

    public TechBlogPost findPostByIdInRedis(final Long postId) {
        //redis repository에서는 찾고자하는 데이터가 없으면 빈 리스트 대신 null를 반환함
        return redisTechBlogPostRepository.findById(postId).map(RedisTechBlogPost::techBlogPost)
                .orElse(null);
    }


    public void savePostsInRedis(final List<TechBlogPost> posts) {
        final List<RedisTechBlogPost> redisTechBlogPosts = posts.stream()
                .map((entity) -> RedisTechBlogPost.builder()
                        .id(entity.getId())
                        .techBlogPost(entity)
                        .build())
                .toList();

        redisTechBlogPostRepository.saveAll(redisTechBlogPosts);
    }


    //redisTemplate.delete()를 사용해서 redis에 저장된 데이터를 삭제할 수 있음
    //jitter를 사용해서 redis에 저장된 데이터를 삭제하는 메서드를 만들어보자
    public void deleteKeysWithJitter() throws InterruptedException {
        try (Cursor<byte[]> cursor = redisTemplate.getConnectionFactory().getConnection().scan(ScanOptions.NONE)) {
            while (cursor.hasNext()) {
                String key = new String(cursor.next());
                long seconds = applyJitterSeconds();
                redisTemplate.expire(key, seconds, TimeUnit.SECONDS);
            }
        }
    }

    private long applyJitterSeconds() throws InterruptedException {
        // 지터 시간은 예를 들어 100ms에서 1000ms 사이의 랜덤한 시간으로 설정
        return (long) (Math.random() * 900 + 100);
    }

    public List<TechBlogPostCategoryDto> redisPostCategoriesEntityToDto(
            final List<RedisTechBlogPostCategory> redisTechBlogPostCategories) {
        return redisTechBlogPostCategories.stream()
                .map((redisEntity) -> TechBlogPostCategoryDto.builder()
                        .techBlogPostBasicInfoDto(TechBlogPostBasicInfoDto.builder()
                                .id(redisEntity.redisTechBlogPostBasicInfo().id())
                                .postLike(redisEntity.redisTechBlogPostBasicInfo().postLike())
                                .summary(redisEntity.redisTechBlogPostBasicInfo().summary())
                                .thumbnailUrl(redisEntity.redisTechBlogPostBasicInfo().thumbnailUrl())
                                .title(redisEntity.redisTechBlogPostBasicInfo().title())
                                .url(redisEntity.redisTechBlogPostBasicInfo().url())
                                .viewCount(redisEntity.redisTechBlogPostBasicInfo().viewCount())
                                .techBlogCode(redisEntity.redisTechBlogPostBasicInfo().techBlogCode())
                                .writtenAt(redisEntity.redisTechBlogPostBasicInfo().writtenAt())
                                .build())
                        .categoryDto(redisEntity.redisCategories().stream()
                                .map(redisCategory -> CategoryDto.builder()
                                        .id(redisCategory.id())
                                        .name(redisCategory.name())
                                        .build())
                                .toList()).build()).toList();
    }
}
