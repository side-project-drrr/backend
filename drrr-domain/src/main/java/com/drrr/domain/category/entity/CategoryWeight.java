package com.drrr.domain.category.entity;

import static com.drrr.core.recommandation.constant.constant.WeightConstants.INCREASE_WEIGHT;
import static com.drrr.core.recommandation.constant.constant.WeightConstants.MIN_CONDITIONAL_WEIGHT;
import static com.drrr.core.recommandation.constant.constant.WeightConstants.MIN_WEIGHT;

import com.drrr.core.recommandation.constant.constant.DaysConstants;
import com.drrr.core.recommandation.constant.constant.HoursConstants;
import com.drrr.core.recommandation.constant.constant.PostConstants;
import com.drrr.core.recommandation.constant.constant.WeightConstants;
import com.drrr.domain.category.dto.CategoryWeightDto;
import com.drrr.domain.jpa.entity.BaseEntity;
import com.drrr.domain.member.entity.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "DRRR_CATEGORY_WEIGHT")
@Entity
@PrimaryKeyJoinColumn(name = "CATEGORY_WEIGHT_ID")
public class CategoryWeight extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    private double value;

    private boolean preferred;

    /**
     * 카테고리 별로 몇개의 게시물을 추천해줄 건지 계산 return : key - 카테고리_아이디, value - 카테고리별 추천 게시물 수 categoryWeightDtos - 가장 최근 게시물 순으로
     * 정렬되어 있는 상태
     */
    public static Map<Long, Integer> calculatePostDistribution(List<CategoryWeightDto> categoryWeightDtos) {
        int totalPosts = PostConstants.RECOMMEND_POSTS_COUNT.getValue();

        // Calculate the total weight
        double totalWeight = categoryWeightDtos.stream().mapToDouble(CategoryWeightDto::value).sum();

        // key : categoryId, value : 가중치 백분율
        Map<Long, Double> fractionalPartsMap = new LinkedHashMap<>();
        Map<Long, Integer> resultMap = new HashMap<>();

        // 카테고리별 게시물 개수
        int remainingPosts = categoryWeightDtos.stream()
                .map(dto -> {
                    double rawPostCount = (dto.value() / totalWeight) * totalPosts;
                    int postCount = (int) rawPostCount;
                    double fractionalPart = rawPostCount - postCount;

                    fractionalPartsMap.put(dto.category().getId(), fractionalPart);
                    resultMap.put(dto.category().getId(), postCount);

                    return postCount;
                })
                .reduce(totalPosts, (a, b) -> a - b);

        //가중치 비율이 제일 높은 순
        //key : 카테고리Id, value : 가중치
        fractionalPartsMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .filter(entry -> resultMap.containsKey(entry.getKey()))
                .limit(remainingPosts)
                .forEach(entry -> resultMap.put(entry.getKey(), resultMap.get(entry.getKey()) + 1));

        return resultMap;
    }

    private double getDecreasedWeightValueByHours(LocalDateTime pastTime, LocalDateTime now) {
        Duration duration = Duration.between(pastTime, now);
        long diffHours = duration.toHours();
        long quotient = diffHours / HoursConstants.PAST_HOURS.getValue();
        return quotient * WeightConstants.DECREASE_WEIGHT.getValue();
    }

    public boolean isExpiredCategoryWeight(){
        return MIN_WEIGHT.isLessEqualThan(this.value) || isUnreadPastDays(updatedAt);
    }

    private boolean isUnreadPastDays(LocalDateTime unreadDays) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(unreadDays, now);
        long diffHours = duration.toDays();

        return diffHours == DaysConstants.UNREAD_DAYS.getValue();
    }

    public void accumulateWeight() {
        this.value = INCREASE_WEIGHT.sum(this.value);
    }

    /**
     * 마지막 본 특정 카테고리에 대한 게시물에 대해 읽은 지 8시간이 지났을 때마다 가중치 1씩 감소
     */
    public void calculateMemberWeight(LocalDateTime dateTime) {
        LocalDateTime lastUpdatedAt = this.updatedAt;
        double weightValue = this.value;
        boolean isPreferred = this.preferred;
        double minusWeight = getDecreasedWeightValueByHours(lastUpdatedAt, dateTime);
        weightValue = decreaseWeightValue(weightValue, minusWeight, isPreferred);
        this.value = weightValue;
    }

    private double decreaseWeightValue(double weightValue, double minusWeight, boolean isPreferred) {
        double updateWeight = weightValue - minusWeight;
        //updateWeight이 설정한 최저 가중치, 최대 가중치 사이에 있으면 그대로 가져가고 벗어나면 최고치 or 최저치로 설정
        updateWeight = Math.max(WeightConstants.MIN_WEIGHT.getValue(),
                Math.min(updateWeight, WeightConstants.MAX_WEIGHT.getValue()));

        //선호하는 카테고리에 대해서는 최소 가중치 할당
        if (updateWeight < MIN_CONDITIONAL_WEIGHT.getValue() && isPreferred) {
            updateWeight = WeightConstants.MIN_CONDITIONAL_WEIGHT.getValue();
        }
        return updateWeight;
    }
}
