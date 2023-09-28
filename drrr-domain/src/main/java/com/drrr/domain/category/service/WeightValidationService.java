package com.drrr.domain.category.service;

import com.drrr.domain.category.entity.CategoryWeight;
import com.drrr.domain.category.repository.CategoryWeightRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Service
@Transactional
public class WeightValidationService {
    private final CategoryWeightRepository categoryWeightRepository;

    /**
     * 사용자에게 블로그 게시물을 추천해줄 때나 사용자가 특정 게시물을 읽을 때 호출
     */
    public void validateWeight(Long memberId, LocalDateTime now) {
        // 가중치 회원 테이블에서 조회
        List<CategoryWeight> categoryWeights = categoryWeightRepository.findCategoryWeightsByMemberId(memberId);

        if (categoryWeights.isEmpty()) {
            throw new IllegalArgumentException("No Member Weight found with memberId: " + memberId);
        }

        categoryWeights.stream()
                .peek(categoryWeight -> categoryWeight.calculateMemberWeight(now))
                .filter(CategoryWeight::isExpiredCategoryWeight)
                .forEach(categoryWeightRepository::delete);
    }
}
