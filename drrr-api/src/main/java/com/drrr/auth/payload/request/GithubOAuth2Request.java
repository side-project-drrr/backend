package com.drrr.auth.payload.request;


import com.drrr.auth.infrastructure.authentication.OAuth2Client;
import com.drrr.auth.infrastructure.oauth2.OAuth2Provider;
import com.drrr.auth.payload.dto.OAuth2GithubAccessTokenRequest;
import com.drrr.web.reader.ResourceReader;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GithubOAuth2Request {
    private final ResourceReader reader;
    private final OAuth2Client oAuth2Client;

    public OAuth2GithubResponse findProviderId(final String code) {
        OAuth2GithubAccessTokenRequest githubRequest =
                OAuth2GithubAccessTokenRequest.buildOAuth2GithubAccessTokenRequest(reader.getGithubClientId(),
                        code,
                        reader.getGithubClientSecret(),
                        OAuth2Provider.GITHUB_REQEUST_ACCESS_TOKEN_URI.getRequestUrl()
                );
        final String accessToken = oAuth2Client.exchangeGitHubOAuth2AccessToken(githubRequest);
        final JsonNode jsonNode = oAuth2Client.getUserProfile(accessToken,
                OAuth2Provider.GITHUB_PROFILE_URI.getRequestUrl());
        return OAuth2GithubResponse.from(jsonNode);
    }

    @Builder
    public record OAuth2GithubResponse(
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

        public static OAuth2GithubResponse from(JsonNode jsonNode) {
            return OAuth2GithubResponse.builder()
                    .providerId(jsonNode.get("id").asText())
                    .profileImageUrl(jsonNode.get("avatar_url").asText())
                    .build();
        }

    }
}
