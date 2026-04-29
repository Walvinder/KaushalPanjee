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
public class TechQualificationResponse<T> {
	private List<T> courseList = new ArrayList<>();
	private Integer responseCode;
	private String responseDesc;
	private String responseMsg;

	public TechQualificationResponse(List<T> courseList) {
		this.courseList = courseList;
	}

	public TechQualificationResponse(List<T> courseList, Map<String, String> errorsMap) {
		this.courseList = courseList;
	}

	public TechQualificationResponse(List<T> courseList, Integer responseCode, String responseDesc) {
		this.courseList = courseList;
		this.responseCode = responseCode;
		this.responseDesc = responseDesc;
	}
}
