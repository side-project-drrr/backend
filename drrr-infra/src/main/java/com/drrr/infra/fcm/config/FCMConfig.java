package com.drrr.infra.fcm.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class FCMConfig {
    @Bean
    FirebaseMessaging firebaseMessaging() throws IOException {
        ClassPathResource resource = new ClassPathResource("security-storage-infra/firebase/drrr.json");

        InputStream refreshToken = resource.getInputStream();

        FirebaseApp firebaseApp = null;
        List<FirebaseApp> firebaseAppList = FirebaseApp.getApps();

        if (firebaseAppList.isEmpty()) {
            GoogleCredentials googleCredentials = GoogleCredentials.fromStream(refreshToken);
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(googleCredentials)
                    .build();

            return FirebaseMessaging.getInstance(FirebaseApp.initializeApp(options));
        }

        firebaseApp = firebaseAppList.stream()
                .filter((app) -> Objects.equals(app.getName(), FirebaseApp.DEFAULT_APP_NAME))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);

        return FirebaseMessaging.getInstance(firebaseApp);
    }
}