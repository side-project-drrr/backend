package com.drrr.techblogpost.service;

import com.drrr.domain.techblogpost.cache.entity.RedisPostCategories;
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

    //모든 블로그 가져오는 service
    @Transactional(readOnly = true)
    public Slice<TechBlogPostCategoryDto> execute(final PageableRequest pageableRequest) {
        final String key = "techBlogPosts";

        final RedisPostCategories redisPostCategories = redisTechBlogPostService.findCacheSlicePostsInRedis(
                pageableRequest.page(),
                pageableRequest.size(),
                key
        );

        if (redisTechBlogPostService.hasCachedKeyByRange(pageableRequest.page(), pageableRequest.size(), key)) {
            return new SliceImpl<>(
                    RedisUtil.redisPostCategoriesEntityToDto(
                            redisPostCategories.redisPostsContents()
                    ),
                    pageableRequest.fromPageRequest(),
                    redisPostCategories.hasNext()
            );
        }

        //redis에 저장되어 있는 게 없으면 db에서 탐색
        final Slice<TechBlogPostCategoryDto> allPosts = techBlogPostRepository.findAllPosts(
                pageableRequest.fromPageRequest());

        //redis에 저장
        redisTechBlogPostService.saveSlicePostsInRedis(allPosts.getContent(), key);

        return allPosts;
    }

    //특정 카테고리에 해당하는 블로그를 가져오는 service
    public Slice<TechBlogPostCategoryDto> execute(final Long categoryId, final PageableRequest pageableRequest) {
        final String key = "category" + categoryId;

        final RedisPostCategories redisCategoryPosts = redisTechBlogPostService.findCacheSlicePostsInRedis(
                pageableRequest.page(),
                pageableRequest.size(),
                key
        );

        if (redisTechBlogPostService.hasCachedKeyByRange(pageableRequest.page(), pageableRequest.size(), key)) {
            return new SliceImpl<>(
                    RedisUtil.redisPostCategoriesEntityToDto(
                            redisCategoryPosts.redisPostsContents()
                    ),
                    pageableRequest.fromPageRequest(),
                    redisCategoryPosts.hasNext()
            );
        }

        final Slice<TechBlogPostCategoryDto> postsByCategory = techBlogPostRepository.findPostsByCategory(categoryId,
                pageableRequest.fromPageRequest());

        //redis에 저장
        redisTechBlogPostService.saveSlicePostsInRedis(postsByCategory.getContent(), key);
        return postsByCategory;
    }

    //특정 게시물 상세보기
    public TechBlogPostDetailedInfoDto executeFindPostDetail(final Long postId) {

        final TechBlogPost post = techBlogPostService.findTechBlogPostsById(postId);
        return TechBlogPostDetailedInfoDto.from(post);
    }


}
