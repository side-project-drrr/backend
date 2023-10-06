package com.drrr.auth.infrastructure.authentication;



import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.drrr.auth.payload.dto.OAuth2Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2Client {
    private final RestClient restClient = RestClient.create();
    private final ObjectMapper objectMapper;

    public OAuth2Response getUserProfile(final String accessToken, String uri) {
        OAuth2Response result = restClient.get()
                .uri(uri)
                .header("Authorization", accessToken)
                .accept(APPLICATION_JSON)
                .exchange((request, response) -> {
                    if (response.getStatusCode().is4xxClientError()) {
                        throw new IllegalArgumentException("유효하지 않는 인증" + response.getBody());
                    }
                    else {
                        return convertResponse(response);
                    }
                });

        return result;
    }

    private OAuth2Response convertResponse(ClientHttpResponse response){
        OAuth2Response result = null;
        try {
            result = objectMapper.readValue(response.getBody(), OAuth2Response.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result;

    }

}
