package com.drrr.auth.controller;

import com.drrr.auth.payload.response.EmailCheckResponse;
import com.drrr.auth.service.impl.EmailCheckService;
import com.drrr.domain.email.service.VerificationService.VerificationDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class CheckController {
    private final EmailCheckService emailCheckService;

    @Operation(summary = "이메일 중복확인 API", description = "호출 성공 시 이메일 중복 확인 여부")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이메일 중복 확인 여부 반환", content = @Content(schema = @Schema(implementation = VerificationDto.class)))
    })
    @GetMapping("/check/email")
    public EmailCheckResponse EmailDuplicateCheck(@RequestParam("email") final String email) {
        return emailCheckService.execute(email);
    }
}
