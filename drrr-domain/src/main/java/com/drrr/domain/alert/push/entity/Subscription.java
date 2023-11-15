package com.drrr.domain.alert.push.entity;

import com.drrr.domain.jpa.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@Table(name = "DRRR_SUBSCRIPTION")
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@PrimaryKeyJoinColumn(name = "SUBSCRIPTION_ID")
public class Subscription extends BaseEntity {
    @Column(length = 500)
    private String endpoint;
    private String expirationTime;
    private String p256dh; // public key
    private String auth;   // authentication secret
    private Long memberId;
}
