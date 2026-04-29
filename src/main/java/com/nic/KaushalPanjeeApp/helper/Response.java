package com.nic.KaushalPanjeeApp.helper;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response<T> {
	private List<T> wrappedList = new ArrayList<>();
	private Integer responseCode;
	private String responseDesc;
	private String responseMsg;
	private String appCode;

	public Response(List<T> wrappedList) {
		this.wrappedList = wrappedList;
	}

	public Response(List<T> wrappedList, Integer responseCode, String responseDesc, String appCode) {
		this.wrappedList = wrappedList;
		this.responseCode = responseCode;
		this.responseDesc = responseDesc;
		this.appCode = appCode;
	}
}
