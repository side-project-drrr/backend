package com.drrr.domain.category.service;


import com.drrr.domain.category.entity.CategoryWeight;
import com.drrr.domain.category.repository.CategoryWeightRepository;
import com.drrr.domain.exception.DomainExceptionCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class WeightValidationService {
    private final CategoryWeightRepository categoryWeightRepository;

    /**
     * 사용자에게 블로그 게시물을 추천해줄 때나 사용자가 특정 게시물을 읽을 때 호출
     */
    public void validateWeight(final Long memberId) {
        // 가중치 회원 테이블에서 조회
        final List<CategoryWeight> categoryWeights = categoryWeightRepository.findByMemberId(memberId);

        if (categoryWeights.isEmpty()) {
            log.error("사용자 가중치를 찾을 수 없습니다.");
            log.error("memberId -> {}", memberId);
            throw DomainExceptionCode.INVALID_AUTHORIZE_CODE.newInstance();
        }

        categoryWeights.stream()
                .peek(CategoryWeight::calculateMemberWeight)
                .filter(CategoryWeight::isExpiredCategoryWeight)
                .forEach(categoryWeightRepository::delete);
    }
}
