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
@Table(name = "candidate_user")
public class UserDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private long userId;

	@Column(name = "user_name")
	private String userName;

	@Column(name = "user_profile_Id")
	private long userProfileId;

	@Column(name = "login_id", unique = true)
	private String loginId;

	@Column(name = "login_status")
	private String loginStatus;

	@Column(name = "login_attempt")
	private short loginAttempt;

	@Column(name = "password")
	private String password;

	@Column(name = "fcm_token")
	private String fcmToken;

	@Column(name = "password_changed")
	private String passwordChanged;

	@Column(name = "created_on")
	private Date createdOn;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "updated_on")
	private Date updatedOn;

	@Column(name = "updated_by")
	private String updatedBy;
}
