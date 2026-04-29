package com.nic.KaushalPanjeeApp.helper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Request {
	private String login;
	private String password;
	private String appVersion;
	private String orgCode;
	private String moduleCd;
	private String officialCode;
	private String batchId;
}
