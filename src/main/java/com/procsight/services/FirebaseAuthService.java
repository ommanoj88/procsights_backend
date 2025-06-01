package com.procsight.services;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FirebaseAuthService {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseAuthService.class);

    public String verifyToken(String token) throws FirebaseAuthException {
        try {
            logger.info("🔍 Starting Firebase token verification...");
            logger.debug("Token length: {} characters", token != null ? token.length() : 0);

            if (token == null || token.trim().isEmpty()) {
                logger.error("❌ Token is null or empty");
                throw new IllegalArgumentException("Token is null or empty");
            }

            // Log first 50 characters of token for debugging (safely)
            String tokenPreview = token.length() > 50 ? token.substring(0, 50) + "..." : token;
            logger.debug("Token preview: {}", tokenPreview);

            // Verify the Firebase ID token
            logger.info("🔑 Calling Firebase Admin SDK to verify token...");
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);

            if (decodedToken == null) {
                logger.error("❌ Firebase token verification returned null");
                throw new IllegalArgumentException("Token verification returned null - invalid token");
            }

            String uid = decodedToken.getUid();
            String email = decodedToken.getEmail();

            logger.info("✅ Token verified successfully!");
            logger.info("UID: {}", uid);
            logger.info("Email: {}", email);
            logger.debug("Token issuer: {}", decodedToken.getIssuer());
            logger.debug("Token name: {}", decodedToken.getName());

            return uid;

        } catch (FirebaseAuthException e) {
            logger.error("❌ Firebase token verification failed!");
            logger.error("Error Code: {}", e.getAuthErrorCode());
            logger.error("Error Message: {}", e.getMessage());
            logger.error("Error Details: ", e);
            throw e; // Re-throw the original FirebaseAuthException
        } catch (IllegalArgumentException e) {
            logger.error("❌ Invalid token format: {}", e.getMessage());
            throw new RuntimeException("Invalid token: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("❌ Unexpected error during token verification", e);
            throw new RuntimeException("Token verification failed: " + e.getMessage(), e);
        }
    }
}