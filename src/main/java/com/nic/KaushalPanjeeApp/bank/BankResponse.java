package com.nic.KaushalPanjeeApp.bank;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BankResponse<T> {
	private List<T> bankDetailsList = new ArrayList<>();
	private Integer responseCode;
	private String responseDesc;
	private String responseMsg;

	public BankResponse() {
	}

	public BankResponse(List<T> bankDetailsList) {
		this.bankDetailsList = bankDetailsList;
	}

	public BankResponse(List<T> bankDetailsList, Map<String, String> errorsMap) {
		this.bankDetailsList = bankDetailsList;
	}

	public BankResponse(List<T> bankDetailsList, Integer responseCode, String responseDesc) {
		this.bankDetailsList = bankDetailsList;
		this.responseCode = responseCode;
		this.responseDesc = responseDesc;
	}

	public List<T> getBankDetailsList() {
		return bankDetailsList;
	}

	public void setBankDetailsList(List<T> bankDetailsList) {
		this.bankDetailsList = bankDetailsList;
	}

	public Integer getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(Integer responseCode) {
		this.responseCode = responseCode;
	}

	public String getResponseDesc() {
		return responseDesc;
	}

	public void setResponseDesc(String responseDesc) {
		this.responseDesc = responseDesc;
	}

	public String getResponseMsg() {
		return responseMsg;
	}

	public void setResponseMsg(String responseMsg) {
		this.responseMsg = responseMsg;
	}
}
