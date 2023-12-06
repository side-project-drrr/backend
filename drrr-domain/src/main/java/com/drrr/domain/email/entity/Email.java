package com.drrr.domain.email.entity;

import com.drrr.domain.jpa.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "DRRR_EMAIL")
@PrimaryKeyJoinColumn(name = "EMAIL_ID")
public class Email extends BaseEntity {
    private String providerId;
    private String email;
    private String verificationCode;
    private boolean isVerified;
}
