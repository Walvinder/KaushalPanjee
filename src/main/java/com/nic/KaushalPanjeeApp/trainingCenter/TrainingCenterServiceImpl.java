package com.nic.KaushalPanjeeApp.trainingCenter;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.nic.KaushalPanjeeApp.config.SecurityHeader;
import com.nic.KaushalPanjeeApp.helper.HandledException;
import com.nic.KaushalPanjeeApp.helper.LocationBean;
import com.nic.KaushalPanjeeApp.helper.MyConstants;
import com.nic.KaushalPanjeeApp.helper.Response;
import com.nic.KaushalPanjeeApp.otp.AuthenticationEntity;
import com.nic.KaushalPanjeeApp.otp.AuthenticationRepository;
import com.nic.KaushalPanjeeApp.otp.OtpRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class TrainingCenterServiceImpl implements TrainingCenterService {

	@Autowired
	TrainingCenterRepository repo;

	@Autowired
	OtpRepository otpRepo;

	@Autowired
	AuthenticationRepository authRepo;

	@Autowired
	SecurityHeader secure;

	public Response<Map<String, Object>> getKpPiaDetails(LocationBean location, HttpServletRequest req,
			HttpServletResponse res) {
		Response<Map<String, Object>> response = new Response<>();
		List<Map<String, Object>> wrappedList = new ArrayList<>();
		List<Object[]> piaOrgList = null;
		String status = otpRepo.appVersionStatus(location.getAppVersion());

		try {
			if (status != null && status.equals("Active") && !status.equals("null")) {
				if (location.getSchemeType().trim().equals("DDUGKY")) {
					piaOrgList = repo.getKpPiaDetails(location.getDistrictCode());
				} else if (location.getSchemeType().trim().equals("RSETI")) {
					piaOrgList = repo.getKpOrganizationDetails(location.getDistrictCode());
				}

				if (piaOrgList != null && piaOrgList.size() != 0) {

					wrappedList = piaOrgList.stream().map(p -> {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("piaOrgCode", p[0]);
						map.put("piaOrgName", p[1]);
						return map;
					})

							.collect(Collectors.toList());
					response.setWrappedList(wrappedList);
					response.setResponseCode(HttpStatus.OK.value());
					response.setResponseDesc(HttpStatus.OK.name());

				} else {
					response.setResponseCode(MyConstants.dataCodeErr);
					response.setResponseMsg(MyConstants.dataNameErr);
					response.setResponseDesc("PIA/Organization not available in this district.");

				}

			} else {
				response.setResponseCode(MyConstants.appVersionCodeErr);
				response.setResponseMsg(MyConstants.appVersionNameErr);
				response.setResponseDesc("Please upgrade your app first.");

			}
		} catch (HandledException e) {
			e.printStackTrace();
			throw new HandledException(e.getCode(), e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new HandledException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
		}
		secure.setSecurityHeaders(res);
		return response;

	}

	public Response<Map<String, Object>> getKpTrainingCenters(LocationBean location, HttpServletRequest req,
			HttpServletResponse res) {
		Response<Map<String, Object>> response = new Response<>();
		List<Map<String, Object>> wrappedList = new ArrayList<>();
		List<Object[]> trainingCenterList = null;
		String status = otpRepo.appVersionStatus(location.getAppVersion());

		try {
			if (status != null && status.equals("Active") && !status.equals("null")) {

				trainingCenterList = repo.getKpPiaTrainingCenterList(location.getPiaId());

				if (trainingCenterList != null && trainingCenterList.size() != 0) {

					wrappedList = trainingCenterList.stream().map(p -> {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("trainingCenterId", p[0]);
						map.put("trainingCenterName", p[1]);
						return map;
					})

							.collect(Collectors.toList());
					response.setWrappedList(wrappedList);
					response.setResponseCode(HttpStatus.OK.value());
					response.setResponseDesc(HttpStatus.OK.name());

				} else {
					response.setResponseCode(MyConstants.dataCodeErr);
					response.setResponseMsg(MyConstants.dataNameErr);
					response.setResponseDesc("Training Center not available of this PIA.");

				}

			} else {
				response.setResponseCode(MyConstants.appVersionCodeErr);
				response.setResponseMsg(MyConstants.appVersionNameErr);
				response.setResponseDesc("Please upgrade your app first.");

			}
		} catch (HandledException e) {
			e.printStackTrace();
			throw new HandledException(e.getCode(), e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new HandledException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
		}
		secure.setSecurityHeaders(res);
		return response;

	}

	public Response<Map<String, Object>> getOrgKpTrainingCenters(LocationBean location, HttpServletRequest req,
			HttpServletResponse res) {
		Response<Map<String, Object>> response = new Response<>();
		List<Map<String, Object>> wrappedList = new ArrayList<>();
		List<Object[]> trainingCenterList = null;
		String status = otpRepo.appVersionStatus(location.getAppVersion());

		try {
			if (status != null && status.equals("Active") && !status.equals("null")) {

				trainingCenterList = repo.getKpOrgInstituteList(location.getOrgId());

				if (trainingCenterList != null && trainingCenterList.size() != 0) {

					wrappedList = trainingCenterList.stream().map(p -> {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("instituteId", p[0]);
						map.put("instituteName", p[1]);
						return map;
					})

							.collect(Collectors.toList());
					response.setWrappedList(wrappedList);
					response.setResponseCode(HttpStatus.OK.value());
					response.setResponseDesc(HttpStatus.OK.name());

				} else {
					response.setResponseCode(MyConstants.dataCodeErr);
					response.setResponseMsg(MyConstants.dataNameErr);
					response.setResponseDesc("Institute not available of this organization.");

				}

			} else {
				response.setResponseCode(MyConstants.appVersionCodeErr);
				response.setResponseMsg(MyConstants.appVersionNameErr);
				response.setResponseDesc("Please upgrade your app first.");

			}
		} catch (HandledException e) {
			e.printStackTrace();
			throw new HandledException(e.getCode(), e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new HandledException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
		}
		secure.setSecurityHeaders(res);
		return response;

	}

	public Response<Map<String, Object>> getTrainingCenterCourses(LocationBean location, HttpServletRequest req,
			HttpServletResponse res) {
		Response<Map<String, Object>> response = new Response<>();
		List<Map<String, Object>> wrappedList = new ArrayList<>();
		List<Object[]> tcTradeList = null;
		String status = otpRepo.appVersionStatus(location.getAppVersion());

		try {
			if (status != null && status.equals("Active") && !status.equals("null")) {

				tcTradeList = repo.getKpTrainingCenterCourseList(location.getTrainingCenterId());

				if (tcTradeList != null && tcTradeList.size() != 0) {

					wrappedList = tcTradeList.stream().map(p -> {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("courseId", p[0]);
						map.put("courseName", p[1]);
						return map;
					})

							.collect(Collectors.toList());
					response.setWrappedList(wrappedList);
					response.setResponseCode(HttpStatus.OK.value());
					response.setResponseDesc(HttpStatus.OK.name());

				} else {
					response.setResponseCode(MyConstants.dataCodeErr);
					response.setResponseMsg(MyConstants.dataNameErr);
					response.setResponseDesc("Course not available in this training center.");

				}

			} else {
				response.setResponseCode(MyConstants.appVersionCodeErr);
				response.setResponseMsg(MyConstants.appVersionNameErr);
				response.setResponseDesc("Please upgrade your app first.");

			}
		} catch (HandledException e) {
			e.printStackTrace();
			throw new HandledException(e.getCode(), e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new HandledException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
		}
		secure.setSecurityHeaders(res);
		return response;

	}

	public Response<Map<String, Object>> getInstituteCourses(LocationBean location, HttpServletRequest req,
			HttpServletResponse res) {
		Response<Map<String, Object>> response = new Response<>();
		List<Map<String, Object>> wrappedList = new ArrayList<>();
		List<Object[]> tcTradeList = null;
		String status = otpRepo.appVersionStatus(location.getAppVersion());

		try {
			if (status != null && status.equals("Active") && !status.equals("null")) {

				tcTradeList = repo.getKpInstituteCourseList(location.getInstituteId());

				if (tcTradeList != null && tcTradeList.size() != 0) {

					wrappedList = tcTradeList.stream().map(p -> {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("instCourseId", p[0]);
						map.put("instCourseName", p[1]);
						return map;
					})

							.collect(Collectors.toList());
					response.setWrappedList(wrappedList);
					response.setResponseCode(HttpStatus.OK.value());
					response.setResponseDesc(HttpStatus.OK.name());

				} else {
					response.setResponseCode(MyConstants.dataCodeErr);
					response.setResponseMsg(MyConstants.dataNameErr);
					response.setResponseDesc("Course not available in this Institute.");

				}

			} else {
				response.setResponseCode(MyConstants.appVersionCodeErr);
				response.setResponseMsg(MyConstants.appVersionNameErr);
				response.setResponseDesc("Please upgrade your app first.");

			}
		} catch (HandledException e) {
			e.printStackTrace();
			throw new HandledException(e.getCode(), e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new HandledException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
		}
		secure.setSecurityHeaders(res);
		return response;

	}

	public Response<Map<String, Object>> insertCandidateTrainingCenter(LocationBean location, HttpServletRequest req,
			HttpServletResponse res) {
		Response<Map<String, Object>> response = new Response<>();
		String loginId = location.getLoginId();
		Timestamp currentTimestamp = Timestamp.from(Instant.now());
		String status = otpRepo.appVersionStatus(location.getAppVersion());
		int trainingCenterId = location.getTrainingCenterId();
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
		String authHeader = req.getHeader("Authorization");
		String authToken = null;

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			authToken = authHeader.substring(7); // Remove "Bearer " prefix
		}

		boolean authFlag = authRepo.getTokenValidation(loginId, authToken);

		try {
			if (authFlag) {
				if (status != null && status.equals("Active") && !status.equals("null")) {

					TrainingCenterEntity instance = repo.getTrainingCenterInstance(loginId);

					if (instance == null) {
						TrainingCenterEntity obj = new TrainingCenterEntity();

						obj.setLoginId(loginId);
						obj.setSchemeName(location.getSchemeType());
						obj.setDistrictCode(location.getDistrictCode());
						obj.setPiaCode(location.getPiaId());
						obj.setOrgId(location.getOrgId());
						obj.setTrainingCenterId(trainingCenterId);
						obj.setInstituteId(Integer.parseInt(location.getInstituteId()));
						obj.setCourseId(location.getCourseId());
						obj.setCreatedBy(loginId);
						obj.setCreatedOn(currentTimestamp);
						repo.save(obj);

					} else {
						instance.setSchemeName(location.getSchemeType());
						instance.setDistrictCode(location.getDistrictCode());
						instance.setPiaCode(location.getPiaId());
						instance.setOrgId(location.getOrgId());
						instance.setTrainingCenterId(trainingCenterId);
						instance.setInstituteId(Integer.parseInt(location.getInstituteId()));
						instance.setCourseId(location.getCourseId());
						instance.setUpdatedBy(loginId);
						instance.setUpdatedOn(currentTimestamp);
						repo.save(instance);

					}

					response.setResponseCode(HttpStatus.OK.value());
					response.setResponseDesc("Details Saved Successfully.");

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

}
