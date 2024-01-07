package com.drrr.domain.member.entity;

import com.drrr.domain.jpa.entity.BaseEntity;
import com.drrr.domain.member.service.RegisterMemberService.RegisterMemberDto;
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

/*    @Enumerated(EnumType.STRING)
    private MemberRole role;*/

    @Column(unique = true)
    private String profileImageUrl;

    public static Member createMember(String email, String nickname, String provider, String providerId,
                                      String profileImageUrl) {
        return Member.builder()
                .email(email)
                .nickname(nickname)
                .provider(provider)
                .providerId(providerId)
                .profileImageUrl(profileImageUrl)
                .build();
    }

    public static Member createMember(final RegisterMemberDto registerMemberDto) {
        return Member.builder()
                .email(registerMemberDto.email())
                .nickname(registerMemberDto.nickname())
                .provider(registerMemberDto.provider())
                .providerId(registerMemberDto.providerId())
                .build();
    }
}
