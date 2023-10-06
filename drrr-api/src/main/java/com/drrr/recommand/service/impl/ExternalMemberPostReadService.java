package com.drrr.recommand.service.impl;

import com.drrr.domain.category.service.MemberViewWeightService;
import com.drrr.domain.category.service.WeightValidationService;
import com.drrr.domain.log.service.LogUpdateService;
import com.drrr.recommand.dto.AdjustPostWeightRequest;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ExternalMemberPostReadService {
    private final WeightValidationService weightValidationService;
    private final MemberViewWeightService memberViewWeightService;
    private final LogUpdateService logUpdateService;

    public void execute(AdjustPostWeightRequest request, Long memberId, Long postId) {
        weightValidationService.validateWeight(memberId, LocalDateTime.now());
        memberViewWeightService.increaseMemberViewPost(memberId, postId,
                request.categoryIds());
        logUpdateService.insertMemberPostReadLog(memberId, postId);
        logUpdateService.insertMemberPostHistory(memberId, postId);
    }
}
