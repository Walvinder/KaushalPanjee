package com.nic.KaushalPanjeeApp.ojtAttendance;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.nic.KaushalPanjeeApp.helper.MyConstants;
import com.nic.KaushalPanjeeApp.helper.Response;
import com.nic.KaushalPanjeeApp.otp.AuthenticationEntity;
import com.nic.KaushalPanjeeApp.otp.AuthenticationRepository;
import com.nic.KaushalPanjeeApp.otp.OtpRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class OjtAttendanceServideImpl implements OjtAttendanceServide {

	@Autowired
	OjtAttendanceRepository attenRepo;

	@Autowired
	OtpRepository otpRepo;

	@Autowired
	AuthenticationRepository authRepo;

	public Response<Map<String, Object>> insertOjtCandidateAttendance(OjtAttendanceBean req, HttpServletRequest request,
			HttpServletResponse resp) {
		Response<Map<String, Object>> response = new Response<>();
		Timestamp currentTimestamp = Timestamp.from(Instant.now());
		String appVersion = req.getAppVersion();
		String imeiNo = req.getImeiNo();
		String loginId = req.getCandidateId();

		AuthenticationEntity authinstance = authRepo.getAuthInstance(loginId);
		if (authinstance == null) {
			response.setResponseCode(HttpStatus.UNAUTHORIZED.value());
			response.setResponseDesc(HttpStatus.UNAUTHORIZED.name());
			return response;
		}
		Date updatedDate = authinstance.getUpdatedOn();
		Instant updatedInstant = updatedDate.toInstant();
		Instant currentInstant = currentTimestamp.toInstant();
		long resultTime = ChronoUnit.MINUTES.between(updatedInstant, currentInstant);
		if (1440 - resultTime <= 0) {
			authRepo.delete(authinstance);
			response.setResponseCode(HttpStatus.UNAUTHORIZED.value());
			response.setResponseDesc(HttpStatus.UNAUTHORIZED.name());
			return response;
		}
		String authHeader = request.getHeader("Authorization");
		String authToken = null;

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			authToken = authHeader.substring(7); // Remove "Bearer " prefix
		}

		boolean authFlag = authRepo.getTokenValidation(loginId, authToken);
		try {
			if (authFlag) {

				String status = otpRepo.appVersionStatus(appVersion);
				if (status != null && status.equals("Active") && !status.equals("null")) {
					String attendanceDate = "";
					boolean dateFlag = isValidDate(req.getAttendanceDate().trim(), "dd-MM-yyyy");
					if (dateFlag) {
						attendanceDate = req.getAttendanceDate().trim();
					} else {
						attendanceDate = formatDate(req.getAttendanceDate().trim());
					}

					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

					LocalDate localDateTime = LocalDate.parse(attendanceDate, formatter);

					String chekcin = (localDateTime + " " + req.getCheckIn().trim());
					String checkout = (localDateTime + " " + req.getCheckOut().trim());
					String latitute = "";
					String longitute = "";
					List<Object[]> locationList = attenRepo.getUserLocation(req.getBatchId());
					for (Object[] row : locationList) {
						if (row != null && row.length >= 2) {
							latitute = row[0] != null ? row[0].toString() : "N/A";
							longitute = row[1] != null ? row[1].toString() : "N/A";
							System.out.println("latitute : " + latitute);
							System.out.println("longitute : " + longitute);
						}
					}

					OjtAttendance existInstance = attenRepo.getAttendanceInstance(req.getBatchId(),
							req.getCandidateId(), localDateTime);
					if (existInstance == null) {
						OjtAttendance attenInstance = new OjtAttendance();
						attenInstance.setBatchId(req.getBatchId());
						attenInstance.setCandidateId(req.getCandidateId());
						attenInstance.setEmployeersId(req.getEmployeersId());
						attenInstance.setWorkplaceId(req.getWorkplaceId());
						attenInstance.setAttendanceFlag("checkout");
						attenInstance.setCheckIn(chekcin);
						attenInstance.setImeiNo(imeiNo);
						attenInstance.setAttendanceDate(localDateTime);
						attenInstance.setTotalHours(req.getTotalHours());
						attenInstance.setLatitute(latitute);
						attenInstance.setLongitute(longitute);
						attenInstance.setAddress(req.getAddress());
						attenInstance.setCreatedBy(req.getCandidateId());
						attenInstance.setCreatedOn(currentTimestamp);
						attenRepo.save(attenInstance);

					} else {
						existInstance.setAttendanceFlag("checkout");
						existInstance.setCheckOut(checkout);
						existInstance.setTotalHours(req.getTotalHours());
						existInstance.setAddress(req.getAddress());
						existInstance.setImeiNo(imeiNo);
						existInstance.setUpdatedBy(req.getCandidateId());
						existInstance.setUpdatedOn(currentTimestamp);
						attenRepo.save(existInstance);

					}
					response.setResponseCode(HttpStatus.OK.value());
					response.setResponseDesc(HttpStatus.OK.name());

				} else {
					response.setResponseCode(MyConstants.appVersionCodeErr);
					response.setResponseDesc(MyConstants.appVersionNameErr);
				}
			} else {
				response.setResponseCode(HttpStatus.UNAUTHORIZED.value());
				response.setResponseDesc(HttpStatus.UNAUTHORIZED.name());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;

	}

	public static boolean isValidDate(String date, String pattern) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		try {
			LocalDate.parse(date, formatter);
			return true;
		} catch (DateTimeParseException e) {
			return false;
		}
	}

	public static String formatDate(String inputDate) {
		LocalDate date = LocalDate.parse(inputDate); // ISO format
		return date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
	}

}
