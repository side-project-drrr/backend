package com.drrr.recommand.service.impl;

import com.drrr.domain.category.service.MemberViewWeightService;
import com.drrr.domain.category.service.WeightValidationService;
import com.drrr.domain.log.service.LogUpdateService;
import com.drrr.recommand.dto.AdjustPostWeightRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ExternalMemberPostReadService {
    private final WeightValidationService weightValidationService;
    private final MemberViewWeightService memberViewWeightService;
    private final LogUpdateService logUpdateService;

    @Transactional
    public void execute(final AdjustPostWeightRequest request, final Long memberId, final Long postId) {
        //가중치 검증
        weightValidationService.validateWeight(memberId);
        //조회수 증가
        memberViewWeightService.increaseMemberViewPost(memberId, postId,
                request.categoryIds());

        //로깅 및 히스토리 데이터 insert
        logUpdateService.insertMemberLogAndHistory(memberId, postId);
    }
}
