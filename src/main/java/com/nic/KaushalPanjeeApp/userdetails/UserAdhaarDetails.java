package com.nic.KaushalPanjeeApp.userdetails;

import java.time.LocalDate;
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
@Table(name = "mst_candidate_adhaar_details")
public class UserAdhaarDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_adhaar_id")
	private long userAdhaarId;

	@Column(name = "user_profile_Id")
	private long userProfileId;

	@Column(name = "login_id", unique = true)
	private String loginId;

	@Column(name = "mobile_no")
	private String mobileNo;

	@Column(name = "email_id")
	private String emailId;

	@Column(name = "care_of")
	private String careOf;

	@Column(name = "state_code")
	private String stateCode;

	@Column(name = "aadhar_state_name")
	private String aadharStateName;

	@Column(name = "aadhar_district_name")
	private String aadharDistrictName;

	@Column(name = "aadhar_block_name")
	private String aadharBlockName;

	@Column(name = "aadhar_post_office")
	private String aadharPostOffice;

	@Column(name = "aadhar_gp_name")
	private String aadharGpName;

	@Column(name = "aadhar_village_name")
	private String aadharVillageName;

	@Column(name = "aadhar_location")
	private String aadharLocation;

	@Column(name = "aadhar_pin_code")
	private String aadharPinCode;

	@Column(name = "aadhar_no")
	private String aadharNo;

	@Column(name = "registered_from")
	private String registeredFrom;

	@Column(name = "hash_aadhaar_no")
	private String aadharSHANo;

	@Column(name = "scheme_flag")
	private String schemeFlag;

	@Column(name = "is_face_registered")
	private String isFaceRegistered;

	@Column(name = "scheme_id")
	private Long schemeId;

	@Column(name = "candidate_name")
	private String candidateName;

	@Column(name = "gender")
	private String gender;

	@Column(name = "date_of_birth")
	private LocalDate dateOfBirth;

	@Column(name = "guardian_name")
	private String guardianName;

	@Column(name = "mother_name")
	private String motherName;

	@Column(name = "guardian_mobile_no")
	private String guardianMobileNo;

	@Column(name = "user_consent")
	private boolean userConsent;

	@Column(name = "in_batch")
	private boolean inBatch;

	@Column(name = "remove_count")
	private Integer removeCount;

	@Column(name = "drop_out_count")
	private Integer dropOutCount;

	@Column(name = "created_on")
	private Date createdOn;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "updated_on")
	private Date updatedOn;

	@Column(name = "updated_by")
	private String updatedBy;

	@Column(name = "candidate_remark")
	private String candidateRemark;
}
