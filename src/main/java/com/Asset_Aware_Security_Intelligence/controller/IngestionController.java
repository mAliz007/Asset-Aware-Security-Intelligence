package com.Asset_Aware_Security_Intelligence.controller;

import com.Asset_Aware_Security_Intelligence.service.IngestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ingest")
@RequiredArgsConstructor
public class IngestionController {

    private final IngestionService ingestionService;

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
}