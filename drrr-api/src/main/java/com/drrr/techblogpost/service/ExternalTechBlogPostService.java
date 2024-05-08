package com.drrr.techblogpost.service;

import com.drrr.domain.like.entity.TechBlogPostLike;
import com.drrr.domain.like.repository.TechBlogPostLikeRepository;
import com.drrr.domain.techblogpost.cache.entity.RedisPostCategories;
import com.drrr.domain.techblogpost.cache.payload.RedisSlicePostsContents;
import com.drrr.domain.techblogpost.dto.TechBlogPostSliceDto;
import com.drrr.domain.techblogpost.repository.TechBlogPostRepository;
import com.drrr.domain.techblogpost.service.DynamicDataService;
import com.drrr.domain.techblogpost.service.RedisTechBlogPostService;
import com.drrr.techblogpost.response.TechBlogPostResponse;
import com.drrr.web.page.request.PageableRequest;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ExternalTechBlogPostService {
    private final RedisTechBlogPostService redisTechBlogPostService;
    private final TechBlogPostRepository techBlogPostRepository;
    private final TechBlogPostLikeRepository techBlogPostLikeRepository;
    private final DynamicDataService dynamicDataService;

    //모든 블로그 가져오는 service
    @Transactional(readOnly = true)
    public Slice<TechBlogPostResponse> execute(PageableRequest pageableRequest, final Long memberId) {
        final String key = "techBlogPosts";

        final RedisPostCategories redisPostCategories = redisTechBlogPostService.findCacheSlicePostsInRedis(
                pageableRequest.page(),
                pageableRequest.size(),
                key,
                memberId
        );
        System.out.println("##################EXTERNAL TECH BLOG POST SERVICE###############");
        for (RedisSlicePostsContents content :redisPostCategories.redisSlicePostsContents()) {
            System.out.println("content post id = " + content.redisTechBlogPostStaticData().id());
        }

        if (redisTechBlogPostService.hasCachedKeyByRange(pageableRequest.page(), pageableRequest.size(), key)) {
            System.out.println("INSIDE hasCachedKeyByRange");
            findSlicePostsByRedis(pageableRequest, memberId, redisPostCategories);
        }

        return saveAndReturnSlicePosts(pageableRequest, memberId, key);
    }

    //특정 카테고리에 해당하는 블로그를 가져오는 service
    public Slice<TechBlogPostResponse> execute(final Long categoryId, final PageableRequest pageableRequest, final Long memberId) {
        final String key = "category" + categoryId;

        final RedisPostCategories redisPostCategories = redisTechBlogPostService.findCacheSlicePostsInRedis(
                pageableRequest.page(),
                pageableRequest.size(),
                key,
                memberId
        );

        if (redisTechBlogPostService.hasCachedKeyByRange(pageableRequest.page(), pageableRequest.size(), key)) {
            findSlicePostsByRedis(pageableRequest, memberId, redisPostCategories);
        }

        return saveAndReturnCategorySlicePosts(pageableRequest, memberId, key, categoryId);
    }

    private Slice<TechBlogPostResponse> findSlicePostsByRedis(
            final PageableRequest pageableRequest,
            final Long memberId,
            final RedisPostCategories redisCategoryPosts
    ){
        final Set<Long> memberLikedPostIdSet = dynamicDataService.findMemberLikedPostIdSet(memberId);
        System.out.println("INSIDE findSlicePostsByRedis");
        for (Long aLong : memberLikedPostIdSet) {
            System.out.println("post id -> "+aLong);
        }
        return new SliceImpl<>(
                TechBlogPostResponse.fromRedis(
                        redisCategoryPosts.redisSlicePostsContents(),
                        memberLikedPostIdSet
                ),
                pageableRequest.fromPageRequest(),
                redisCategoryPosts.hasNext()
        );
    }

    private Slice<TechBlogPostResponse> saveAndReturnSlicePosts(final PageableRequest pageableRequest, final Long memberId, final String key){
        final TechBlogPostSliceDto allPosts = techBlogPostRepository.findAllPosts(
                pageableRequest.fromPageRequest()
        );

        return findSlicePostsAndSaveInRedis(allPosts, memberId, pageableRequest, key);
    }

    private Slice<TechBlogPostResponse> saveAndReturnCategorySlicePosts(
            final PageableRequest pageableRequest,
            final Long memberId,
            final String key,
            final Long categoryId
    ){
        final TechBlogPostSliceDto allPosts = techBlogPostRepository.findPostsByCategory(
                categoryId,
                pageableRequest.fromPageRequest()
        );

        return findSlicePostsAndSaveInRedis(allPosts, memberId, pageableRequest, key);
    }

    private Slice<TechBlogPostResponse> findSlicePostsAndSaveInRedis(
            final TechBlogPostSliceDto allPosts,
            final Long memberId,
            final PageableRequest pageableRequest,
            final String key
    ){

        final List<Long> postIds = allPosts.contents()
                .stream()
                .map((content) -> content.techBlogPostStaticDataDto().id())
                .toList();

        final List<TechBlogPostLike> memberLikedPosts = techBlogPostLikeRepository.findByMemberIdAndPostIdIn(memberId,
                postIds);

        final Set<Long> memberLikedPostIdSet = TechBlogPostLike.toSet(memberLikedPosts);

        final Slice<TechBlogPostResponse> sliceResult = TechBlogPostResponse.from(
                allPosts.contents(),
                allPosts.hasNext(),
                pageableRequest.fromPageRequest(),
                memberLikedPostIdSet
        );

        //redis에 저장
        redisTechBlogPostService.saveSlicePostsInRedis(allPosts.contents(), key, allPosts.hasNext(), memberLikedPostIdSet, memberId);

        return sliceResult;
    }


}
