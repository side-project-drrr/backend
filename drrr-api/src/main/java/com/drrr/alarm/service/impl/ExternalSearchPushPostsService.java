package com.drrr.alarm.service.impl;

import com.drrr.alarm.service.request.PushDateRequest;
import com.drrr.domain.like.entity.TechBlogPostLike;
import com.drrr.domain.like.service.TechBlogPostLikeService;
import com.drrr.domain.techblogpost.service.TechBlogPostService;
import com.drrr.infra.push.service.PushService;
import com.drrr.techblogpost.response.TechBlogPostResponse;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ExternalSearchPushPostsService {
    private final PushService pushService;
    private final TechBlogPostService techBlogPostService;
    private final TechBlogPostLikeService techBlogPostLikeService;

    public List<TechBlogPostResponse> execute(final Long memberId, final PushDateRequest request) {
        final List<Long> postIds = pushService.findMemberPushDateRange(memberId, request.from(), request.to());

        final List<TechBlogPostLike> memberLikedPosts = techBlogPostLikeService.findMemberLikedPosts(memberId, postIds);
        final Set<Long> postIdSet = TechBlogPostLike.toSet(memberLikedPosts);

        return TechBlogPostResponse.from(techBlogPostService.findPushPosts(postIds), postIdSet);
    }


}
