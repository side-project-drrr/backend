package com.drrr.auth.payload.request;


import com.drrr.auth.infrastructure.authentication.OAuth2Client;
import com.drrr.auth.infrastructure.oauth2.OAuth2Provider;
import com.drrr.auth.payload.dto.OAuth2KakaoAccessTokenRequest;
import com.drrr.web.reader.ResourceReader;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KakaoOAuth2Request {
    private final ResourceReader reader;
    private final OAuth2Client oAuth2Client;

    public OAuth2KakaoResponse findProviderId(final String code) {
        OAuth2KakaoAccessTokenRequest kakaoRequest =
                OAuth2KakaoAccessTokenRequest.buildOAuth2KakaoAccessTokenRequest(reader.getKakaoClientId(),
                        code,
                        OAuth2Provider.KAKAO_REQUEST_ACCESS_TOKEN_URI.getRequestUrl()
                );
        final String accessToken = oAuth2Client.exchangeKakaoOAuth2AccessToken(kakaoRequest);
        JsonNode jsonNode = oAuth2Client.getUserProfile(accessToken, OAuth2Provider.KAKAO_PROFILE_URI.getRequestUrl());
        return OAuth2KakaoResponse.from(jsonNode);
    }

    @Builder
    public record OAuth2KakaoResponse(
            String profileImageUrl,
            String providerId
    ) implements OAuth2Request {
        @Override
        public String getProviderId() {
            return this.providerId;
        }

        @Override
        public String getProfileImageUrl() {
            return this.profileImageUrl;
        }

        public static OAuth2KakaoResponse from(JsonNode jsonNode) {
            return OAuth2KakaoResponse.builder()
                    .providerId(jsonNode.get("id").asText())
                    .profileImageUrl(jsonNode.get("profile_image").asText())
                    .build();
        }
    }

}
