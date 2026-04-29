package com.nic.KaushalPanjeeApp.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DistrictResponse<T> {
	private List<T> districtList = new ArrayList<>();
	private Integer responseCode;
	private String responseDesc;
	private String responseMsg;

	public DistrictResponse(List<T> districtList) {
		this.districtList = districtList;
	}

	public DistrictResponse(List<T> districtList, Map<String, String> errorsMap) {
		this.districtList = districtList;
	}

	public DistrictResponse(List<T> districtList, Integer responseCode, String responseDesc) {
		this.districtList = districtList;
		this.responseCode = responseCode;
		this.responseDesc = responseDesc;
	}
}
