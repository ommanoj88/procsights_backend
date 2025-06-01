package com.procsight.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.config.path:}")
    private String firebaseConfigPath;

    @Bean
    public FirebaseApp firebaseApp() {
        System.out.println("🔥 Initializing Firebase configuration...");
        System.out.println("Firebase Config Path from properties: " + firebaseConfigPath);

        try {
            InputStream serviceAccount = null;

            // Try multiple ways to find the service account key
            if (firebaseConfigPath != null && !firebaseConfigPath.isEmpty()) {
                if (firebaseConfigPath.startsWith("classpath:")) {
                    // Handle classpath reference
                    String resourcePath = firebaseConfigPath.substring("classpath:".length());
                    System.out.println("Loading from classpath: " + resourcePath);
                    ClassPathResource resource = new ClassPathResource(resourcePath);
                    serviceAccount = resource.getInputStream();
                } else {
                    // Handle file path
                    File file = new File(firebaseConfigPath);
                    System.out.println("Checking file existence: " + file.getAbsolutePath());
                    System.out.println("File exists: " + file.exists());

                    if (!file.exists()) {
                        throw new RuntimeException("Firebase config file not found at: " + file.getAbsolutePath());
                    }

                    serviceAccount = new FileInputStream(file);
                }
            } else {
                // Try default locations
                System.out.println("No firebase.config.path specified, trying default locations...");

                // Try classpath first
                try {
                    ClassPathResource resource = new ClassPathResource("serviceAccountKey.json");
                    serviceAccount = resource.getInputStream();
                    System.out.println("✓ Found serviceAccountKey.json in classpath");
                } catch (Exception e) {
                    // Try environment variable
                    String credentialsPath = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
                    if (credentialsPath != null) {
                        System.out.println("Trying GOOGLE_APPLICATION_CREDENTIALS: " + credentialsPath);
                        serviceAccount = new FileInputStream(credentialsPath);
                    } else {
                        throw new RuntimeException(
                                "Firebase service account key not found. Please:\n" +
                                        "1. Set firebase.config.path in application.properties\n" +
                                        "2. Place serviceAccountKey.json in src/main/resources/\n" +
                                        "3. Set GOOGLE_APPLICATION_CREDENTIALS environment variable"
                        );
                    }
                }
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp app = FirebaseApp.initializeApp(options);
            System.out.println("✅ Firebase initialized successfully!");
            System.out.println("Firebase App Name: " + app.getName());

            return app;

        } catch (IOException e) {
            System.err.println("❌ Failed to initialize Firebase:");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize Firebase: " + e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("❌ Unexpected error during Firebase initialization:");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize Firebase: " + e.getMessage(), e);
        }
    }
}