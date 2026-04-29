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
public class SchemeResponse<T> {
	private List<T> schemeList = new ArrayList<>();
	private Integer responseCode;
	private String responseDesc;
	private String responseMsg;

	public SchemeResponse(List<T> schemeList) {
		this.schemeList = schemeList;
	}

	public SchemeResponse(List<T> schemeList, Map<String, String> errorsMap) {
		this.schemeList = schemeList;
	}

	public SchemeResponse(List<T> schemeList, Integer responseCode, String responseDesc) {
		this.schemeList = schemeList;
		this.responseCode = responseCode;
		this.responseDesc = responseDesc;
	}
}
