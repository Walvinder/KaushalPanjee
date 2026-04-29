package com.nic.KaushalPanjeeApp.bank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankBean {
	private String ifscCode;
	private String appVersion;
	private String imeiNo;
	private String loginId;

}
