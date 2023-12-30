package com.drrr.auth.controller;

import com.drrr.auth.payload.dto.OAuth2Response;
import com.drrr.auth.payload.request.AccessTokenRequest;
import com.drrr.auth.payload.request.EmailRequest;
import com.drrr.auth.payload.request.EmailVerificationRequest;
import com.drrr.auth.payload.request.SignInRequest;
import com.drrr.auth.payload.request.SignUpRequest;
import com.drrr.auth.payload.response.AccessTokenResponse;
import com.drrr.auth.payload.response.SignInResponse;
import com.drrr.auth.payload.response.SignUpResponse;
import com.drrr.auth.service.impl.EmailVerificationService;
import com.drrr.auth.service.impl.ExchangeOAuth2AccessTokenService;
import com.drrr.auth.service.impl.IssuanceTokenService;
import com.drrr.auth.service.impl.IssuanceVerificationCode;
import com.drrr.auth.service.impl.SignInService;
import com.drrr.auth.service.impl.SignUpService;
import com.drrr.auth.service.impl.UnregisterService;
import com.drrr.domain.email.service.VerificationService.VerificationDto;
import com.drrr.web.interceptor.annotation.Auth;
import com.drrr.web.resolver.annotation.UserId;
import com.drrr.web.security.annotation.UserAuthority;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
@UserAuthority
@RequestMapping("/auth")
public class AuthController {
    private final SignUpService signUpService;
    private final SignInService signInService;
    private final UnregisterService unregisterService;
    private final IssuanceTokenService issuanceTokenService;
    private final ExchangeOAuth2AccessTokenService exchangeOAuth2AccessTokenService;
    private final IssuanceVerificationCode issuanceVerificationCode;
    private final EmailVerificationService emailVerificationService;


    @Operation(summary = "Front에서 준 code로 provider ID를 반환하는 API",
            description = "호출 성공 시 provider id와 isRegistered(isRegistered : false (신규회원) true (기존회원)), profile image url 반환",
            parameters = {
                    @Parameter(name = "code", description = "OAuth2 인증코드", in = ParameterIn.QUERY, schema = @Schema(type = "string")),
                    @Parameter(name = "state", description = "OAuth2 주체", in = ParameterIn.QUERY, schema = @Schema(type = "string"))
            })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "신규 회원 여부 및 provider id 반환", content = @Content(schema = @Schema(implementation = OAuth2Response.class)))
    })
    @GetMapping("/oauth2/profile")
    public OAuth2Response exchangeOAuth2AccessToken(@NotNull @RequestParam("code") final String code,
                                                    @NotNull @RequestParam("state") final String provider) {
        return exchangeOAuth2AccessTokenService.execute(code, provider);
    }

    @Operation(summary = "소셜 로그인 회원가입 호출 API", description = "호출 성공 시 JWT Access, Refresh 토큰 반환")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공", content = @Content(schema = @Schema(implementation = SignUpResponse.class)))
    })
    @PostMapping("/signup")
    public SignUpResponse signUp(@Validated @RequestBody final SignUpRequest signUpRequest) {
        return signUpService.execute(signUpRequest);
    }

    @Operation(summary = "소셜 로그인 API", description = "호출 성공 시 JWT Access, Refresh 토큰 반환")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(schema = @Schema(implementation = SignInResponse.class)))
    })
    @PostMapping("/signin")
    public SignInResponse signIn(@Validated @RequestBody final SignInRequest signInRequest) {
        return signInService.execute(signInRequest);
    }

    @Operation(summary = "토큰 재발급", description = "호출 성공 시 JWT Access 토큰 반환")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "access token 재발급 성공", content = @Content(schema = @Schema(implementation = AccessTokenResponse.class)))
    })
    @PostMapping("/access-token")
    public ResponseEntity<AccessTokenResponse> regenerateAccessToken(
            @Validated @RequestBody final AccessTokenRequest request) {
        AccessTokenResponse accessTokenResponse = issuanceTokenService.regenerateAccessToken(request);
        return ResponseEntity.ok(accessTokenResponse);
    }

    @Operation(summary = "이메일 인증코드 발급 API", description = "호출 성공 시 이메일 인증코드 발급")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이메일 인증코드 발급, String type", content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PostMapping("/email")
    public ResponseEntity<String> createEmailVerification(@RequestBody final EmailRequest emailRequest) {
        issuanceVerificationCode.execute(emailRequest);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "이메일 인증코드 확인 API", description = "호출 성공 시 이메일 인증코드 확인 여부")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이메일 인증코드 확인 여부 반환", content = @Content(schema = @Schema(implementation = VerificationDto.class)))
    })
    @PostMapping("/email/verification")
    public VerificationDto executeEmailVerification(@RequestBody final EmailVerificationRequest request) {
        return emailVerificationService.execute(request);
    }

    @Auth
    @Operation(summary = "회원탈퇴 API", description = "호출 성공 시 회원탈퇴")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원탈퇴 성공", content = @Content(schema = @Schema(implementation = String.class)))
    })
    @DeleteMapping("/member/unregister")
    public ResponseEntity<String> memberUnregister(@UserId final Long memberId) {
        unregisterService.execute(memberId);
        return ResponseEntity.ok().build();
    }


}
