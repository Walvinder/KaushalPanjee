package com.nic.KaushalPanjeeApp.helper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpBean {
	private String mobileNo;
	private String email;
	private String qual;
	private String appVersion;
	private String imeiNo;
	private String loginId;
	private String centerName;
	private String sectorId;
	private String districtCode;
	private String centerCode;
	private String data;
	private String otp;
	private String qualCat;
	private String tradeName;
	private String schemeType;
}
