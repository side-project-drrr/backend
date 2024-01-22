package com.drrr.auth.service.impl;

import com.drrr.auth.payload.dto.OAuth2Response;
import com.drrr.auth.payload.request.GithubOAuth2Request;
import com.drrr.auth.payload.request.KakaoOAuth2Request;
import com.drrr.auth.payload.request.OAuth2Request;
import com.drrr.domain.member.exception.OAuth2ExceptionCode;
import com.drrr.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ExchangeOAuth2AccessTokenService {
    private final KakaoOAuth2Request kakaoOAuth2Request;
    private final GithubOAuth2Request githubOAuth2Request;
    private final MemberRepository memberRepository;

    @Transactional
    public OAuth2Response execute(final String code, final String provider) {

        final OAuth2Request oRequest = findOAuth2ProviderIdBySubject(code, provider);
        //회원가입이 필요한 경우인지 기존 회원인지 구분하기 위함
        final boolean isRegistered = memberRepository.existsByProviderId(oRequest.getProviderId());
        return OAuth2Response.builder()
                .profileImageUrl(oRequest.getProfileImageUrl())
                .providerId(oRequest.getProviderId())
                .isRegistered(isRegistered)
                .build();
    }

    private OAuth2Request findOAuth2ProviderIdBySubject(final String code, final String provider) {
        return switch (provider) {
            case "github" -> githubOAuth2Request.findProviderId(code);
            case "kakao" -> kakaoOAuth2Request.findProviderId(code);
            default -> {
                log.error(
                        "OAuth2Client Class findOAuth2ProviderIdBySubject(String code, String provider) Method InvalidUriException Error");
                throw OAuth2ExceptionCode.INVALID_OAUTH2_SUBJECT.newInstance();
            }
        };
    }
}
