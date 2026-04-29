package com.nic.KaushalPanjeeApp.helper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationBean {
	private String stateCode;
	private String districtCode;
	private String lgdDistrictCode;
	private String blockCode;
	private String gpCode;
	private String ulbCode;
	private String appVersion;
	private String seccName;
	private String lgdVillCode;
	private String loginId;
	private String imeiNo;
	private String schemeType;
	private String piaId;
	private String orgId;
	private int trainingCenterId;
	private String instituteId;
	private int courseId;

}
