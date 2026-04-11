package com.Asset_Aware_Security_Intelligence;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling   // Powers the future cleanup of expired tokens
@EnableAsync        // Powers background email sending so the UI stays fast
public class AssetAwareSecurityIntelligenceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AssetAwareSecurityIntelligenceApplication.class, args);
	}

}