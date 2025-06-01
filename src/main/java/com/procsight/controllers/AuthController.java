package com.procsight.controllers;

import com.procsight.models.User;
import com.procsight.models.Company;
import com.procsight.services.UserService;
import com.procsight.services.CompanyService;
import com.procsight.services.FirebaseAuthService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserService userService;



    @Autowired
    private CompanyService companyService;

    @Autowired
    private FirebaseAuthService firebaseAuthService;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);


    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> request) {
        try {
            logger.info("🚀 Login attempt started");

            String token = request.get("token");
            logger.info("Token received - Length: {}", token != null ? token.length() : 0);

            if (token == null || token.isEmpty()) {
                logger.warn("❌ Login attempt with missing token");
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Token is missing in the request"
                ));
            }

            // Additional token validation
            if (token.trim().length() < 100) {
                logger.warn("❌ Token seems too short: {} characters", token.length());
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Invalid token format - token too short"
                ));
            }

            logger.info("🔍 Starting Firebase token verification...");

            // Verify Firebase Token
            String uid;
            try {
                uid = firebaseAuthService.verifyToken(token);
                logger.info("✅ Token verification successful for UID: {}", uid);
            } catch (FirebaseAuthException e) {
                logger.error("❌ Firebase token verification failed: {} - {}",
                        e.getAuthErrorCode() != null ? e.getAuthErrorCode() : "UNKNOWN",
                        e.getMessage());
                return ResponseEntity.status(401).body(Map.of(
                        "success", false,
                        "message", "Invalid Firebase token: " + e.getMessage(),
                        "errorCode", e.getAuthErrorCode() != null ? e.getAuthErrorCode().toString() : "UNKNOWN"
                ));
            } catch (RuntimeException e) {
                logger.error("❌ Token validation failed: {}", e.getMessage());
                return ResponseEntity.status(401).body(Map.of(
                        "success", false,
                        "message", "Token validation failed: " + e.getMessage()
                ));
            }

            // Find user in database
            logger.info("🔍 Looking up user in database for UID: {}", uid);
            User user;
            try {
                user = userService.getUserByUid(uid)
                        .orElseThrow(() -> new RuntimeException("User not found in database for UID: " + uid));
                logger.info("✅ User found: {} {}", user.getFirstName(), user.getLastName());

                // ADD DEBUG LOGGING HERE
                logger.info("🔍 User details - ID: {}, Email: {}", user.getId(), user.getEmail());
                logger.info("🔍 Company: {}", user.getCompany());
                if (user.getCompany() != null) {
                    logger.info("🔍 Company ID: {}, Name: {}", user.getCompany().getId(), user.getCompany().getName());
                }
                logger.info("🔍 Role: {}", user.getRole());
                if (user.getRole() != null) {
                    logger.info("🔍 Role Name: {}", user.getRole().getName());
                }
                logger.info("🔍 Department: {}, LastLogin: {}", user.getDepartment(), user.getLastLogin());

            } catch (Exception e) {
                logger.error("❌ Database error when looking up user: {}", e.getMessage());
                return ResponseEntity.status(404).body(Map.of(
                        "success", false,
                        "message", "User not found in database. Please register first."
                ));
            }

            // Update last login
            try {
                userService.updateLastLogin(uid);
                logger.info("✅ Last login updated for user: {}", uid);
            } catch (Exception e) {
                logger.warn("⚠️ Failed to update last login: {}", e.getMessage());
                // Don't fail the login for this
            }

            logger.info("🎉 Login successful for user: {} {}", user.getFirstName(), user.getLastName());

            // REPLACE THE RESPONSE BUILDING WITH SAFER VERSION
            Map<String, Object> userResponse = new HashMap<>();
            userResponse.put("id", user.getId());
            userResponse.put("email", user.getEmail());
            userResponse.put("firstName", user.getFirstName());
            userResponse.put("lastName", user.getLastName());

            // Safely handle company
            if (user.getCompany() != null) {
                userResponse.put("companyId", user.getCompany().getId());
                userResponse.put("companyName", user.getCompany().getName());
            } else {
                userResponse.put("companyId", null);
                userResponse.put("companyName", null);
            }

            // Safely handle role
            if (user.getRole() != null) {
                userResponse.put("role", user.getRole().getName());
            } else {
                userResponse.put("role", null);
            }

            userResponse.put("department", user.getDepartment());
            userResponse.put("lastLogin", user.getLastLogin());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Login successful",
                    "uid", uid,
                    "token", token,
                    "user", userResponse
            ));

        } catch (Exception e) {
            logger.error("❌ Unexpected error during login", e);
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "Login failed: " + e.getMessage(),
                    "errorType", e.getClass().getSimpleName()
            ));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body(Map.of(
                        "success", false,
                        "message", "Missing or invalid authorization header"
                ));
            }

            String token = authHeader.substring(7); // Remove "Bearer " prefix
            String uid = firebaseAuthService.verifyToken(token);

            User user = userService.getUserByUid(uid)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            return ResponseEntity.ok(new HashMap<String, Object>() {{
                put("id", user.getId());
                put("uid", user.getUid());
                put("email", user.getEmail());
                put("firstName", user.getFirstName());
                put("lastName", user.getLastName());
                put("companyId", user.getCompany() != null ? user.getCompany().getId() : null);
                put("companyName", user.getCompany() != null ? user.getCompany().getName() : null);
                put("role", user.getRole() != null ? user.getRole().getName() : null);
                put("department", user.getDepartment());
                put("costCenter", user.getCostCenter());
                put("approvalLimit", user.getApprovalLimit());
                put("isActive", user.getIsActive());
                put("lastLogin", user.getLastLogin());
                put("createdAt", user.getCreatedAt());
            }});
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(401).body(Map.of(
                    "success", false,
                    "message", "Invalid token: " + e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "Failed to get user: " + e.getMessage()
            ));
        }
    }

    // For testing - get user without authentication
    @GetMapping("/test/users")
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        try {
            var users = userService.getAllUsers();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "count", users.size(),
                    "users", users.stream().map(user -> Map.of(
                            "id", user.getId(),
                            "email", user.getEmail(),
                            "firstName", user.getFirstName(),
                            "lastName", user.getLastName(),
                            "companyName", user.getCompany() != null ? user.getCompany().getName() : null,
                            "createdAt", user.getCreatedAt()
                    )).toList()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "Failed to get users: " + e.getMessage()
            ));
        }
    }
}