package com.Asset_Aware_Security_Intelligence.controller;

import com.Asset_Aware_Security_Intelligence.service.IngestionService;
import com.Asset_Aware_Security_Intelligence.service.DiscoveryService; // Correctly imported
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ingest")
@RequiredArgsConstructor
public class IngestionController {

    private final IngestionService ingestionService;

    // ADD THIS LINE BELOW:
    private final DiscoveryService discoveryService;



    /**
     * SINGLE SYNC TEST URLS:
     * Siemens: https://raw.githubusercontent.com/cisagov/CSAF/develop/csaf_files/OT/white/2022/icsa-22-221-01.json
     * Hitachi: https://raw.githubusercontent.com/cisagov/CSAF/develop/csaf_files/OT/white/2023/icsa-23-215-02.json
     * Schneider: https://raw.githubusercontent.com/cisagov/CSAF/develop/csaf_files/OT/white/2024/icsa-24-074-01.json
     */
    @PostMapping("/sync")
    public ResponseEntity<?> triggerSync(@RequestBody Map<String, String> request) {
        String url = request.get("url");
        try {
            ingestionService.ingestFromUrl(url);
            return ResponseEntity.ok(Map.of("message", "Sync successful for ID: " + url));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/sync-all")
    public ResponseEntity<?> syncAll() {
        // Now 'discoveryService' is recognized because it's a field in this class
        discoveryService.discoverAndSync();
        return ResponseEntity.ok("Global sync started in background...");
    }
}