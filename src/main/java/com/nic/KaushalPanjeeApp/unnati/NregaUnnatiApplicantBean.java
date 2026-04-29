package com.nic.KaushalPanjeeApp.unnati;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NregaUnnatiApplicantBean {
	private String adhaarHash;
	private String userType;
	private String jobCardNo;
	private String applicantNo;
	private String applicantNameEnglish;
	private String candidateName;
	private String candidateMobileNo;
	private String permanentStateId;
	private String permanentStateName;
	private String permanentDistrictId;
	private String permanentDistrictName;
	private String permanentBlockId;
	private String permanentPanchayatId;
	private String permanentPanchayatName;
	private String permanentVillageId;
	private String permanentVillageName;
	private String fatherName;
	private String gender;
	private String category;
	private String dateOfBirth;

}
