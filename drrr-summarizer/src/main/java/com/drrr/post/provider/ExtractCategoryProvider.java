package com.drrr.post.provider;


import com.drrr.post.payload.response.OpenAiChatCompletionResponse;
import com.drrr.post.property.ExtractCategoryProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class ExtractCategoryProvider {

    private static final String BEARER = "Bearer ";
    private final RestClient restclient = RestClient.create();
    private final ExtractCategoryProperty extractCategoryProperty;


    public OpenAiChatCompletionResponse request(String content) {
        return restclient.post()
                .uri(extractCategoryProperty.apiUrl())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, BEARER + extractCategoryProperty.gptKey())
                .body(extractCategoryProperty.createRequest(content))
                .retrieve()
                .toEntity(OpenAiChatCompletionResponse.class)
                .getBody();
    }
}
