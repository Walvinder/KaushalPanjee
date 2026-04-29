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
@Table(name = "mst_candidate_profile_status")
public class UserProfileStatus {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "profile_status_id")
	private long profileStatusId;

	@Column(name = "user_profile_Id")
	private long userProfileId;

	@Column(name = "login_id", unique = true)
	private String loginId;

	@Column(name = "personal_status")
	private short personalStatus;

	@Column(name = "address_status")
	private short addressStatus;

	@Column(name = "secc_status")
	private short seccStatus;

	@Column(name = "educational_status")
	private short educationalStatus;

	@Column(name = "employment_status")
	private short employmentStatus;

	@Column(name = "training_status")
	private short trainingStatus;

	@Column(name = "banking_status")
	private short bankingStatus;

	@Column(name = "created_on")
	private Date createdOn;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "updated_on")
	private Date updatedOn;

	@Column(name = "updated_by")
	private String updatedBy;

}
