package com.drrr.auth.controller;

import com.drrr.auth.payload.request.EmailRequest;
import com.drrr.auth.payload.request.EmailVerificationRequest;
import com.drrr.auth.service.impl.EmailVerificationService;
import com.drrr.auth.service.impl.IssuanceVerificationCode;
import com.drrr.domain.email.service.VerificationService.VerificationDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class EmailController {
    private final EmailVerificationService emailVerificationService;
    private final IssuanceVerificationCode issuanceVerificationCode;

    @Operation(summary = "이메일 인증코드 검증 API", description = "호출 성공 시 이메일 인증코드 검증")
    @ApiResponses(@ApiResponse(
            responseCode = "200",
            description = "providerId 및 이메일 인증코드 검증 여부 반환 [true : 인증 성공, false : 인증 실패] 반환")
    )
    @PostMapping("/verify-email")
    public VerificationDto executeEmailVerification(@RequestBody final EmailVerificationRequest request) {
        return emailVerificationService.execute(request);
    }

    @Operation(summary = "이메일 인증코드 발급 API", description = "호출 성공 시 이메일 인증코드 발급")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "이메일 인증코드 발급"))
    @PostMapping("/send-verification-email")
    public void createEmailVerification(@RequestBody final EmailRequest emailRequest) {
        issuanceVerificationCode.execute(emailRequest);
    }
}
