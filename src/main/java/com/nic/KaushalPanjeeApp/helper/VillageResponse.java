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
public class VillageResponse<T> {
	private List<T> villageList = new ArrayList<>();
	private Integer responseCode;
	private String responseDesc;
	private String responseMsg;

	public VillageResponse(List<T> villageList) {
		this.villageList = villageList;
	}

	public VillageResponse(List<T> villageList, Map<String, String> errorsMap) {
		this.villageList = villageList;
	}

	public VillageResponse(List<T> villageList, Integer responseCode, String responseDesc) {
		this.villageList = villageList;
		this.responseCode = responseCode;
		this.responseDesc = responseDesc;
	}

}
