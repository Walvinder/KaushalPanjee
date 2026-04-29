package com.nic.KaushalPanjeeApp.helper;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MasterResponse<T> {
	private List<T> personalList = new ArrayList<>();
	private List<T> addressList = new ArrayList<>();
	private List<T> seccList = new ArrayList<>();
	private List<T> educationalList = new ArrayList<>();
	private List<T> employementList = new ArrayList<>();
	private List<T> trainingList = new ArrayList<>();
	private List<T> bankList = new ArrayList<>();
	private Integer responseCode;
	private String responseDesc;

	public MasterResponse(List<T> personalList, List<T> addressList, List<T> seccList, List<T> educationalList,
			List<T> employementList, List<T> trainingList, List<T> bankList) {
		super();
		this.personalList = personalList;
		this.addressList = addressList;
		this.seccList = seccList;
		this.educationalList = educationalList;
		this.employementList = employementList;
		this.trainingList = trainingList;
		this.bankList = bankList;
	}

}
