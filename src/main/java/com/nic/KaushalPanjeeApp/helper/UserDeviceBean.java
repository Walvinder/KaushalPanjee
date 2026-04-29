package com.nic.KaushalPanjeeApp.helper;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDeviceBean {
	private String loginId;
	private String password;
	private String imeiNo;
	private String appVersion;
	private String deviceName;
	private Timestamp createDate;
	private String fcmToken;

}
