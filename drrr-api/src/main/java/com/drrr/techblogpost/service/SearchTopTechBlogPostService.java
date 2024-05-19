package com.drrr.techblogpost.service;

import com.drrr.core.code.techblog.TopTechBlogType;
import com.drrr.domain.like.entity.TechBlogPostLike;
import com.drrr.domain.like.repository.TechBlogPostLikeRepository;
import com.drrr.domain.like.service.TechBlogPostLikeService;
import com.drrr.domain.techblogpost.cache.payload.RedisSlicePostsContents;
import com.drrr.domain.techblogpost.dto.TechBlogPostCategoryDto;
import com.drrr.domain.techblogpost.service.DynamicDataService;
import com.drrr.domain.techblogpost.service.RedisTechBlogPostService;
import com.drrr.domain.techblogpost.service.TechBlogPostService;
import com.drrr.techblogpost.response.TechBlogPostResponse;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SearchTopTechBlogPostService {
    private final TechBlogPostService techBlogPostService;
    private final TechBlogPostLikeService techBlogPostLikeService;
    private final String RECOMMENDATION_MEMBER = "POST:%s";
    private final RedisTechBlogPostService redisTechBlogPostService;
    private final TechBlogPostLikeRepository techBlogPostLikeRepository;

    private final DynamicDataService dynamicDataService;

    public List<TechBlogPostResponse> execute(final Long memberId, final int count, final TopTechBlogType type) {
        final String key = String.format(RECOMMENDATION_MEMBER, type.toString());

        if (redisTechBlogPostService.hasCachedKey(memberId, count, key)) {
            final Set<Long> memberLikedPostIdSet = dynamicDataService.findMemberLikedPostIdSet(memberId);
            List<RedisSlicePostsContents> memberRecommendation = redisTechBlogPostService.findRedisZSetByKey(
                    memberId,
                    count,
                    key
            );
            return TechBlogPostResponse.fromRedis(memberRecommendation, memberLikedPostIdSet);
        }

        final List<TechBlogPostCategoryDto> posts = techBlogPostService.findTopPostByType(count, type);

        final List<Long> postIds = posts.stream().map((content) -> content.techBlogPostStaticDataDto().id())
                .toList();

        final List<TechBlogPostLike> memberLikedPosts = techBlogPostLikeRepository.findByMemberIdAndPostIdIn(memberId,
                postIds);

        final Set<Long> postIdSet = techBlogPostLikeService.findLikedPostIdsSet(memberId, postIds);

        redisTechBlogPostService.saveRedisZSetByKey(memberId, posts, memberLikedPosts, key);

        return TechBlogPostResponse.from(posts, postIdSet);
    }
}
