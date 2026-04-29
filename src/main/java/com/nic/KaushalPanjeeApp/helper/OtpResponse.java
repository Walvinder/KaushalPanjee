package com.nic.KaushalPanjeeApp.helper;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpResponse<T> {
	private Integer responseCode;
	private String responseDesc;
	private String responseFlag;
	private String candidateId;
	private List<T> wrappedList = new ArrayList<>();

	public OtpResponse(List<T> wrappedList) {
		this.wrappedList = wrappedList;
	}

}
