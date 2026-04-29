package com.nic.KaushalPanjeeApp.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@ControllerAdvice
@RestController
public class HttpMethodBlockingAdvice {
	@RequestMapping(method = { RequestMethod.DELETE, RequestMethod.PUT })
	@ResponseBody
	public ResponseEntity<String> handleRestrictedMethods() {
		return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body("HTTP method not allowed");
	}

	@RequestMapping(method = RequestMethod.OPTIONS)
	@ResponseBody
	public ResponseEntity<String> handleOptions() {
		return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body("HTTP method not allowed");
	}

	@RequestMapping(method = RequestMethod.TRACE)
	@ResponseBody
	public ResponseEntity<String> handleTrace() {
		return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body("HTTP method not allowed");
	}
}
