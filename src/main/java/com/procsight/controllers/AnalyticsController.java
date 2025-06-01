package com.procsight.controllers;

import com.procsight.dto.AnalyticsDto;
import com.procsight.services.AnalyticsService;
import com.procsight.services.FirebaseAuthService;
import com.procsight.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "*")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @Autowired
    private FirebaseAuthService firebaseAuthService;

    @Autowired
    private UserService userService;

    @GetMapping("/spend")
    public ResponseEntity<Map<String, Object>> getSpendAnalytics(
            @RequestParam(defaultValue = "month") String period,
            @RequestParam(required = false) String groupBy,
            @RequestParam(defaultValue = "USD") String currency,
            @RequestHeader("Authorization") String authHeader) {
        try {
            // Verify authentication
            String token = authHeader.substring(7);
            String uid = firebaseAuthService.verifyToken(token);

            var user = userService.getUserByUid(uid)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String companyId = user.getCompany().getId();

            AnalyticsDto analytics = analyticsService.getSpendAnalytics(companyId, period);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "analytics", analytics
            ));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "Failed to fetch analytics: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardData(
            @RequestHeader("Authorization") String authHeader) {
        try {
            // Verify authentication
            String token = authHeader.substring(7);
            String uid = firebaseAuthService.verifyToken(token);

            var user = userService.getUserByUid(uid)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String companyId = user.getCompany().getId();

            // Get comprehensive dashboard data
            AnalyticsDto monthlyAnalytics = analyticsService.getSpendAnalytics(companyId, "month");
            AnalyticsDto yearlyAnalytics = analyticsService.getSpendAnalytics(companyId, "year");

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "dashboard", Map.of(
                            "monthlySpend", monthlyAnalytics.getTotalSpend(),
                            "yearlySpend", yearlyAnalytics.getTotalSpend(),
                            "monthlyInvoiceCount", monthlyAnalytics.getInvoiceCount(),
                            "averageInvoiceAmount", monthlyAnalytics.getAverageInvoiceAmount(),
                            "categoryBreakdown", monthlyAnalytics.getBreakdown(),
                            "trends", monthlyAnalytics.getTrends(),
                            "insights", monthlyAnalytics.getInsights(),
                            "alerts", monthlyAnalytics.getAlerts(),
                            "topVendors", monthlyAnalytics.getTopVendors()
                    )
            ));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "Failed to fetch dashboard data: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/reports/generate")
    public ResponseEntity<Map<String, Object>> generateReport(
            @RequestBody Map<String, Object> request,
            @RequestHeader("Authorization") String authHeader) {
        try {
            // Verify authentication
            String token = authHeader.substring(7);
            String uid = firebaseAuthService.verifyToken(token);

            var user = userService.getUserByUid(uid)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String companyId = user.getCompany().getId();
            String reportType = (String) request.get("reportType");
            String format = (String) request.get("format");

            // Mock report generation
            String reportId = "report_" + System.currentTimeMillis();
            String downloadUrl = "/api/reports/download/" + reportId;

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "reportId", reportId,
                    "downloadUrl", downloadUrl,
                    "expiresAt", java.time.LocalDateTime.now().plusHours(24).toString(),
                    "status", "generating"
            ));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "Failed to generate report: " + e.getMessage()
            ));
        }
    }
}