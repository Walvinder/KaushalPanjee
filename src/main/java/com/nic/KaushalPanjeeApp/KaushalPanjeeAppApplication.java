package com.nic.KaushalPanjeeApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class KaushalPanjeeAppApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(KaushalPanjeeAppApplication.class, args);
		System.out.println("Kaushal Panjee App............");
	}

	@Bean
	@LoadBalanced
	RestTemplate loadBalancedRestTemplate() {
		return new RestTemplate();
	}

}
