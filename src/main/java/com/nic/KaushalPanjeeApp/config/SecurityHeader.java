package com.nic.KaushalPanjeeApp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class SecurityHeader {

	public void setSecurityHeaders(HttpServletResponse res) {
		res.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
		res.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
		res.setHeader(HttpHeaders.PRAGMA, "no-cache");
		res.setHeader(HttpHeaders.EXPIRES, "0");
		res.setHeader("X-Content-Type-Options", "nosniff");
		res.setHeader("X-XSS-Protection", "1; mode=block");
		res.setHeader("X-Frame-Options", "SAMEORIGIN");
		res.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains; preload");
		res.setHeader("Content-Security-Policy", "script-src 'self'");
		res.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET, POST");
	}

}
