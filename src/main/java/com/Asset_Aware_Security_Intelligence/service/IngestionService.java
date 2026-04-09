package com.Asset_Aware_Security_Intelligence.service;

import com.Asset_Aware_Security_Intelligence.model.*;
import com.Asset_Aware_Security_Intelligence.repository.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class IngestionService {

    private final AdvisoryRepository advisoryRepository;
    private final com.AssetAwareSecurityIntelligence.service.NormalizationService normalizationService;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    @Transactional
    public void ingestFromUrl(String url) throws Exception {
        // 1. Fetch JSON
        String jsonContent = restTemplate.getForObject(url, String.class);
        JsonNode root = mapper.readTree(jsonContent);

        // 2. Extract Metadata
        String csafId = root.path("document").path("tracking").path("id").asText();

        // Check if already exists to avoid duplicates
        if (advisoryRepository.findByCsafId(csafId).isPresent()) return;

        Advisory advisory = Advisory.builder()
                .csafId(csafId)
                .title(root.path("document").path("title").asText())
                .publisher(root.path("document").path("publisher").path("name").asText())
                .lastUpdated(LocalDateTime.now())
                .build();

        // 3. Map Product Tree (ID -> Name/CPE)
        Map<String, JsonNode> productMap = new HashMap<>();
        root.path("product_tree").path("full_product_names").forEach(p -> {
            productMap.put(p.path("product_id").asText(), p);
        });

        // 4. Process Vulnerabilities
        List<Vulnerability> vulnerabilities = new ArrayList<>();
        root.path("vulnerabilities").forEach(vNode -> {
            Vulnerability v = Vulnerability.builder()
                    .cveId(vNode.path("cve").asText())
                    .description(vNode.path("notes").get(0).path("text").asText())
                    .cvssScore(vNode.path("scores").get(0).path("cvss_v3").path("baseScore").asDouble())
                    .severity(vNode.path("scores").get(0).path("cvss_v3").path("baseSeverity").asText())
                    .advisory(advisory)
                    .build();

            // Link Affected Products
            List<AdvisoryProduct> products = new ArrayList<>();
            vNode.path("product_status").path("known_affected").forEach(pIdNode -> {
                JsonNode pInfo = productMap.get(pIdNode.asText());
                if (pInfo != null) {
                    String rawName = pInfo.path("name").asText();
                    String vendor = advisory.getPublisher(); // Simplified for now

                    products.add(AdvisoryProduct.builder()
                            .vendor(vendor)
                            .productName(rawName)
                            .canonicalName(normalizationService.canonicalizeProductName(vendor, rawName))
                            .versionStartPadded(normalizationService.padVersion("0.0.0")) // In real CSAF, we parse ranges
                            .versionEndPadded(normalizationService.padVersion("99.9.9"))
                            .vulnerability(v)
                            .build());
                }
            });
            v.setAffectedProducts(products);
            vulnerabilities.add(v);
        });

        advisory.setVulnerabilities(vulnerabilities);
        advisoryRepository.save(advisory);
    }
}