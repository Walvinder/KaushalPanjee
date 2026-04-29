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
public class TechDomainResponse<T> {
	private List<T> domainList = new ArrayList<>();
	private Integer responseCode;
	private String responseDesc;
	private String responseMsg;

	public TechDomainResponse(List<T> domainList) {
		this.domainList = domainList;
	}

	public TechDomainResponse(List<T> domainList, Map<String, String> errorsMap) {
		this.domainList = domainList;
	}

	public TechDomainResponse(List<T> domainList, Integer responseCode, String responseDesc) {
		this.domainList = domainList;
		this.responseCode = responseCode;
		this.responseDesc = responseDesc;
	}
}
