package com.drrr.member.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record ProfileUpdateRequest(
        @Pattern(regexp = "^[a-zA-Z0-9가-힣]+$", message = "닉네임은 알파벳, 숫자, 한글만 포함할 수 있습니다.")
        String nickname,
        @Email(message = "알맞은 이메일 형식이 아닙니다")
        String email
) {
}
