package com.example.tripplanpractice.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@Slf4j
@Configuration
public class FireBaseConfig {

    @Value("${spring.firebase.service-account.path}")
    private String SERVICE_ACCOUNT_PATH;

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        try {
            if (!FirebaseApp.getApps().isEmpty()) {
                log.info("Firebase app already initialized.");
                return FirebaseApp.getInstance();
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(new ClassPathResource(SERVICE_ACCOUNT_PATH).getInputStream()))
                    .build();

            log.info("Firebase app initialized successfully.");
            return FirebaseApp.initializeApp(options);

        } catch (IOException e) {
            log.error("Firebase initialization failed: {}", e.getMessage());
            return null;
        }
    }

    @Bean
    public FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
        if (firebaseApp == null) {
            log.error("FirebaseApp is null. FirebaseMessaging cannot be initialized.");
            return null;
        }

        return FirebaseMessaging.getInstance(firebaseApp);
    }
}
