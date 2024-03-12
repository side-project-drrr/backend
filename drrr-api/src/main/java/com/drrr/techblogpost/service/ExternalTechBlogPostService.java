package com.drrr.techblogpost.service;

import com.drrr.domain.techblogpost.cache.RedisKeywordPostRequest;
import com.drrr.domain.techblogpost.cache.entity.RedisCategoryPosts;
import com.drrr.domain.techblogpost.cache.entity.RedisCategoryPosts.CompoundCategoriesPostId;
import com.drrr.domain.techblogpost.cache.entity.RedisPostCategories;
import com.drrr.domain.techblogpost.cache.entity.RedisPostCategories.CompoundPostCategoriesId;
import com.drrr.domain.techblogpost.cache.request.RedisPageRequest;
import com.drrr.domain.techblogpost.dto.TechBlogPostCategoryDto;
import com.drrr.domain.techblogpost.dto.TechBlogPostDetailedInfoDto;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import com.drrr.domain.techblogpost.repository.TechBlogPostRepository;
import com.drrr.domain.techblogpost.service.RedisTechBlogPostService;
import com.drrr.domain.techblogpost.service.TechBlogPostService;
import com.drrr.web.page.request.PageableRequest;
import com.drrr.web.redis.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ExternalTechBlogPostService {
    private final TechBlogPostService techBlogPostService;
    private final RedisTechBlogPostService redisTechBlogPostService;
    private final TechBlogPostRepository techBlogPostRepository;

    @Transactional(readOnly = true)
    public Slice<TechBlogPostCategoryDto> execute(final PageableRequest pageableRequest) {
        RedisPostCategories redisPostCategories = redisTechBlogPostService.findCachePostsInRedis(
                pageableRequest.page(),
                pageableRequest.size()
        );

        //redis key 값
        final CompoundPostCategoriesId key = CompoundPostCategoriesId.builder()
                .redisPageRequest(RedisPageRequest.from(pageableRequest.page(), pageableRequest.size()))
                .build();

        if (redisTechBlogPostService.hasCachedKey(key)) {
            return new SliceImpl<>(
                    RedisUtil.redisPostCategoriesEntityToDto(
                            redisPostCategories.redisTechBlogPostCategories()),
                    pageableRequest.fromPageRequest(),
                    redisPostCategories.hasNext()
            );
        }

        //redis에 저장되어 있는 게 없으면 db에서 탐색
        final Slice<TechBlogPostCategoryDto> allPosts = techBlogPostRepository.findAllPosts(
                pageableRequest.fromPageRequest());

        //redis에 저장
        redisTechBlogPostService.savePostCategoriesInRedis(
                pageableRequest.page(),
                pageableRequest.size(),
                allPosts.hasNext(),
                allPosts.getContent()
        );

        return allPosts;
    }

    public Slice<TechBlogPostCategoryDto> execute(final Long categoryId, final PageableRequest pageableRequest) {
        RedisCategoryPosts redisCategoryPosts = redisTechBlogPostService.findPostsInRedisByCategory(
                pageableRequest.page(),
                pageableRequest.size(),
                categoryId
        );

        //redis key 값
        final CompoundCategoriesPostId key = CompoundCategoriesPostId.builder()
                .redisPageRequest(RedisPageRequest.from(pageableRequest.page(), pageableRequest.size()))
                .categoryId(categoryId)
                .build();

        if (redisTechBlogPostService.hasCachedKey(key)) {
            return new SliceImpl<>(
                    RedisUtil.redisPostCategoriesEntityToDto(
                            redisCategoryPosts.redisTechBlogPostCategories()),
                    pageableRequest.fromPageRequest(),
                    redisCategoryPosts.hasNext()
            );
        }

        Slice<TechBlogPostCategoryDto> postsByCategory = techBlogPostRepository.findPostsByCategory(categoryId,
                pageableRequest.fromPageRequest());

        //redis에 저장
        redisTechBlogPostService.savePostsByCategory(RedisKeywordPostRequest.from(
                        pageableRequest.page(),
                        pageableRequest.size(),
                        categoryId,
                        postsByCategory.hasNext(),
                        postsByCategory.getContent()
                )
        );
        return postsByCategory;
    }

    public TechBlogPostDetailedInfoDto executeFindPostDetail(final Long postId) {

        TechBlogPost post = techBlogPostService.findTechBlogPostsById(postId);
        return TechBlogPostDetailedInfoDto.from(post);
    }


}
