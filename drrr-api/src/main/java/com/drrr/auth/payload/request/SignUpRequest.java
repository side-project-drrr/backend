package com.drrr.auth.payload.request;


import com.drrr.domain.member.service.RegisterMemberService.RegisterMemberDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import lombok.Builder;

@Builder
public record SignUpRequest(
        @Schema(description = "사용자 이메일", example = "xxxx@xxxx.com")
        @Email(message = "알맞은 이메일 형식이 아닙니다")
        @NotNull
        String email,
        @Schema(description = "카테고리 id", example = "[1, 2, 3]")
        List<@NotNull(message = "사용자의 선호 카테고리id를 지정해주세요") Long> categoryIds,
        @Schema(description = "사용자 별명", example = "텐시")
        @Pattern(regexp = "^[a-zA-Z0-9가-힣]+$", message = "닉네임은 알파벳, 숫자, 한글만 포함할 수 있습니다.")
        @NotNull
        String nickname,
        @Schema(description = "소셜 로그인 주최", nullable = true, example = "kakao")
        @Pattern(regexp = "^[a-zA-Z]*$", message = "provider는 영문자로만 이루어져야 합니다")
        String provider,
        @Schema(description = "소셜 로그인 id", example = "12343")
        String providerId,

        @Schema(description = "소셜 로그인 프로파일 url", example = "https~")
        String profileImageUrl
) {

    public RegisterMemberDto toRegisterMemberDto() {
        return RegisterMemberDto.builder()
                .email(email)
                .nickname(nickname)
                .provider(provider)
                .providerId(providerId)
                .profileImageUrl(profileImageUrl)
                .build();
    }
}
