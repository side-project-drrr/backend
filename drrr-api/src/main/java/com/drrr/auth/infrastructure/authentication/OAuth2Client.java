package com.drrr.auth.infrastructure.authentication;


import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.drrr.auth.payload.dto.OAuth2GithubAccessTokenRequest;
import com.drrr.auth.payload.dto.OAuth2KakaoAccessTokenRequest;
import com.drrr.core.exception.login.OAuth2Exception;
import com.drrr.core.exception.login.OAuth2ExceptionCode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2Client {
    private final RestClient restClient = RestClient.create();
    private final ObjectMapper objectMapper;

    public String getUserProfile(final String accessToken, final String uri) {
        final String providerId = restClient.get()
                .uri(uri)
                .header("Authorization", accessToken)
                .accept(APPLICATION_JSON)
                .exchange((request, response) -> {
                    if (response.getStatusCode().is4xxClientError()) {
                        throw new IllegalArgumentException(
                                "Access Token이 유효하지  않습니다. request uri -> " + uri + ", response -> "
                                        + response.getBody());
                    } else {
                        final JsonNode jsonNode = objectMapper.readTree(response.getBody());
                        //asText()에서 code가 잘못 됐을 경우 null 반환
                        try {
                            final String id = jsonNode.get("id").asText();
                            return id;
                        } catch (NullPointerException npe) {
                            log.error("provider ID를 받아오지 못했습니다. Access Token를 확인해주세요.");
                            throw new OAuth2Exception(OAuth2ExceptionCode.INVALID_ACCESS_TOKEN.getCode(), OAuth2ExceptionCode.INVALID_ACCESS_TOKEN.getMessage());
                        }
                    }
                });
        return providerId;
    }

    /**
     * Code로 AccessToken 받기
     */
    public String exchangeKakaoOAuth2AccessToken(final OAuth2KakaoAccessTokenRequest requestParams) {
        final UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(requestParams.uri())
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", requestParams.clientId())
                .queryParam("code", requestParams.code());

        final String url = uriBuilder.build().encode().toUriString();

        final String result = restClient.post()
                .uri(url)
                .accept(APPLICATION_JSON)
                .header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
                .exchange((request, response) -> {
                    if (response.getStatusCode().is4xxClientError()) {
                        log.error(
                                "OAuth2Client Class exchangeKakaoOAuth2AccessToken(final OAuth2KakaoAccessTokenRequest requestParams) Method IllegalArgumentException Error");
                        throw new OAuth2Exception(OAuth2ExceptionCode.INVALID_AUTHORIZE_CODE.getCode(), OAuth2ExceptionCode.INVALID_AUTHORIZE_CODE.getMessage());
                    } else {

                        final JsonNode jsonNode = objectMapper.readTree(response.getBody());
                        //asText()에서 code가 잘못 됐을 경우 null 반환
                        try {
                            final String accessToken = jsonNode.get("access_token").asText();
                            return accessToken;
                        } catch (NullPointerException npe) {
                            log.error(
                                    "OAuth2Client Class exchangeKakaoOAuth2AccessToken(final OAuth2KakaoAccessTokenRequest requestParams) Method NullPointerException Error");
                            throw new OAuth2Exception(OAuth2ExceptionCode.PROVIDER_ID_NULL.getCode(), OAuth2ExceptionCode.PROVIDER_ID_NULL.getMessage());
                        }
                    }
                });

        return result;

    }

    public String exchangeGitHubOAuth2AccessToken(final OAuth2GithubAccessTokenRequest requestBody) {
        final Map<String, String> body = new HashMap<>();
        body.put("client_id", requestBody.getClientId());
        body.put("client_secret", requestBody.getClientSecret());
        body.put("code", requestBody.getCode());

        final String result = restClient.post()
                .uri(requestBody.getUri())
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .body(body)
                .exchange((request, response) -> {
                    if (response.getStatusCode().is4xxClientError()) {
                        log.error(
                                "OAuth2Client Class exchangeGitHubOAuth2AccessToken(OAuth2GithubAccessTokenRequest requestBody) Method IllegalArgumentException Error");
                        throw new OAuth2Exception(OAuth2ExceptionCode.INVALID_AUTHORIZE_CODE.getCode(), OAuth2ExceptionCode.INVALID_AUTHORIZE_CODE.getMessage());
                    } else {
                        final JsonNode jsonNode = objectMapper.readTree(response.getBody());

                        //asText()에서 code가 잘못 됐을 경우 null 반환
                        try {
                            final String accessToken = jsonNode.get("access_token").asText();
                            return accessToken;
                        } catch (NullPointerException npe) {
                            log.error(
                                    "OAuth2Client Class exchangeGitHubOAuth2AccessToken(OAuth2GithubAccessTokenRequest requestBody) Method NullPointerException Error");
                            throw new OAuth2Exception(OAuth2ExceptionCode.PROVIDER_ID_NULL.getCode(), OAuth2ExceptionCode.PROVIDER_ID_NULL.getMessage());
                        }
                    }
                });

        return result;
    }


}
