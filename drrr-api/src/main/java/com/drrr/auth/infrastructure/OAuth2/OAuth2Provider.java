package com.drrr.auth.infrastructure.oauth2;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public enum OAuth2Provider {

    KAKAO_REQUEST_ACCESS_TOKEN_URI("https://kauth.kakao.com/oauth/token"),
    KAKAO_PROFILE_URI("https://kapi.kakao.com/v2/user/me"),
    GITHUB_REQEUST_ACCESS_TOKEN_URI("https://github.com/login/oauth/access_token"),
    GITHUB_PROFILE_URI("https://api.github.com/user"),
    REDIRECT_URL("http://localhost:8081");
    private final String requestUrl;

}
