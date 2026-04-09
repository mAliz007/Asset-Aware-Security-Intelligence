package com.AssetAwareSecurityIntelligence.service;

import org.springframework.stereotype.Service;
import java.util.stream.Collectors;
import java.util.Arrays;

@Service
public class NormalizationService {

    /**
     * Cleans product names for high-speed string matching.
     * Example: "Siemens S7-1500 CPU" -> "siemenss71500"
     */
    public String canonicalizeProductName(String vendor, String product) {
        if (vendor == null) vendor = "";
        if (product == null) product = "";

        String combined = (vendor + product).toLowerCase();

        // 1. Remove common "noise" words found in industrial advisories
        String[] noise = {"cpu", "plc", "series", "controller", "inc.", "ltd", "version", "software", "hardware"};
        for (String word : noise) {
            combined = combined.replace(word, "");
        }

        // 2. Remove all non-alphanumeric characters and whitespace
        return combined.replaceAll("[^a-zA-Z0-9]", "");
    }

    /**
     * Converts a version string into a padded 4-segment string for SQL comparison.
     * Example: "2.1.10" -> "00002.00010.00000.00000"
     */
    public String padVersion(String version) {
        if (version == null || version.isEmpty() || version.equalsIgnoreCase("any")) {
            return "00000.00000.00000.00000";
        }

        // 1. Clean the string (remove 'v' prefix or beta tags)
        String cleanVersion = version.toLowerCase()
                .replaceAll("^v", "")
                .replaceAll("[^0-9.]", " ")
                .trim()
                .split("\\s+")[0]; // Take only the first numeric part

        String[] parts = cleanVersion.split("\\.");
        StringBuilder padded = new StringBuilder();

        for (int i = 0; i < 4; i++) {
            if (i > 0) padded.append(".");

            if (i < parts.length) {
                try {
                    // Pad each segment to 5 digits
                    String segment = parts[i];
                    if (segment.length() > 5) segment = segment.substring(0, 5);
                    padded.append(String.format("%05d", Integer.parseInt(segment)));
                } catch (NumberFormatException e) {
                    padded.append("00000");
                }
            } else {
                padded.append("00000");
            }
        }
        return padded.toString();
    }
}