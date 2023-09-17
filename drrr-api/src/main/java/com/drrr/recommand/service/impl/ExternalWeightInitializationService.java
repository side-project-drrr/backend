package com.drrr.recommand.service.impl;

import com.drrr.domain.category.service.InitializeWeightService;
import com.drrr.recommand.dto.AdjustPostWeightRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ExternalWeightInitializationService {
    private final InitializeWeightService initializeWeightService;

    public void execute(AdjustPostWeightRequest request) {
        initializeWeightService.initializeCategoryWeight(request.getMemberId(), request.getCategoryIds());
    }
}
