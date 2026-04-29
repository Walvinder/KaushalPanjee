package com.nic.KaushalPanjeeApp.helper;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LanguageResponse<T> {
	private List<T> languageList = new ArrayList<>();
	private List<T> centerList = new ArrayList<>();
	private Integer responseCode;
	private String responseDesc;
	private String responseMsg;

	public LanguageResponse(List<T> languageList, List<T> centerList) {
		this.languageList = languageList;
		this.centerList = centerList;
	}

	public LanguageResponse(List<T> languageList, List<T> centerList, Integer responseCode, String responseDesc) {
		this.languageList = languageList;
		this.centerList = centerList;
		this.responseCode = responseCode;
		this.responseDesc = responseDesc;
	}
}
