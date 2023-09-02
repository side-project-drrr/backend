package com.drrr.auth.controller;

import com.drrr.auth.dto.AccessTokenRequest;
import com.drrr.auth.dto.AccessTokenResponse;
import com.drrr.auth.payload.request.SignInRequest;
import com.drrr.auth.payload.request.SignUpRequest;
import com.drrr.auth.payload.response.SignInResponse;
import com.drrr.auth.payload.response.SignUpResponse;
import com.drrr.auth.service.impl.IssuanceTokenService;
import com.drrr.auth.service.impl.SignInService;
import com.drrr.auth.service.impl.SignUpService;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
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

    @PermitAll
    @PostMapping("/signup")
    public SignUpResponse signup(@Validated @RequestBody SignUpRequest signUpRequest) {
        return signUpService.execute(signUpRequest);
    }

    @PermitAll
    @PostMapping("/signin")
    public SignInResponse signin(@Validated @RequestBody SignInRequest signInRequest) {
        return signInService.execute(signInRequest);
    }

    @Secured("USER")
    @PostMapping("/access-token")
    public ResponseEntity<AccessTokenResponse> regenerateAccessToken(@RequestBody final AccessTokenRequest request) {
        AccessTokenResponse accessTokenResponse = issuanceTokenService.regenerateAccessToken(request);
        return ResponseEntity.ok(accessTokenResponse);
    }
}
