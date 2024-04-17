package com.drrr.techblogpost.service;

import com.drrr.domain.techblogpost.cache.entity.RedisPostCategories;
import com.drrr.domain.techblogpost.dto.TechBlogPostCategoryDto;
import com.drrr.domain.techblogpost.dto.TechBlogPostDetailedInfoDto;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import com.drrr.domain.techblogpost.repository.TechBlogPostRepository;
import com.drrr.domain.techblogpost.service.RedisTechBlogPostService;
import com.drrr.domain.techblogpost.service.TechBlogPostService;
import com.drrr.techblogpost.response.TechBlogPostResponse;
import com.drrr.web.page.request.PageableRequest;
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
    public Slice<TechBlogPostResponse> execute(final PageableRequest pageableRequest) {
        final String key = "techBlogPosts";

        final RedisPostCategories redisPostCategories = redisTechBlogPostService.findCacheSlicePostsInRedis(
                pageableRequest.page(),
                pageableRequest.size(),
                key
        );

        if (redisTechBlogPostService.hasCachedKeyByRange(pageableRequest.page(), pageableRequest.size(), key)) {
            return new SliceImpl<>(
                    TechBlogPostResponse.fromRedis(
                            redisPostCategories.redisSlicePostsContents()
                    ),
                    pageableRequest.fromPageRequest(),
                    redisPostCategories.hasNext()
            );
        }

        //redis에 저장되어 있는 게 없으면 db에서 탐색
        Slice<TechBlogPostCategoryDto> allPostsSlice = techBlogPostRepository.findAllPosts(
                pageableRequest.fromPageRequest());

        Slice<TechBlogPostResponse> sliceResult = TechBlogPostResponse.from(
                allPostsSlice.getContent(),
                allPostsSlice.hasNext(),
                pageableRequest.fromPageRequest()
        );

        //redis에 저장
        redisTechBlogPostService.saveSlicePostsInRedis(allPostsSlice.getContent(), key, allPostsSlice.hasNext());

        return sliceResult;
    }

    //특정 카테고리에 해당하는 블로그를 가져오는 service
    public Slice<TechBlogPostResponse> execute(final Long categoryId, final PageableRequest pageableRequest) {
        final String key = "category" + categoryId;

        final RedisPostCategories redisCategoryPosts = redisTechBlogPostService.findCacheSlicePostsInRedis(
                pageableRequest.page(),
                pageableRequest.size(),
                key
        );

        if (redisTechBlogPostService.hasCachedKeyByRange(pageableRequest.page(), pageableRequest.size(), key)) {
            return new SliceImpl<>(
                    TechBlogPostResponse.fromRedis(redisCategoryPosts.redisSlicePostsContents()),
                    pageableRequest.fromPageRequest(),
                    redisCategoryPosts.hasNext()
            );
        }

        final Slice<TechBlogPostCategoryDto> postsByCategory = techBlogPostRepository.findPostsByCategory(categoryId,
                pageableRequest.fromPageRequest());
        final Slice<TechBlogPostResponse> sliceResult = TechBlogPostResponse.from(
                postsByCategory.getContent(),
                postsByCategory.hasNext(),
                pageableRequest.fromPageRequest()
        );

        //redis에 저장
        redisTechBlogPostService.saveSlicePostsInRedis(postsByCategory.getContent(), key, postsByCategory.hasNext());
        return sliceResult;
    }

    //특정 게시물 상세보기
    public TechBlogPostDetailedInfoDto executeFindPostDetail(final Long postId) {

        final TechBlogPost post = techBlogPostService.findTechBlogPostsById(postId);
        return TechBlogPostDetailedInfoDto.from(post);
    }


}
