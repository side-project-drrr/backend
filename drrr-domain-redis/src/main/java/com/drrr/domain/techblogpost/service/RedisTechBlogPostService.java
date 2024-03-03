package com.drrr.domain.techblogpost.service;

import com.drrr.core.code.redis.RedisTTL;
import com.drrr.domain.category.dto.CategoryDto;
import com.drrr.domain.category.entity.RedisCategory;
import com.drrr.domain.techblogpost.dto.TechBlogPostBasicInfoDto;
import com.drrr.domain.techblogpost.dto.TechBlogPostCategoryDto;
import com.drrr.domain.techblogpost.entity.RedisAllPostCategoriesSlice;
import com.drrr.domain.techblogpost.entity.RedisAllPostCategoriesSlice.CompoundPostCategoriesSliceId;
import com.drrr.domain.techblogpost.entity.RedisCategoryTechBlogPost;
import com.drrr.domain.techblogpost.entity.RedisPageRequest;
import com.drrr.domain.techblogpost.entity.RedisTechBlogPost;
import com.drrr.domain.techblogpost.entity.RedisTechBlogPostBasicInfo;
import com.drrr.domain.techblogpost.entity.RedisTechBlogPostCategory;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import com.drrr.domain.techblogpost.repository.RedisCategoryTechBlogPostRepository;
import com.drrr.domain.techblogpost.repository.RedisTechBlogPostRepository;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
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
    private final RedisCategoryTechBlogPostRepository redisCategoryTechBlogPostRepository;
    private final RedisTemplate<String, RedisTechBlogPost> redisTechBlogPostTemplate;
    private final RedisTemplate<Object, Object> redisTemplate;

    public Boolean hasSliceKey(final int page, final int size) {
        final CompoundPostCategoriesSliceId id = CompoundPostCategoriesSliceId.builder()
                .redisPageRequest(RedisPageRequest.from(page, size))
                .build();
        return redisTemplate.hasKey(id);
    }

    public Slice<TechBlogPostCategoryDto> findAllPostsInRedis(final int page, final int size) {
        final CompoundPostCategoriesSliceId key = CompoundPostCategoriesSliceId.builder()
                .redisPageRequest(RedisPageRequest.from(page, size))
                .build();

        final RedisAllPostCategoriesSlice value = (RedisAllPostCategoriesSlice) redisTemplate.opsForValue().get(key);

        if (Objects.isNull(value)) {
            return null;
        }

        redisTemplate.expire(key, RedisTTL.ONE_HOUR.getSeconds(), TimeUnit.SECONDS);

        final List<TechBlogPostCategoryDto> techBlogPostCategoryDto = value.redisTechBlogPostCategories().stream()
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

        return new SliceImpl<>(techBlogPostCategoryDto, PageRequest.of(page, size), value.hasNext());
    }

    //Redis에 저장할 건데 key는 String value는 RedisAllPostCategoriesSlice를 byte[]로 변환한 다음에 문자열로 value로 저장할거야
    //일단 Slice<TechBlogPostCategoryDto>를 byte로 만들고 String으로 저장해줘
    public void saveAllPostsInRedis(final int page, final int size, final boolean hasNext,
                                    final List<TechBlogPostCategoryDto> posts) {
        final CompoundPostCategoriesSliceId key = CompoundPostCategoriesSliceId.builder()
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
        final RedisAllPostCategoriesSlice redisAllPostCategoriesSlice = RedisAllPostCategoriesSlice.builder()
                .id(key)
                .redisTechBlogPostCategories(value)
                .hasNext(hasNext)
                .build();

        try {
            redisTemplate.opsForValue().set(key, redisAllPostCategoriesSlice, 3600, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

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

    public List<TechBlogPost> findPostsByCategoryIdInRedis(final Long categoryId) {
        final Optional<RedisCategoryTechBlogPost> redisCategoryTechBlogPosts = redisCategoryTechBlogPostRepository.findById(
                categoryId);

        return redisCategoryTechBlogPosts.map(
                redisCategoryTechBlogPost -> redisCategoryTechBlogPost.techBlogPost().stream()
                        .filter(Objects::nonNull)
                        .toList()).orElse(null);

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

    public void saveCategoryPostsInRedis(final Long categoryId, final List<TechBlogPost> posts) {
        final RedisCategoryTechBlogPost redisPosts = RedisCategoryTechBlogPost.builder()
                .id(categoryId)
                .techBlogPost(posts)
                .build();

        redisCategoryTechBlogPostRepository.save(redisPosts);
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
}
