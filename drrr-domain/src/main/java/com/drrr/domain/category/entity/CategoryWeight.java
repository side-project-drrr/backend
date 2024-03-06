package com.drrr.domain.category.entity;

import static com.drrr.core.recommandation.constant.WeightConstants.INCREASE_WEIGHT;
import static com.drrr.core.recommandation.constant.WeightConstants.MIN_CONDITIONAL_WEIGHT;
import static com.drrr.core.recommandation.constant.WeightConstants.MIN_WEIGHT;

import com.drrr.core.recommandation.constant.DaysConstants;
import com.drrr.core.recommandation.constant.HoursConstants;
import com.drrr.core.recommandation.constant.WeightConstants;
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

    private double weightValue;

    private boolean preferred;
    private LocalDateTime lastReadAt;

    /**
     * HoursConstants.PAST_HOURS(시간 값)값마다 WeightConstants.DECREASE_WEIGHT만큼 떨어지는 가중치 값 반환
     */
    private double getDecreasedWeightValueByHours(final LocalDateTime pastTime, final LocalDateTime now) {
        final Duration duration = Duration.between(pastTime, now);
        final long diffHours = duration.toHours();
        final long quotient = diffHours / HoursConstants.PAST_HOURS.getValue();
        return quotient * WeightConstants.DECREASE_WEIGHT.getValue();
    }

    /**
     * MIN_WEIGHT(최소 가중치) 값보다 더 낮거나 동일한 카테고리에 해당하는 기술 블로그를 UNREAD_DAYS에 정의한 값만큼 안 읽었는지 검증 둘 중에 하나라도 해당되면 그 카테고리의 가중치는
     * 초기값으로 변환하기 위함
     */
    public boolean isExpiredCategoryWeight() {
        //선호하지 않는 카테고리에 대해서는 최소 가중치 보다 낮으면 삭제 또는 선호하는 카테고리는 시간이 지나도 만료되지 않음
        return (MIN_WEIGHT.isGreaterThan(this.weightValue) || isUnreadPastDays(this.lastReadAt));
    }

    private boolean isUnreadPastDays(final LocalDateTime unreadDays) {
        //선호하는 카테고리는 시간이 지나도 만료되지 않음
        if (this.preferred) {
            return false;
        }

        final LocalDateTime now = LocalDateTime.now();
        final Duration duration = Duration.between(unreadDays, now);
        final long diffHours = duration.toDays();

        return diffHours >= DaysConstants.UNREAD_DAYS.getValue();
    }

    public void accumulateWeight() {
        this.weightValue = INCREASE_WEIGHT.sum(this.weightValue);
    }

    /**
     * 마지막 본 특정 카테고리에 대한 게시물에 대해 읽은 지 PAST_HOURS 값만큼 시간이 지났을 때마다 가중치 DECREASE_WEIGHT 값만큼 감소
     */
    public void calculateMemberWeight() {
        final LocalDateTime lastUpdatedAt = this.lastReadAt;

        final boolean isPreferred = this.preferred;
        //시간에 따른 가중치 감소값
        final double minusWeight = getDecreasedWeightValueByHours(lastUpdatedAt, LocalDateTime.now());

        //가중치 범위 검증
        this.weightValue = limitWeightValue(this.weightValue, minusWeight, isPreferred);
    }

    /**
     * 카테고리의 가중치가 MIN_WEIGHT과 MAX_WEIGHT의 사이에 있는지 검증 MIN_WEIGHT의 값보다 카테고리 가중치가 적으면 MIN_WEIGHT 값으로 설정 (선호가중치는
     * MIN_CONDITIONAL_WEIGHT로 설정) MAX_WEIGHT 보다 높으면 MAX_WEIGHT 값으로 설정
     */
    private double limitWeightValue(final double weightValue, final double minusWeight, final boolean isPreferred) {
        double updateWeight = weightValue - minusWeight;
        //최대 가중치를 넘어갈 경우 MAX_WEIGHT으로 설정
        updateWeight = Math.max(Math.min(updateWeight, WeightConstants.MAX_WEIGHT.getValue()), MIN_WEIGHT.getValue());
        //선호하는 카테고리가 선호카테고리가중치 최소값에 못 미치면 선호카테고리가중치 값으로 할당
        if (updateWeight < MIN_CONDITIONAL_WEIGHT.getValue() && isPreferred) {
            updateWeight = WeightConstants.MIN_CONDITIONAL_WEIGHT.getValue();
        }
        return updateWeight;
    }
}
