package com.drrr.recommand.service.impl;

import com.drrr.domain.category.service.MemberViewWeightService;
import com.drrr.domain.category.service.WeightValidationService;
import com.drrr.domain.log.service.LogUpdateService;
import com.drrr.domain.techblogpost.repository.TechBlogPostCategoryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ExternalMemberPostReadService {
    private final WeightValidationService weightValidationService;
    private final MemberViewWeightService memberViewWeightService;
    private final TechBlogPostCategoryRepository techBlogPostCategoryRepository;
    private final LogUpdateService logUpdateService;

    @Transactional
    public void execute(final Long memberId, final Long postId) {
        //가중치 검증
        weightValidationService.validateWeight(memberId);
        //카테고리 조회
        List<Long> categoryIds = techBlogPostCategoryRepository.findCategoriesByPostId(postId);
        //조회수 증가
        memberViewWeightService.increaseMemberViewPost(memberId, postId, categoryIds);
        //로깅 및 히스토리 데이터 insert
        logUpdateService.insertMemberLogAndHistory(memberId, postId);
    }
}
