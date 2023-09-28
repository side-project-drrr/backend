package com.drrr.auth.payload.request;


import com.drrr.core.code.Gender;
import com.drrr.domain.category.entity.Category;
import com.drrr.domain.member.service.RegisterMemberService.RegisterMemberDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
    @Schema(description = "access token", nullable = false, example = "[access token]")
    @NotNull
    private String accessToken;
    @Schema(description = "사용자 이메일", nullable = false, example = "xxxx@xxxx.com")
    @Email(message = "알맞은 이메일 형식이 아닙니다")
    @NotNull
    private String email;

    @Schema(description = "카테고리 id", nullable = false, example = "[1, 2, 3]")
    private List<@NotNull(message ="사용자의 선호 카테고리id를 지정해주세요")Long> categoryIds;

    @Schema(description = "사용자 별명", nullable = false, example = "홍길동")
    @NotNull
    private String nickname;
    @Schema(description = "사용자 전화번호", nullable = true, example = "010xxxxxxxx")
    @Pattern(regexp="^[0-9]*$", message ="전화번호는 숫자만 입력가능합니다")
    private String phoneNumber;
    @Schema(description = "성별", nullable = false, example = "MAN or WOMAN")
    private Gender gender;
    @Schema(description = "생일", nullable = false, example = "1900412")
    @Pattern(regexp="^\\d{8}$", message = "생일은 YYYYMMDD 형태로 이루어져야 합니다")
    private String birthYear;
    @Schema(description = "소셜 로그인 주최", nullable = true, example = "kakao")
    @Pattern(regexp="^[a-zA-Z]*$", message = "provider는 영문자로만 이루어져야 합니다")
    private String provider;
    @Schema(description = "소셜 로그인 id", nullable = true, example = "12343")
    @Pattern(regexp="^[0-9]*$",message = "providerId는 숫자만 입력가능합니다")
    private String providerId;
    @Schema(description = "CDN 이미지 url", nullable = false, example = "https://~")
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
