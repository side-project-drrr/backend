package com.drrr.domain.techblogpost.service;

import com.drrr.domain.techblogpost.dto.TechBlogPostCategoryDto;
import com.drrr.domain.techblogpost.entity.RedisAllPostCategoriesSlice;
import com.drrr.domain.techblogpost.entity.RedisAllPostCategoriesSlice.CompoundPostCategoriesSliceId;
import com.drrr.domain.techblogpost.entity.RedisCategoryTechBlogPost;
import com.drrr.domain.techblogpost.entity.RedisTechBlogPost;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import com.drrr.domain.techblogpost.repository.RedisCategoryTechBlogPostRepository;
import com.drrr.domain.techblogpost.repository.RedisTechBlogPostRepository;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.SerializationUtils;

@Service
@RequiredArgsConstructor
@Transactional
public class RedisTechBlogPostService {
    private final RedisTechBlogPostRepository redisTechBlogPostRepository;
    private final RedisCategoryTechBlogPostRepository redisCategoryTechBlogPostRepository;
    private final RedisTemplate<String, RedisTechBlogPost> redisTechBlogPostTemplate;
    private final RedisTemplate<CompoundPostCategoriesSliceId, String> redisTemplate;

    public Slice<TechBlogPostCategoryDto> findAllPostsInRedis(final int page, final int size) {
        CompoundPostCategoriesSliceId key = CompoundPostCategoriesSliceId.builder()
                .page(page)
                .size(size)
                .build();

        String value = redisTemplate.opsForValue().get(key);

        if (Objects.isNull(value)) {
            return null;
        }

        byte[] data = Base64.getDecoder().decode(value);
        try (ByteArrayInputStream b = new ByteArrayInputStream(data);
             ObjectInputStream o = new ObjectInputStream(b)) {
            RedisAllPostCategoriesSlice entity = (RedisAllPostCategoriesSlice) o.readObject();
            return new SliceImpl<>(entity.getSliceData(), PageRequest.of(page, size), entity.isHasNext());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    //Redis에 저장할 건데 key는 String value는 RedisAllPostCategoriesSlice를 byte[]로 변환한 다음에 문자열로 value로 저장할거야
    //일단 Slice<TechBlogPostCategoryDto>를 byte로 만들고 String으로 저장해줘
    public void saveAllPostsInRedis(final int page, final int size, final boolean hasNext,
                                    final List<TechBlogPostCategoryDto> posts) {
        CompoundPostCategoriesSliceId key = CompoundPostCategoriesSliceId.builder().page(page).size(size).build();
        RedisAllPostCategoriesSlice redisAllPostCategoriesSlice = RedisAllPostCategoriesSlice.builder()
                .id(key)
                .sliceData(posts)
                .hasNext(hasNext)
                .build();

        try {
            byte[] serialize = SerializationUtils.serialize(redisAllPostCategoriesSlice);
            String value = Base64.getEncoder().encodeToString(serialize);
            redisTemplate.opsForValue().set(key, value, 3600, TimeUnit.SECONDS);
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
                .map(RedisTechBlogPost::getTechBlogPost)
                .toList();
    }

    public TechBlogPost findPostByIdInRedis(final Long postId) {
        //redis repository에서는 찾고자하는 데이터가 없으면 빈 리스트 대신 null를 반환함
        return redisTechBlogPostRepository.findById(postId).map(RedisTechBlogPost::getTechBlogPost)
                .orElse(null);
    }

    public List<TechBlogPost> findPostsByCategoryIdInRedis(final Long categoryId) {
        final Optional<RedisCategoryTechBlogPost> redisCategoryTechBlogPosts = redisCategoryTechBlogPostRepository.findById(
                categoryId);

        return redisCategoryTechBlogPosts.map(
                redisCategoryTechBlogPost -> redisCategoryTechBlogPost.getTechBlogPost().stream()
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
}
