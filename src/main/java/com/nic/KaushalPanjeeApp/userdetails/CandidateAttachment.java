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
@Table(name = "candidate_attachment")
public class CandidateAttachment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "attachment_id")
	private long attachmentId;

	@Column(name = "user_profile_Id")
	private long userProfileId;

	@Column(name = "login_id")
	private String loginId;

	@Column(name = "aadhar_image")
	private String aadharImage;

	@Column(name = "category_cert")
	private String categoryCert;

	@Column(name = "voter_id_card")
	private String voterIdCard;

	@Column(name = "dl_card")
	private String dlCard;

	@Column(name = "residence_cert")
	private String residenceCert;

	@Column(name = "minority_cert")
	private String minorityCert;

	@Column(name = "disablity_cert")
	private String disablityCert;

	@Column(name = "narega_card")
	private String naregaCard;

	@Column(name = "ration_card")
	private String rationCard;

	@Column(name = "rsby_card")
	private String rsbyCard;

	@Column(name = "pmayg_attachment")
	private String pmaygAttachment;

	@Column(name = "pip_cert")
	private String pipCert;

	@Column(name = "shg_image")
	private String shgImage;

	@Column(name = "created_on")
	private Date createdOn;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "updated_on")
	private Date updatedOn;

	@Column(name = "updated_by")
	private String updatedBy;
}
