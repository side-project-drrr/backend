package com.drrr.domain.member.entity;

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

@Builder
@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "DRRR_MEMBER")
@PrimaryKeyJoinColumn(name = "MEMBER_ID")
public class Member extends BaseEntity {

    @Column(nullable = false)
    private String nickname;

    private String provider;
    @Column(unique = true)
    private String providerId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(unique = true)
    private String profileImageUrl;

    @Column(nullable = false)
    private boolean isActive;

    public static Member createMember(final String email, final String nickname, final String provider,
                                      final String providerId,
                                      final String profileImageUrl) {
        return Member.builder()
                .email(email)
                .nickname(nickname)
                .provider(provider)
                .providerId(providerId)
                .profileImageUrl(profileImageUrl)
                .isActive(true)
                .build();
    }
}
