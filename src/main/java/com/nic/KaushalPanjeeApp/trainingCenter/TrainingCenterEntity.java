package com.nic.KaushalPanjeeApp.trainingCenter;

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
@Table(name = "candidate_training_center_details")
public class TrainingCenterEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;

	@Column(name = "login_id", unique = true)
	private String loginId;

	@Column(name = "scheme_name")
	private String schemeName;

	@Column(name = "district_code")
	private String districtCode;

	@Column(name = "pia_code")
	private String piaCode;

	@Column(name = "org_id")
	private String orgId;

	@Column(name = "training_center_id")
	private int trainingCenterId;

	@Column(name = "institute_id")
	private int instituteId;

	@Column(name = "course_id")
	private int courseId;

	@Column(name = "created_on")
	private Date createdOn;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "updated_on")
	private Date updatedOn;

	@Column(name = "updated_by")
	private String updatedBy;

}
