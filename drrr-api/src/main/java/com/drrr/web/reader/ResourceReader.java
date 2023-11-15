package com.drrr.web.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Getter
@Component
public class ResourceReader {
    private final String kakaoClientId;
    private final String githubClientId;
    private final String githubClientSecret;
    private final ResourceLoader resourceLoader;

    public ResourceReader(@Value("${kakao.private.key}") Resource kakaoClientId,
                       @Value("${spring.security.oauth2.client.registration.github.client-id}") Resource githubClientId,
                       @Value("${spring.security.oauth2.client.registration.github.client-secret}") Resource githubClientSecret, ResourceLoader resourceLoader)
            throws IOException {
        this.resourceLoader = resourceLoader;
        this.kakaoClientId = ResourceReader.loadFileAsString(kakaoClientId);
        this.githubClientId = ResourceReader.loadFileAsString(githubClientId);
        this.githubClientSecret = ResourceReader.loadFileAsString(githubClientSecret);
    }

    private static String loadFileAsString(Resource filePath) throws IOException {
        Resource resource = filePath;
        InputStream inputStream = resource.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }

        return stringBuilder.toString();
    }
}
