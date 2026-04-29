package com.nic.KaushalPanjeeApp.aadhaarLog;

import java.sql.Timestamp;

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
@Table(name = "kaushal_panjee_aadhaar_txn_logs")
public class AadhaarTransactionLogEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name="aadhaar_txn_id")
	private String aadhaarTxnId;
	
	@Column(name="app_txn_id")
	private String appTxnId;

	@Column(name = "status")
	private String status;

	@Column(name = "aadhaar_code")
	private String aadhaarCode;
	
	@Column(name="created_on")
	private Timestamp createdOn;

}
