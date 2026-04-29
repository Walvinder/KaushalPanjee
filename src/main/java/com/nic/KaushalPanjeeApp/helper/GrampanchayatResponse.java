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
public class GrampanchayatResponse<T> {
	private List<T> grampanchayatList = new ArrayList<>();
	private Integer responseCode;
	private String responseDesc;
	private String responseMsg;

	public GrampanchayatResponse(List<T> grampanchayatList) {
		this.grampanchayatList = grampanchayatList;
	}

	public GrampanchayatResponse(List<T> grampanchayatList, Map<String, String> errorsMap) {
		this.grampanchayatList = grampanchayatList;
	}

	public GrampanchayatResponse(List<T> grampanchayatList, Integer responseCode, String responseDesc) {
		this.grampanchayatList = grampanchayatList;
		this.responseCode = responseCode;
		this.responseDesc = responseDesc;
	}
}
