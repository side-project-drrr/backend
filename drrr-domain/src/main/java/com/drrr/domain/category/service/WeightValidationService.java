package com.drrr.domain.category.service;

import com.drrr.core.recommandation.constant.constant.DaysConstants;
import com.drrr.core.recommandation.constant.constant.HoursConstants;
import com.drrr.core.recommandation.constant.constant.WeightConstants;
import com.drrr.domain.category.dto.CategoryWeightDto;
import com.drrr.domain.category.entity.CategoryWeight;
import com.drrr.domain.category.repository.CategoryWeightRepository;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
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
    public void validateWeight(Long memberId) {
        // 가중치 회원 테이블에서 조회
        List<CategoryWeight> categoryWeights = categoryWeightRepository.findCategoryWeightsByMemberId(memberId);

        if(categoryWeights.isEmpty()){
            throw new IllegalArgumentException("No Member Weight found with memberId: " + memberId);
        }

        categoryWeights.parallelStream()
                .peek(CategoryWeight::calculateMemberWeight)
                .filter(CategoryWeight::isExpiredCategoryWeight)
                .forEach(categoryWeightRepository::delete);
    }
}
