package com.drrr.auth.controller;

import com.drrr.auth.payload.request.AccessTokenRequest;
import com.drrr.auth.payload.request.SignInRequest;
import com.drrr.auth.payload.request.SignUpRequest;
import com.drrr.auth.payload.response.AccessTokenResponse;
import com.drrr.auth.payload.response.SignInResponse;
import com.drrr.auth.payload.response.SignUpResponse;
import com.drrr.auth.service.impl.IssuanceTokenService;
import com.drrr.auth.service.impl.SignInService;
import com.drrr.auth.service.impl.SignUpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final SignUpService signUpService;
    private final SignInService signInService;
    private final IssuanceTokenService issuanceTokenService;


    @Operation(summary = "소셜 로그인 회원가입 호출 API", description = "호출 성공 시 JWT Access, Refresh 토큰 반환")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공", content = @Content(schema = @Schema(implementation = SignUpResponse.class)))
    })
    @PostMapping("/signup")
    public SignUpResponse signUp(@Validated @RequestBody SignUpRequest signUpRequest) {
        return signUpService.execute(signUpRequest);
    }

    @Operation(summary = "소셜 로그인 API", description = "호출 성공 시 JWT Access, Refresh 토큰 반환")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(schema = @Schema(implementation = SignInResponse.class)))
    })
    @PostMapping("/signin")
    public SignInResponse signIn(@Validated @RequestBody SignInRequest signInRequest) {
        return signInService.execute(signInRequest);
    }

    @Operation(summary = "토큰 재발급", description = "호출 성공 시 JWT Access 토큰 반환")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "access token 재발급 성공", content = @Content(schema = @Schema(implementation = AccessTokenResponse.class)))
    })
    @PreAuthorize("hasAuthority('USER')")
    @PostMapping("/access-token")
    public ResponseEntity<AccessTokenResponse> regenerateAccessToken(@Validated @RequestBody final AccessTokenRequest request) {
        AccessTokenResponse accessTokenResponse = issuanceTokenService.regenerateAccessToken(request);
        return ResponseEntity.ok(accessTokenResponse);
    }
}
