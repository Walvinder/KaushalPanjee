package com.nic.KaushalPanjeeApp.helper;

import java.util.Date;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExceptionResponse {

	private Integer responseCode;
	private Date requestDate;
	private String url;
	private String errorMsg;
	private Map<String, String> errors;
	private String responseDesc;

	public ExceptionResponse(Integer responseCode, Date requestDate, String url, String errorMsg, String responseDesc) {
		super();
		this.responseCode = responseCode;
		this.requestDate = requestDate;
		this.url = url;
		this.errorMsg = errorMsg;
		this.responseDesc = responseDesc;
	}

	public ExceptionResponse(Integer responseCode, Date requestDate, String url, Map<String, String> errors,
			String responseDesc) {
		super();
		this.responseCode = responseCode;
		this.requestDate = requestDate;
		this.url = url;
		this.errors = errors;
		this.responseDesc = responseDesc;
	}

}
