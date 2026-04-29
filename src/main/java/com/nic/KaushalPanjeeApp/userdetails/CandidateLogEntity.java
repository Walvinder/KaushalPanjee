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
@Table(name = "mst_candidate_app_logs")
public class CandidateLogEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "serial_number")
	private long serialNumber;

	@Column(name = "user_id")
	private String usesId;

	@Column(name = "imei_no")
	private String imeiNo;

	@Column(name = "date_and_time")
	private Date dateAndTime;

	@Column(name = "action_performed")
	private String actionPerformed;

	@Column(name = "status")
	private String status;
}