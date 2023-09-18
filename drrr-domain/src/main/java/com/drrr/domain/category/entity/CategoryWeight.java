package com.drrr.domain.category.entity;

import static com.drrr.core.recommandation.constant.constant.WeightConstants.INCREASE_WEIGHT;
import static com.drrr.core.recommandation.constant.constant.WeightConstants.MIN_WEIGHT;

import com.drrr.core.recommandation.constant.constant.DaysConstants;
import com.drrr.core.recommandation.constant.constant.HoursConstants;
import com.drrr.core.recommandation.constant.constant.WeightConstants;
import com.drrr.domain.category.dto.CategoryWeightDto;
import com.drrr.domain.jpa.entity.BaseEntity;
import com.drrr.domain.member.entity.Member;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Table(name = "DRRR_CATEGORY_WEIGHT")
@Entity
@PrimaryKeyJoinColumn(name = "CATEGORY_WEIGHT_ID")
public class CategoryWeight extends BaseEntity {
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "member_id")
    private Member member;


    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "category_id")
    private Category category;

    private double value;

    private boolean preferred;



    private double getDecreasedWeightValueByHours(LocalDateTime pastTime) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(pastTime, now);
        long diffHours = duration.toHours();
        long quotient =  diffHours / HoursConstants.PAST_HOURS.getValue();
        return quotient * WeightConstants.DECREASE_WEIGHT.getValue();
    }
    /**
     * 마지막 본 특정 카테고리에 대한 게시물에 대해 읽은 지 8시간이 지났을 때마다 가중치 1씩 감소
     */
    public void calculateMemberWeight() {
        LocalDateTime lastUpdatedAt = this.updatedAt;
        double weightValue = this.value;
        boolean isPreferred = this.preferred;
        double minusWeight = getDecreasedWeightValueByHours(lastUpdatedAt);

        //가중치가 최대값을 넘으면 최대값에서 감소, 최대값이 아니라면 현재 값에서 감소
        weightValue -= weightValue > WeightConstants.MAX_WEIGHT.getValue() ? WeightConstants.MAX_WEIGHT.getValue()
                - minusWeight : weightValue - minusWeight;

        //가중치가 0 이하이고 선호하는 카테고리라면 최소 선호 가중치 MIN_CONDITIONAL_WEIGHT 할당
        weightValue -=
                weightValue <= 0 && isPreferred ? WeightConstants.MIN_CONDITIONAL_WEIGHT.getValue() : weightValue;

        this.value = weightValue;
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
}
