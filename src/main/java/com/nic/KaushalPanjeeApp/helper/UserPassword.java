package com.nic.KaushalPanjeeApp.helper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPassword {

	private String loginId;
	private String oldPassword;
	private String newPassword;
	private String userInput;
	private String mobileNo;
	private String email;
	private String appVersion;
	private String imeiNo;
	private String otp;
	private String isFaceRegistered;
	private String aadharNo;

}
