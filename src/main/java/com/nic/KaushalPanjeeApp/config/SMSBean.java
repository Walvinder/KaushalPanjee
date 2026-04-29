package com.nic.KaushalPanjeeApp.config;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SMSBean implements Serializable {
	private static final long serialVersionUID = 1186730212286675266L;

	private String msg;
	private String mobile;
	private boolean isUnicodeMsg;
	private String email;
	private String sms;
	private String templateId;

}
