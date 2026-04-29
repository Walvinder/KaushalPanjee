package com.nic.KaushalPanjeeApp.helper;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InsertUserResponse<T> {
	private List<T> wrappedList = new ArrayList<>();
	private Integer responseCode;
	private String responseDesc;
	private String responseStatus;
	private String responsePercentage;

	public InsertUserResponse(List<T> wrappedList) {
		this.wrappedList = wrappedList;
	}
}
