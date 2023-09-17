package com.drrr.recommand.service.impl;

import com.drrr.domain.category.service.MemberViewWeightService;
import com.drrr.domain.category.service.WeightValidationService;
import com.drrr.recommand.dto.AdjustPostWeightRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ExternalMemberPostReadService {
    private final WeightValidationService weightValidationService;
    private final MemberViewWeightService memberViewWeightService;

    public void execute(AdjustPostWeightRequest request) {
        weightValidationService.validateWeight(request.getMemberId());
        memberViewWeightService.increaseMemberViewPost(request.getMemberId(), request.getPostId(),
                request.getCategoryIds());
    }
}
