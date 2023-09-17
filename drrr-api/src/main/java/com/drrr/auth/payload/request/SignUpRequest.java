package com.drrr.auth.payload.request;


import com.drrr.core.code.Gender;
import com.drrr.domain.category.entity.Category;
import com.drrr.domain.member.service.RegisterMemberService.RegisterMemberDto;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class SignUpRequest {
    @NonNull
    private String accessToken;
    @NonNull
    private String email;

    private List<Long> categoryIds;

    private String nickname;
    private String phoneNumber;
    private Gender gender;
    private String birthYear;
    private String provider;
    private String providerId;
    private String imageUrl;

    public RegisterMemberDto toRegisterMemberDto() {
        return RegisterMemberDto.builder()
                .accessToken(accessToken)
                .email(email)
                .nickname(nickname)
                .phoneNumber(phoneNumber)
                .gender(gender)
                .birthYear(birthYear)
                .provider(provider)
                .providerId(providerId)
                .imageUrl(imageUrl)
                .build();
    }
}
