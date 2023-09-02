package com.drrr.auth.service.impl;


import com.drrr.auth.infrastructure.OAuth2.InvalidUriException;
import com.drrr.auth.infrastructure.OAuth2.OAuth2Provider;
import com.drrr.auth.infrastructure.authentication.OAuth2Client;
import com.drrr.auth.payload.dto.OAuth2Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExternalAuthenticationFacade {

    private final OAuth2Client oAuth2Client;

    public String execute(String accessToken, String provider) {
        OAuth2Response response = oAuth2Client.getUserProfile("Bearer " + accessToken, getProviderRequestUri(provider));
        log.info("id: {}", response.getId());
        return response.getId();
    }

    public String getProviderRequestUri(String provider) {
        return switch (provider) {
            case "github" -> OAuth2Provider.GITHUB.getRequestUri();
            case "kakao" -> OAuth2Provider.KAKAO.getRequestUri();
            default -> throw new InvalidUriException("Request Uri를 선택할 수 없습니다.");
        };
    }
}
