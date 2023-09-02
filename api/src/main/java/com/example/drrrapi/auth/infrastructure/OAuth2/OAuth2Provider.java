package com.example.drrrapi.auth.infrastructure.OAuth2;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum OAuth2Provider {
    KAKAO("https://kapi.kakao.com/v2/user/me"),
    GITHUB("https://api.github.com/user");
    private final String requestUri;

}
