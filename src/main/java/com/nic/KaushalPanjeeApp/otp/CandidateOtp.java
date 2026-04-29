package com.nic.KaushalPanjeeApp.otp;

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
@Table(name = "mst_candidate_otp")
public class CandidateOtp {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "otp_id")
	private long otpId;

	@Column(name = "imei_no")
	private String imeiNo;

	@Column(name = "otp")
	private String otp;

	@Column(name = "mobile_no")
	private String mobileNo;

	@Column(name = "email_id")
	private String emailId;

	@Column(name = "is_verified")
	private boolean isVerified;

	@Column(name = "otp_attempt")
	private int otpAttempt;

	@Column(name = "created_on")
	private Date createdOn;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "updated_on")
	private Date updatedOn;

	@Column(name = "updated_by")
	private String updatedBy;
}