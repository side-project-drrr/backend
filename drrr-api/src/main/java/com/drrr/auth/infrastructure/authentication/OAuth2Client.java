package com.drrr.auth.infrastructure.authentication;


import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.drrr.auth.payload.dto.OAuth2GithubAccessTokenRequest;
import com.drrr.auth.payload.dto.OAuth2GithubBody;
import com.drrr.auth.payload.dto.OAuth2KakaoAccessTokenRequest;
import com.drrr.core.exception.member.OAuth2ExceptionCode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.oauth2.sdk.GrantType;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2Client {
    public static final String BEARER = "Bearer ";
    private final RestClient restClient = RestClient.create();
    private final ObjectMapper objectMapper;

    public JsonNode getUserProfile(final String accessToken, final String uri) {
        System.out.println("access token: "+accessToken);
        return restClient.get()
                .uri(uri)
                .header(HttpHeaders.AUTHORIZATION, BEARER + accessToken)
                .accept(APPLICATION_JSON)
                .exchange((request, response) -> {
                    if (response.getStatusCode().is3xxRedirection() || response.getStatusCode().is4xxClientError()) {
                        throw new IllegalArgumentException(
                                "Error Occurred, request uri -> " + uri + ", response -> "
                                        + objectMapper.readTree(response.getBody()).toPrettyString());
                    }
                    final JsonNode jsonNode = objectMapper.readTree(response.getBody());
                    //asText()에서 code가 잘못 됐을 경우 null 반환
                    try {
                        return jsonNode;
                    } catch (NullPointerException npe) {
                        log.error("provider ID를 받아오지 못했습니다. Access Token를 확인해주세요.");
                        throw OAuth2ExceptionCode.INVALID_ACCESS_TOKEN.newInstance();
                    }

                });
    }

    /**
     * Code로 AccessToken 받기
     */
    public String exchangeKakaoOAuth2AccessToken(final OAuth2KakaoAccessTokenRequest requestParams) {
        final UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(requestParams.uri())
                .queryParam("grant_type", GrantType.AUTHORIZATION_CODE.getValue())
                .queryParam("client_id", requestParams.clientId())
                .queryParam("code", requestParams.code());

        final String url = uriBuilder.build().encode().toUriString();

        return restClient.post()
                .uri(url)
                .accept(APPLICATION_JSON)
                .header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
                .exchange((request, response) -> {
                    if (response.getStatusCode().is3xxRedirection() || response.getStatusCode().is4xxClientError()) {
                        log.error("Error Occurred, response ->{}", objectMapper.readTree(response.getBody()).toPrettyString());
                        throw OAuth2ExceptionCode.INVALID_AUTHORIZE_CODE.newInstance();
                    }

                    final JsonNode jsonNode = objectMapper.readTree(response.getBody());
                    return Optional.ofNullable(jsonNode.get("access_token"))
                            .map(JsonNode::asText)
                            .orElseThrow(() -> {
                                log.error(
                                        "OAuth2Client Class exchangeKakaoOAuth2AccessToken(final OAuth2KakaoAccessTokenRequest requestParams) Method NullPointerException Error");
                                return OAuth2ExceptionCode.PROVIDER_ID_NULL.newInstance();
                            });
                });
    }

    public String exchangeGitHubOAuth2AccessToken(final OAuth2GithubAccessTokenRequest requestBody) {

        return restClient.post()
                .uri(requestBody.uri())
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .body(OAuth2GithubBody.from(requestBody))
                .exchange((request, response) -> {
                    if (response.getStatusCode().is4xxClientError()) {
                        log.error("Error Occurred, response ->{}", objectMapper.readTree(response.getBody()).toPrettyString());
                        throw OAuth2ExceptionCode.INVALID_AUTHORIZE_CODE.newInstance();
                    }
                    final JsonNode jsonNode = objectMapper.readTree(response.getBody());

                    return Optional.ofNullable(jsonNode.get("access_token"))
                            .map(JsonNode::asText)
                            .orElseThrow(() -> {
                                log.error(
                                        "OAuth2Client Class exchangeGitHubOAuth2AccessToken(OAuth2GithubAccessTokenRequest requestBody) Method NullPointerException Error");
                                return OAuth2ExceptionCode.PROVIDER_ID_NULL.newInstance();
                            });
                });
    }


}
