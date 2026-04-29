package com.nic.KaushalPanjeeApp.links;

import java.util.Map;

import lombok.Data;

@Data
public class LinkResponse {
	private String responseCode;
	private String message;
	private Object data;

	public LinkResponse(String responseCode, String message, Map<String, String> data) {
		this.responseCode = responseCode;
		this.message = message;
		this.data = data;
	}
}
