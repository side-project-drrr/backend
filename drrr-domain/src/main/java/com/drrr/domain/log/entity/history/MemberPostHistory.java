package com.drrr.domain.log.entity.history;

import com.drrr.domain.jpa.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(name = "DRRR_MEMBER_POST_HISTORY")
@PrimaryKeyJoinColumn(name = "MEMBER_POST_HISTORY_ID")
public class MemberPostHistory extends BaseEntity {
    private Long memberId;
    private Long postId;
}

