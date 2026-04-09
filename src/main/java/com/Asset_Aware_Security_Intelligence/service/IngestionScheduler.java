package com.Asset_Aware_Security_Intelligence.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IngestionScheduler {

    private final DiscoveryService discoveryService;

    // Runs at 2:00 AM every day
    @Scheduled(cron = "0 0 2 * * *")
    public void scheduledSync() {
        discoveryService.discoverAndSync();
    }
}