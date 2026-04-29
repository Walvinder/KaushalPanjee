package com.nic.KaushalPanjeeApp.helper;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreationBean {
	private String aadharNo;
	private String candidateName;
	private String gender;
	private String dateOfBirth;
	private String stateName;
	private String stateCode;
	private String stateLgdCode;
	private String districtName;
	private String blockName;
	private String postOffice;
	private String village;
	private String pinCode;
	private String mobileNo;
	private String email;
	private String careOf;
	private String street;
	private String appVersion;
	private String aadharImage;
	private MultipartFile file;
	private String imeiNo;
	private Boolean userConsent;
	private String aadharSHANo;
	private String schemeFlag;
	private long schemeId;
	private String loginId;
	private String fcmToken;

}
