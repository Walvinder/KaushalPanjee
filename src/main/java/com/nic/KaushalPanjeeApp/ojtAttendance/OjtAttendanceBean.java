package com.nic.KaushalPanjeeApp.ojtAttendance;

import lombok.Data;

@Data
public class OjtAttendanceBean {
	private String appVersion;
	private int batchId;
	private String candidateId;
	private int workplaceId;
	private int employeersId;
	private String imeiNo;
	private String checkIn;
	private String checkOut;
	private String attendanceDate;
	private String totalHours;
	private String latitute;
	private String longitute;
	private String address;

}
