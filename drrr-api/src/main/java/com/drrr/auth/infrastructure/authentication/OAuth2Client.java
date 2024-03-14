package com.drrr.auth.infrastructure.authentication;


import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.drrr.auth.payload.dto.OAuth2GithubAccessTokenRequest;
import com.drrr.auth.payload.dto.OAuth2GithubBody;
import com.drrr.auth.payload.dto.OAuth2KakaoAccessTokenRequest;
import com.drrr.domain.exception.DomainExceptionCode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2Client {

    public static final String BEARER = "Bearer ";
    private static final String ACCESS_TOKEN = "access_token";
    private final RestClient restClient = RestClient.create();
    private final ObjectMapper objectMapper;

    public JsonNode getUserProfile(final String accessToken, final String uri) {
        return restClient.get()
                .uri(uri)
                .header(HttpHeaders.AUTHORIZATION, BEARER + accessToken)
                .accept(APPLICATION_JSON)
                .exchange((request, response) -> {
                    if (response.getStatusCode().is3xxRedirection() || response.getStatusCode().is4xxClientError()) {
                        throw DomainExceptionCode.INVALID_ACCESS_TOKEN.newInstance(
                                "Error Occurred, request uri -> " + uri + ", response -> "
                                        + objectMapper.readTree(response.getBody()).toPrettyString());
                    }
                    //asText()에서 code가 잘못 됐을 경우 null 반환
                    return objectMapper.readTree(response.getBody());
                });
    }

    /**
     * Code로 AccessToken 받기
     */
    public String exchangeKakaoOAuth2AccessToken(final OAuth2KakaoAccessTokenRequest kakaoAccessTokenApiRequest) {
        return restClient.post()
                .uri(kakaoAccessTokenApiRequest.toUrl())
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_FORM_URLENCODED)
                .exchange(this::exchangeOAuthProvideAccessToken);
    }

    private String exchangeOAuthProvideAccessToken(
            HttpRequest request,
            ClientHttpResponse response
    ) throws IOException {

        if (response.getStatusCode().is3xxRedirection() || response.getStatusCode().is4xxClientError()) {
            log.error("Error Occurred, response ->{}", objectMapper.readTree(response.getBody()).toPrettyString());
            throw DomainExceptionCode.INVALID_AUTHORIZE_CODE.newInstance();
        }

        return Optional.ofNullable(objectMapper.readTree(response.getBody()))
                .map(jsonNode -> jsonNode.get(ACCESS_TOKEN))
                .map(JsonNode::asText)
                .orElseThrow(DomainExceptionCode.PROVIDER_ID_NULL::newInstance);
    }

    public String exchangeGitHubOAuth2AccessToken(final OAuth2GithubAccessTokenRequest requestBody) {
        return restClient.post()
                .uri(requestBody.uri())
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .body(OAuth2GithubBody.from(requestBody))
                .exchange(this::exchangeOAuthProvideAccessToken);
    }
}
