package com.drrr.alarm.service.impl;

import com.drrr.alarm.service.request.PushDateRequest;
import com.drrr.domain.techblogpost.service.TechBlogPostService;
import com.drrr.infra.push.service.PushService;
import com.drrr.techblogpost.response.TechBlogPostResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ExternalSearchPushPostsService {
    private final PushService pushService;
    private final TechBlogPostService techBlogPostService;

    public List<TechBlogPostResponse> execute(final Long memberId, final PushDateRequest request) {
        List<Long> postIds = pushService.findMemberPushDateRange(memberId, request.from(), request.to());

        return TechBlogPostResponse.from(techBlogPostService.findPushPosts(postIds));
    }


}
