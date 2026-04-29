package com.nic.KaushalPanjeeApp.ojtAttendance;

import java.sql.Timestamp;
import java.time.LocalDate;

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
@Table(name = "mst_ojt_attendance")
public class OjtAttendance {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "attendance_id")
	private Long attendanceId;
	
	@Column(name="batch_id")
	private int batchId;
	
	@Column(name="candidate_id")
	private String candidateId;

	@Column(name = "workplace_id")
	private int workplaceId;

	@Column(name = "employeers_id")
	private int employeersId;
	
	@Column(name="attendance_flag")
	private String attendanceFlag;
	
	@Column(name="imei_no")
	private String imeiNo;
	
	@Column(name="check_in")
	private String checkIn;
	
	@Column(name="check_out")
	private String checkOut;
	
	@Column(name="attendance_date")
	private LocalDate attendanceDate;
	
	@Column(name="total_hours")
	private String totalHours;
	
	@Column(name = "latitute")
	private String latitute;
	
	@Column(name = "longitute")
	private String longitute;
	
	@Column(name = "address")
	private String address;
	
	@Column(name="created_on")
	private Timestamp createdOn;
	
	@Column(name="created_by")
	private String createdBy;
	
	@Column(name="updated_on")
	private Timestamp updatedOn;
	
	@Column(name="updated_by")
	private String updatedBy;	

}
