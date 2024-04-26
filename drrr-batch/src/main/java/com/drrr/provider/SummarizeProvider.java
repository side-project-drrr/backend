package com.drrr.provider;

import com.drrr.payload.request.SummarizeRequest;
import com.drrr.payload.response.SummarizeResponse;
import com.drrr.property.ExtractCategoryProperty;
import com.google.common.net.HttpHeaders;
import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class SummarizeProvider {

    private final RestClient restclient = RestClient.create(new RestTemplateBuilder()
            .setConnectTimeout(Duration.ofMinutes(20))
            .setReadTimeout(Duration.ofMinutes(20))
            .build());
    private final ExtractCategoryProperty extractCategoryProperty;


    public SummarizeResponse request(List<String> content) {

        log.info("{}", extractCategoryProperty.createUri("/api/v1/post/summarize"));
        return restclient.method(HttpMethod.POST)
                .uri(extractCategoryProperty.createUri("/api/v1/post/summarize"))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(new SummarizeRequest(content))
                .retrieve()
                .toEntity(SummarizeResponse.class)
                .getBody();
    }

}
