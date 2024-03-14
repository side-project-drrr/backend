package com.drrr.alarm.service.impl;

import com.drrr.alarm.service.request.PushDateRequest;
import com.drrr.domain.techblogpost.dto.TechBlogPostCategoryDto;
import com.drrr.domain.techblogpost.service.TechBlogPostService;
import com.drrr.infra.push.repository.PushStatusRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ExternalSearchPushPostsService {
    private final PushStatusRepository pushStatusRepository;
    private final TechBlogPostService techBlogPostService;

    public List<TechBlogPostCategoryDto> execute(final Long memberId, final PushDateRequest request) {
        //푸시 open 상태 변경 - 알림 아이콘의 느낌표 표시 클릭 시 상태 변경
        pushStatusRepository.updatePushOpenStatus(memberId, request.from(), request.to());

        List<Long> postIds = pushStatusRepository.findPostIdsByMemberIdAndPushDateRange(
                memberId, request.from(), request.to());
        List<TechBlogPostCategoryDto> pushPosts = techBlogPostService.findPushPosts(postIds);

        return techBlogPostService.findPushPosts(postIds);
    }

    public List<TechBlogPostCategoryDto> execute(final Long memberId, final LocalDate pushDate) {
        //푸시 상태 변경
        pushStatusRepository.updatePushStatus(memberId, pushDate);

        final List<Long> postIds = pushStatusRepository.findPostIdsByMemberIdAndPushDate(memberId, pushDate);
        return techBlogPostService.findPushPosts(postIds);
    }
}
