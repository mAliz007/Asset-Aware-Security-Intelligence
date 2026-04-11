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
    private final NormalizationService normalizationService;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    @Transactional
    public void ingestFromUrl(String url) throws Exception {

        // 1. Fetch JSON
        String jsonContent = restTemplate.getForObject(url, String.class);
        JsonNode root = mapper.readTree(jsonContent);

        String csafId = root.path("document").path("tracking").path("id").asText();
        LocalDateTime incomingUpdateTime = LocalDateTime.now();
        // (later you can replace with real CSAF date field)

        // 2. Check existing advisory
        Optional<Advisory> existingOpt = advisoryRepository.findByCsafId(csafId);

        Advisory advisory;

        if (existingOpt.isPresent()) {

            advisory = existingOpt.get();

            // 🔥 VERSION CHECK (currently fallback since CSAF date parsing not implemented)
            LocalDateTime existingTime = advisory.getCsafLastUpdated();

            if (existingTime != null && existingTime.isAfter(incomingUpdateTime)) {
                return; // older or same → skip
            }

            // 🔥 UPDATE MODE: clear old relations
            advisory.getVulnerabilities().clear();

        } else {
            advisory = new Advisory();
            advisory.setCsafId(csafId);
        }

        // 3. Set metadata
        advisory.setTitle(root.path("document").path("title").asText());
        advisory.setPublisher(root.path("document").path("publisher").path("name").asText());

        advisory.setCsafLastUpdated(incomingUpdateTime);
        advisory.setLastUpdated(LocalDateTime.now());

        // 4. Build product map
        Map<String, JsonNode> productMap = new HashMap<>();
        root.path("product_tree").path("full_product_names").forEach(p -> {
            productMap.put(p.path("product_id").asText(), p);
        });

        // 5. Vulnerabilities
        List<Vulnerability> vulnerabilities = new ArrayList<>();

        root.path("vulnerabilities").forEach(vNode -> {

            Vulnerability v = Vulnerability.builder()
                    .cveId(vNode.path("cve").asText())
                    .description(
                            vNode.path("notes").isArray() && vNode.path("notes").size() > 0
                                    ? vNode.path("notes").get(0).path("text").asText()
                                    : ""
                    )
                    .cvssScore(
                            vNode.path("scores").isArray() && vNode.path("scores").size() > 0
                                    ? vNode.path("scores").get(0).path("cvss_v3").path("baseScore").asDouble()
                                    : 0.0
                    )
                    .severity(
                            vNode.path("scores").isArray() && vNode.path("scores").size() > 0
                                    ? vNode.path("scores").get(0).path("cvss_v3").path("baseSeverity").asText()
                                    : "UNKNOWN"
                    )
                    .advisory(advisory)
                    .build();

            // Products
            List<AdvisoryProduct> products = new ArrayList<>();

            vNode.path("product_status").path("known_affected").forEach(pIdNode -> {

                JsonNode pInfo = productMap.get(pIdNode.asText());

                if (pInfo != null) {

                    String rawName = pInfo.path("name").asText();
                    String vendor = advisory.getPublisher();

                    products.add(AdvisoryProduct.builder()
                            .vendor(vendor)
                            .productName(rawName)
                            .canonicalName(normalizationService.canonicalizeProductName(vendor, rawName))
                            .versionStartPadded(normalizationService.padVersion("0.0.0"))
                            .versionEndPadded(normalizationService.padVersion("99.9.9"))
                            .vulnerability(v)
                            .build());
                }
            });

            v.setAffectedProducts(products);
            vulnerabilities.add(v);
        });

        advisory.setVulnerabilities(vulnerabilities);

        // 6. Save (insert or update)
        advisoryRepository.save(advisory);
    }
}