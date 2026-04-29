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
public class SurveyResponse<T> {
	private List<T> surveyList = new ArrayList<>();
	private Integer responseCode;
	private String responseDesc;
	private String responseMsg;

	public SurveyResponse(List<T> surveyList) {
		this.surveyList = surveyList;
	}

	public SurveyResponse(List<T> surveyList, Map<String, String> errorsMap) {
		this.surveyList = surveyList;
	}

	public SurveyResponse(List<T> surveyList, Integer responseCode, String responseDesc) {
		this.surveyList = surveyList;
		this.responseCode = responseCode;
		this.responseDesc = responseDesc;
	}
}
