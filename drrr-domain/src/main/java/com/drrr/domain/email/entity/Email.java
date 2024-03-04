package com.drrr.domain.email.entity;

import com.drrr.domain.jpa.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
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

    private static final int THREE_MINUTES = 180;

    private String providerId;
    private String email;
    private String verificationCode;
    private boolean isVerified;


    public boolean validateExpire(LocalDateTime now) {
        final Duration duration = Duration.between(this.createdAt, now);
        final long differenceInSeconds = duration.toSeconds();

        //3분 안에 입력
        return differenceInSeconds > THREE_MINUTES;
    }

    public boolean matchVerificationCode(String verificationCode) {
        return Objects.equals(this.verificationCode, verificationCode);
    }
}
