package com.drrr.auth.controller;

import com.drrr.auth.payload.dto.OAuth2Response;
import com.drrr.auth.payload.request.AccessTokenRequest;
import com.drrr.auth.payload.request.OAuth2ProfileRequest;
import com.drrr.auth.payload.request.SignInRequest;
import com.drrr.auth.payload.request.SignOutRequest;
import com.drrr.auth.payload.request.SignUpRequest;
import com.drrr.auth.payload.response.AccessTokenResponse;
import com.drrr.auth.payload.response.CheckResponse;
import com.drrr.auth.payload.response.SignInResponse;
import com.drrr.auth.payload.response.SignUpResponse;
import com.drrr.auth.service.impl.ExchangeOAuth2AccessTokenService;
import com.drrr.auth.service.impl.IssuanceTokenService;
import com.drrr.auth.service.impl.SignInService;
import com.drrr.auth.service.impl.SignUpService;
import com.drrr.auth.service.impl.UnregisterService;
import com.drrr.web.annotation.MemberId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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

    @Operation(summary = "클라이언트에서 준 code와 state(소셜 로그인 주체)로 provider ID를 반환하는 API",
            description = """
                            호출 성공 시 provider id,
                            isRegistered(isRegistered : false [신규회원] true [기존회원]),
                            profile image url 반환
                    """)
    @ApiResponses(@ApiResponse(responseCode = "200", description = "신규 회원 여부 및 provider id 반환"))
    @GetMapping("/oauth2/profile")
    public OAuth2Response exchangeOAuth2AccessToken(
            @Validated @ModelAttribute OAuth2ProfileRequest request) {
        return exchangeOAuth2AccessTokenService.execute(request.code(), request.state());
    }

    @Operation(summary = "사용자 회원가입시 닉네임 중복 체크 API",
            description = """
                            호출 성공 시 닉네임 중복 여부 반환
                    """)
    @ApiResponses(@ApiResponse(responseCode = "200", description = "닉네임 중복 여부 반환"))
    @GetMapping("/check-nickname")
    public CheckResponse checkNickNameDuplication(@RequestParam String nickname) {
        return signUpService.execute(nickname);
    }

    @Operation(summary = "소셜 로그인 회원가입 API", description = "호출 성공 시 JWT Access, Refresh 토큰 반환")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "JWT Access, Refresh 토큰 반환"))
    @PostMapping("/signup")
    public SignUpResponse signUp(@Validated @RequestBody final SignUpRequest signUpRequest) {
        return signUpService.execute(signUpRequest);
    }

    @Operation(summary = "소셜 로그인 API", description = "호출 성공 시 JWT Access, Refresh 토큰 반환")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "JWT Access, Refresh 토큰 반환"))
    @PostMapping("/signin")
    public SignInResponse signIn(@Validated @RequestBody final SignInRequest signInRequest) {
        return signInService.execute(signInRequest);
    }

    @Operation(summary = "로그아웃 API", description = "호출 성공 시 기존 토큰을 레디스에서 제거")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "성공적으로 로그아웃"))
    @PostMapping("/signout")
    public void signOut(@Validated @RequestBody final SignOutRequest signOutRequest) {
        issuanceTokenService.negateToken(signOutRequest);
    }

    @Operation(summary = "토큰 재발급", description = "호출 성공 시 JWT Access 토큰 반환")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "access token 재발급"))
    @PostMapping("/access-token")
    public AccessTokenResponse regenerateAccessToken(@Validated @RequestBody final AccessTokenRequest request) {
        return issuanceTokenService.regenerateAccessToken(request);
    }

    @Operation(summary = "회원탈퇴 API - [JWT TOKEN REQUIRED]", description = "호출 성공 시 실제 회원 정보를 삭제하는 게 아닌 탈퇴 상태로만 변경")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "회원탈퇴 상태로 변경"))
    @Secured("USER")
    @DeleteMapping("/members/me/deletion")
    public void memberUnregister(@MemberId final Long memberId) {
        unregisterService.execute(memberId);
    }

}
