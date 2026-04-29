package com.nic.KaushalPanjeeApp.helper;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HandledException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private HttpStatus code;
	List<FieldError> errors;

	public HandledException(HttpStatus code, String message) {
		super(message);
		this.setCode(code);
	}

}
