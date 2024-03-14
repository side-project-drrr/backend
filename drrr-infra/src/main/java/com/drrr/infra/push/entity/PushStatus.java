package com.drrr.infra.push.entity;

import com.drrr.domain.jpa.entity.BaseEntity;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@Table(name = "DRRR_PUSH_STATUS")
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@PrimaryKeyJoinColumn(name = "PUSH_STATUS_ID")
public class PushStatus extends BaseEntity {
    private LocalDate pushDate;
    private Long memberId;
    @ElementCollection
    @CollectionTable(name = "drrr_push_post_ids", joinColumns = @JoinColumn(name = "push_status_id"))
    @Column(name = "post_id")
    private List<Long> postIds;
    //푸시가 갔는지 여부
    private boolean pushStatus;
    //알림 내용에서 읽은 것 여부
    private boolean readStatus;
    //알림 아이콘에 느낌표 표시 여부
    private boolean openStatus;
}
