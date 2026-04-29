package com.nic.KaushalPanjeeApp.userdetails;

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
@Table(name = "mst_candidate_address_details")
public class UserAddressDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_address_id")
	private long userAddressId;

	@Column(name = "user_profile_Id")
	private long userProfileId;

	@Column(name = "login_id", unique = true)
	private String loginId;

	@Column(name = "pr_state_code")
	private String prStateCode;

	@Column(name = "pr_locality")
	private String prLocality;

	@Column(name = "temp_locality")
	private String tempLocality;

	@Column(name = "pr_district_code")
	private String prDistrictCode;

	@Column(name = "pr_block_code")
	private String prBlockCode;

	@Column(name = "pr_gp_code")
	private String prGpCode;

	@Column(name = "pr_ulb_code")
	private String prUlbCode;

	@Column(name = "pr_ward_code")
	private String prWardCode;

	@Column(name = "pr_village_code")
	private String prVillageCode;

	@Column(name = "pr_street1")
	private String prStreet1;

	@Column(name = "pr_street2")
	private String prStreet2;

	@Column(name = "prPinCode")
	private String pr_pin_code;

	@Column(name = "is_temp_address_same")
	private String isTempAddressSame;

	@Column(name = "temp_state_code")
	private String tempStateCode;

	@Column(name = "temp_district_code")
	private String tempDistrictCode;

	@Column(name = "temp_block_code")
	private String tempBlockCode;

	@Column(name = "temp_gp_code")
	private String tempGpCode;

	@Column(name = "temp_ulb_code")
	private String tempUlbCode;

	@Column(name = "temp_ward_code")
	private String tempWardCode;

	@Column(name = "temp_village_code")
	private String tempVillageCode;

	@Column(name = "temp_street1")
	private String tempStreet1;

	@Column(name = "temp_street2")
	private String tempStreet2;

	@Column(name = "temp_pin_code")
	private String tempPinCode;

	@Column(name = "secc_state_code")
	private String seccStateCode;

	@Column(name = "secc_district_code")
	private String seccDistrictCode;

	@Column(name = "secc_block_code")
	private String seccBlockCode;

	@Column(name = "seccGpCode")
	private String secc_gp_code;

	@Column(name = "secc_sub_district_code")
	private String seccSubDistrictCode;

	@Column(name = "secc_village_code")
	private String seccVillageCode;

	@Column(name = "secc_candidate_name")
	private String seccCandidateName;

	@Column(name = "secc_ahl_tin")
	private String seccAhlTin;

	@Column(name = "created_on")
	private Date createdOn;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "updated_on")
	private Date updatedOn;

	@Column(name = "updated_by")
	private String updatedBy;

	@Column(name = "change_count")
	private int changeCount;

}
