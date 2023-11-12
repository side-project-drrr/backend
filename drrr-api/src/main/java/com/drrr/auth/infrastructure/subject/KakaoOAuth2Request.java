package com.drrr.auth.infrastructure.subject;

import com.drrr.auth.infrastructure.OAuth2.OAuth2Provider;
import com.drrr.auth.infrastructure.authentication.OAuth2Client;
import com.drrr.auth.payload.dto.OAuth2KakaoAccessTokenRequest;
import com.drrr.auth.payload.dto.OAuth2Request;
import com.drrr.web.reader.ResourceReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KakaoOAuth2Request implements OAuth2Request {
    private final ResourceReader reader;
    private final OAuth2Client oAuth2Client;
    @Override
    public String findProviderId(final String code) {
        OAuth2KakaoAccessTokenRequest kakaoRequest =
                OAuth2KakaoAccessTokenRequest.buildOAuth2KakaoAccessTokenRequest(reader.getKakaoClientId(),
                        code,
                        OAuth2Provider.KAKAO_REQUEST_ACCESS_TOKEN_URI.getRequestUrl()
                );
        final String accessToken =  oAuth2Client.exchangeKakaoOAuth2AccessToken(kakaoRequest);
        return oAuth2Client.getUserProfile("Bearer " + accessToken, OAuth2Provider.KAKAO_PROFILE_URI.getRequestUrl());
    }

}
