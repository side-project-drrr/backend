package com.drrr.auth.payload.request;

import com.drrr.auth.infrastructure.oauth2.OAuth2Provider;
import com.drrr.auth.infrastructure.authentication.OAuth2Client;
import com.drrr.auth.payload.dto.OAuth2GithubAccessTokenRequest;
import com.drrr.web.reader.ResourceReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GithubOAuth2Request {
    private final ResourceReader reader;
    private final OAuth2Client oAuth2Client;

    public String findProviderId(final String code) {
        OAuth2GithubAccessTokenRequest githubRequest =
                OAuth2GithubAccessTokenRequest.buildOAuth2GithubAccessTokenRequest(reader.getGithubClientId(),
                        code,
                        reader.getGithubClientSecret(),
                        OAuth2Provider.GITHUB_REQEUST_ACCESS_TOKEN_URI.getRequestUrl()
                );
        final String accessToken =  oAuth2Client.exchangeGitHubOAuth2AccessToken(githubRequest);
        return oAuth2Client.getUserProfile("Bearer " + accessToken, OAuth2Provider.GITHUB_PROFILE_URI.getRequestUrl());
    }
}
