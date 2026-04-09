package com.Asset_Aware_Security_Intelligence.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiscoveryService {

    private final IngestionService ingestionService;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    // Use the API URL to list files, not the RAW URL
    private static final String GITHUB_API_BASE = "https://api.github.com/repos/cisagov/CSAF/contents/csaf_files/OT/white/";

    public void discoverAndSync() {
        // We will crawl the most recent years
        List<String> years = List.of("2024", "2025", "2026");

        for (String year : years) {
            try {
                String apiUrl = GITHUB_API_BASE + year + "?ref=develop";

                // 1. Get the directory listing from GitHub API
                String response = restTemplate.getForObject(apiUrl, String.class);
                JsonNode files = mapper.readTree(response);

                log.info("Found {} files in CISA folder for year {}", files.size(), year);

                // 2. Loop through each file in the JSON array
                for (JsonNode file : files) {
                    String downloadUrl = file.path("download_url").asText();

                    if (downloadUrl.endsWith(".json")) {
                        try {
                            ingestionService.ingestFromUrl(downloadUrl);
                        } catch (Exception e) {
                            log.error("Failed to ingest: {}", downloadUrl);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("Could not crawl year {}: {}", year, e.getMessage());
            }
        }
    }
}