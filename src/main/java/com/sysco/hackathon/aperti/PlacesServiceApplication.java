package com.sysco.hackathon.aperti;

import com.sysco.hackathon.aperti.service.ScheduledMockService;
import com.sysco.hackathon.aperti.util.ApiUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static com.sysco.hackathon.aperti.util.Constants.*;

@SpringBootApplication
public class PlacesServiceApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(PlacesServiceApplication.class, args);
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(@NotNull CorsRegistry registry) {
				registry.addMapping("/**").allowedOrigins("http://localhost:4200", "https://b4cd-220-247-229-50.ngrok-free.app");
			}
		};
	}

	@Override
	public void run(String... args) {
		ApiUtils apiUtils = new ApiUtils();
		ScheduledMockService scheduledMockService = new ScheduledMockService();
		customerMap.put("043", apiUtils.readCustomerFile("sfdcCustomers043.json"));
		customerMap.put("056", apiUtils.readCustomerFile("sfdcCustomers056.json"));
		customerMap.put("067", apiUtils.readCustomerFile("sfdcCustomers067.json"));
		opcoMap.putAll(apiUtils.readOpCoDataFile("mockOpcoDetails.json"));
		windowsList.addAll(scheduledMockService.getMockSchedules());
	}

}
