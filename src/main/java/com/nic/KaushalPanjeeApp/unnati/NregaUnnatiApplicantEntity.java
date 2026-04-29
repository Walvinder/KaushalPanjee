package com.nic.KaushalPanjeeApp.unnati;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "nrega_unnati_applicant")
public class NregaUnnatiApplicantEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;

	@Column(name = "adhaar_hash")
	private String adhaarHash;

	@Column(name = "user_type")
	private String userType;

	@Column(name = "job_card_no")
	private String jobCardNo;

	@Column(name = "applicant_no")
	private String applicantNo;

	@Column(name = "applicant_name_english")
	private String applicantNameEnglish;

	@Column(name = "candidate_name")
	private String candidateName;

	@Column(name = "candidate_mobile_no")
	private String candidateMobileNo;

	@Column(name = "permanent_state_id")
	private String permanentStateId;

	@Column(name = "permanent_state_name")
	private String permanentStateName;

	@Column(name = "permanent_district_id")
	private String permanentDistrictId;

	@Column(name = "permanent_district_name")
	private String permanentDistrictName;

	@Column(name = "permanent_block_id")
	private String permanentBlockId;

	@Column(name = "permanent_panchayat_id")
	private String permanentPanchayatId;

	@Column(name = "permanent_panchayat_name")
	private String permanentPanchayatName;

	@Column(name = "permanent_village_id")
	private String permanentVillageId;

	@Column(name = "permanent_village_name")
	private String permanentVillageName;

	@Column(name = "father_name")
	private String fatherName;

	@Column(name = "gender")
	private String gender;

	@Column(name = "category")
	private String category;

	@Column(name = "date_of_birth")
	private String dateOfBirth;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "created_on")
	private Date createdOn;

	@Column(name = "modified_by")
	private String modifiedBy;

	@Column(name = "modified_on")
	private Date modifiedOn;

	@Column(name = "login_id")
	private String loginId;

}
