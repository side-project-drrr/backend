package com.drrr.alarm.service.impl;

import com.drrr.alarm.service.request.PushDateRequest;
import com.drrr.domain.techblogpost.dto.TechBlogPostCategoryDto;
import com.drrr.domain.techblogpost.service.TechBlogPostService;
import com.drrr.infra.push.repository.PushStatusRepository;
import com.drrr.infra.push.service.PushService;
import java.time.LocalDate;
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
    
    public List<TechBlogPostCategoryDto> execute(final Long memberId, final PushDateRequest request) {
        List<Long> postIds = pushService.findMemberPushDateRange(memberId, request.from(), request.to());

        return techBlogPostService.findPushPosts(postIds);
    }


}
