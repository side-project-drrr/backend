package com.drrr.auth.service.impl;

import com.drrr.auth.infrastructure.subject.GithubOAuth2Request;
import com.drrr.auth.infrastructure.subject.KakaoOAuth2Request;
import com.drrr.auth.payload.dto.OAuth2Response;
import com.drrr.core.exception.login.OAuth2Exception;
import com.drrr.core.exception.login.OAuth2ExceptionCode;
import com.drrr.domain.member.service.ExistenceMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ExchangeOAuth2AccessTokenService {
    private final ExistenceMemberService existenceMemberService;
    private final KakaoOAuth2Request kakaoOAuth2Request;
    private final GithubOAuth2Request githubOAuth2Request;

    public OAuth2Response execute(final String code, final String provider){

        final String providerId = findOAuth2ProviderIdBySubject(code, provider);
        final boolean isRegistered = existenceMemberService.checkMemberExists(providerId);
        return OAuth2Response.builder()
                .providerId(providerId)
                .isRegistered(isRegistered)
                .build();
    }

    private String findOAuth2ProviderIdBySubject(final String code, final String provider){
        return switch (provider) {
            case "github" -> githubOAuth2Request.findProviderId(code);
            case "kakao" -> kakaoOAuth2Request.findProviderId(code);
            default -> {
                log.error("OAuth2Client Class findOAuth2ProviderIdBySubject(String code, String provider) Method InvalidUriException Error");
                throw new OAuth2Exception(OAuth2ExceptionCode.INVALID_OAUTH2_SUBJECT.getCode(), OAuth2ExceptionCode.INVALID_OAUTH2_SUBJECT.getMessage());
            }
        };
    }
}
