package com.nic.KaushalPanjeeApp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class ExternalDbConfig {

	@Bean(name = "externalJdbcTemplate")
	public JdbcTemplate externalJdbcTemplate() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.postgresql.Driver");
		// dataSource.setUrl("jdbc:postgresql://localhost:5432/ddugky16_04_2025");
		dataSource.setUrl("jdbc:postgresql://10.246.24.201:5432/secc_data");
		dataSource.setUsername("postgres");
		dataSource.setPassword("postgres");
		return new JdbcTemplate(dataSource);
	}

}
