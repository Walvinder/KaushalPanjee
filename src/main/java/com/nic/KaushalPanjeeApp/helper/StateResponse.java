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
public class StateResponse<T> {
	private List<T> stateList = new ArrayList<>();
	private Integer responseCode;
	private String responseDesc;
	private String responseMsg;

	public StateResponse(List<T> stateList) {
		this.stateList = stateList;
	}

	public StateResponse(List<T> stateList, Map<String, String> errorsMap) {
		this.stateList = stateList;
	}

	public StateResponse(List<T> stateList, Integer responseCode, String responseDesc) {
		this.stateList = stateList;
		this.responseCode = responseCode;
		this.responseDesc = responseDesc;
	}
}
