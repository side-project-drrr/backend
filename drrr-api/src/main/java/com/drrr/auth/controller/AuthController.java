package com.drrr.auth.controller;

import com.drrr.auth.payload.dto.OAuth2Response;
import com.drrr.auth.payload.request.AccessTokenRequest;
import com.drrr.auth.payload.request.SignInRequest;
import com.drrr.auth.payload.request.SignOutRequest;
import com.drrr.auth.payload.request.SignUpRequest;
import com.drrr.auth.payload.response.AccessTokenResponse;
import com.drrr.auth.payload.response.SignInResponse;
import com.drrr.auth.payload.response.SignUpResponse;
import com.drrr.auth.service.impl.ExchangeOAuth2AccessTokenService;
import com.drrr.auth.service.impl.IssuanceTokenService;
import com.drrr.auth.service.impl.SignInService;
import com.drrr.auth.service.impl.SignUpService;
import com.drrr.auth.service.impl.UnregisterService;
import com.drrr.web.annotation.MemberId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
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
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final SignUpService signUpService;
    private final SignInService signInService;
    private final UnregisterService unregisterService;
    private final IssuanceTokenService issuanceTokenService;
    private final ExchangeOAuth2AccessTokenService exchangeOAuth2AccessTokenService;

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

    @Operation(summary = "로그아웃 API", description = """
            호출 성공 시 기존 토큰 레디스에서 제거 및 key : access token, value : "logout", ttl : 기존 token의 ttl만큼 삽입""
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그아웃 성공", content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PostMapping("/signout")
    public void signOut(@Validated @RequestBody final SignOutRequest signOutRequest) {
        issuanceTokenService.negateToken(signOutRequest);
    }

    @Operation(summary = "토큰 재발급", description = "호출 성공 시 JWT Access 토큰 반환")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "access token 재발급 성공", content = @Content(schema = @Schema(implementation = AccessTokenResponse.class)))
    })
    @PostMapping("/access-token")
    public AccessTokenResponse regenerateAccessToken(
            @Validated @RequestBody final AccessTokenRequest request) {
        return issuanceTokenService.regenerateAccessToken(request);
    }

    @Operation(summary = "회원탈퇴 API - [JWT TOKEN REQUIRED]", description = "호출 성공 시 회원탈퇴")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원탈퇴 성공", content = @Content(schema = @Schema(implementation = String.class)))
    })
    @Secured("USER")
    @DeleteMapping("/member/unregister")
    public void memberUnregister(@MemberId final Long memberId) {
        unregisterService.execute(memberId);
    }
    
}
