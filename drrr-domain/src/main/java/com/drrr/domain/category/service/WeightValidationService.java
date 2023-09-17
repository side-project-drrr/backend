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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class WeightValidationService {
    private final CategoryWeightRepository categoryWeightRepository;

    /**
     * 사용자에게 블로그 게시물을 추천해줄 때나 사용자가 특정 게시물을 읽을 때 호출
     */
    public void validateWeight(Long memberId) {
        List<CategoryWeight> categoryWeights = categoryWeightRepository.findCategoryWeightsByMemberId(memberId)
                .orElseThrow(() -> new NoSuchElementException("No Member Weight found with memberId: " + memberId));

        List<CategoryWeight> newCategoryWeights  = categoryWeights.parallelStream()
                .map((categoryWeight -> {
                    CategoryWeightDto weightDto = CategoryWeightDto.builder()
                            .id(categoryWeight.getId())
                            .member(categoryWeight.getMember())
                            .category(categoryWeight.getCategory())
                            .value(categoryWeight.getValue())
                            .preferred(categoryWeight.isPreferred())
                            .updatedAt(categoryWeight.getUpdatedAt())
                            .build();

                    return calculateMemberWeight(memberId, weightDto);
                }))
                .filter(weight -> weight != null)
                .map((categoryWeightDto) -> {
                    return CategoryWeight.builder()
                            .member(categoryWeightDto.member())
                            .category(categoryWeightDto.category())
                            .value(categoryWeightDto.value())
                            .preferred(categoryWeightDto.preferred())
                            .build();
                })
                .toList();

        categoryWeightRepository.saveAll(newCategoryWeights);
    }

    private CategoryWeightDto calculateMemberWeight(final Long memberId, CategoryWeightDto categoryWeightDto) {
        LocalDateTime lastUpdatedAt = categoryWeightDto.updatedAt();
        double weightValue = categoryWeightDto.value();
        boolean isPreferred = categoryWeightDto.preferred();
        double minusWeight = getDecreasedWeightValueByHours(lastUpdatedAt);

        //가중치가 최대값을 넘으면 최대값에서 감소, 최대값이 아니라면 현재 값에서 감소
        weightValue -= weightValue > WeightConstants.MAX_WEIGHT.getValue() ? WeightConstants.MAX_WEIGHT.getValue()
                - minusWeight : weightValue - minusWeight;

        //가중치가 0 이하이고 선호하는 카테고리라면 최소 선호 가중치 MIN_CONDITIONAL_WEIGHT 할당
        weightValue -=
                weightValue <= 0 && isPreferred ? WeightConstants.MIN_CONDITIONAL_WEIGHT.getValue() : weightValue;

        //weightValue가 0보다 작거나 마지막 읽은 날짜로부터 현재까지 상수(UNREAD_DAYS)일만큼 이상 읽지 않은 게시물이면 데이터 삭제
        //N.xx에 대한 소수점 xx는 무시한 상태로 MIN_WEIGHT인지 검사
        if ((int) weightValue <= WeightConstants.MIN_WEIGHT.getValue() || isUnreadPastDays(lastUpdatedAt)) {
            int result = categoryWeightRepository.deleteUselessCategoryWeightData(memberId,
                    categoryWeightDto.category().getId()).orElseThrow(() -> new RuntimeException(
                    "해당 엔티티를 삭제할 수 없습니다 -> memberId : " + memberId + " categoryId: " + categoryWeightDto.category()
                            .getId()));
            return null;
        }

        return categoryWeightDto.updateValue(weightValue);
    }

    /**
     * 마지막 본 특정 카테고리에 대한 게시물에 대해 읽은 지 8시간이 지났을 때마다 가중치 1씩 감소
     */
    private double getDecreasedWeightValueByHours(LocalDateTime pastTime) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(pastTime, now);
        long diffHours = duration.toHours();
        double quotient = diffHours / HoursConstants.PAST_HOURS.getValue();
        return quotient * WeightConstants.DECREASE_WEIGHT.getValue();
    }

    private boolean isUnreadPastDays(LocalDateTime unreadDays) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(unreadDays, now);
        long diffHours = duration.toDays();

        return diffHours == DaysConstants.UNREAD_DAYS.getValue();
    }


}
