package com.nic.KaushalPanjeeApp.tech;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.nic.KaushalPanjeeApp.config.SecurityHeader;
import com.nic.KaushalPanjeeApp.helper.BannerResponse;
import com.nic.KaushalPanjeeApp.helper.HandledException;
import com.nic.KaushalPanjeeApp.helper.LanguageResponse;
import com.nic.KaushalPanjeeApp.helper.MyConstants;
import com.nic.KaushalPanjeeApp.helper.OtpBean;
import com.nic.KaushalPanjeeApp.helper.Response;
import com.nic.KaushalPanjeeApp.helper.SurveyResponse;
import com.nic.KaushalPanjeeApp.helper.TechDomainResponse;
import com.nic.KaushalPanjeeApp.helper.TechQualificationResponse;
import com.nic.KaushalPanjeeApp.otp.AuthenticationEntity;
import com.nic.KaushalPanjeeApp.otp.AuthenticationRepository;
import com.nic.KaushalPanjeeApp.otp.OtpRepository;
import com.nic.KaushalPanjeeApp.userdetails.CandidateLogRepository;
import com.nic.KaushalPanjeeApp.userdetails.UserDeviceInfoRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class TechnicalDetailsServiceImpl implements TechnicalDetailsService {
	@Autowired
	TechnicalDetailsrepository repo;

	@Autowired
	OtpRepository otpRepo;

	@Autowired
	UserDeviceInfoRepository deviceRepo;

	@Autowired
	AuthenticationRepository authRepo;

	@Autowired
	SecurityHeader bnkService;

	@Autowired
	CandidateLogRepository logrepo;

	public TechQualificationResponse<Map<String, Object>> getTechQualificationList(OtpBean qual, HttpServletRequest req,
			HttpServletResponse res) {
		TechQualificationResponse<Map<String, Object>> response = new TechQualificationResponse<>();
		List<Map<String, Object>> wrappedList = new ArrayList<>();
		List<Object[]> qualList = null;
		String loginId = qual.getLoginId();

		Timestamp currentTimestamp = Timestamp.from(Instant.now());
		AuthenticationEntity authinstance = authRepo.getAuthInstance(loginId);
		if (authinstance == null) {
			response.setResponseCode(HttpStatus.UNAUTHORIZED.value());
			response.setResponseDesc(HttpStatus.UNAUTHORIZED.name());
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

		String qualificationCategory = qual.getQualCat();

		if (qualificationCategory == null) {
			response.setCourseList(null);
			response.setResponseCode(400);
			response.setResponseDesc("Qualification Category cannot be empty");
			response.setResponseMsg("Please select Qualification Category");
			return response;
		}

		try {
			if (authFlag) {
				String appVersion = qual.getAppVersion();
				String status = otpRepo.appVersionStatus(appVersion);
				if (status != null && status.equals("Active") && !status.equals("null")) {
					qualList = repo.getTechQualificationList(qualificationCategory);

					if (qualList != null && qualList.size() != 0) {

						wrappedList = qualList.stream().map(p -> {
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("qualCode", p[0]);
							map.put("qualName", p[1]);
							return map;
						})

								.collect(Collectors.toList());

						response.setCourseList(wrappedList);
						response.setResponseCode(HttpStatus.OK.value());
						response.setResponseDesc(HttpStatus.OK.name());
						response.setResponseMsg("");
					} else {
						response.setCourseList(null);
						response.setResponseCode(304);
						response.setResponseDesc("Something went wrong.");
						response.setResponseMsg("Technical Qualification not available.");
					}

				} else {
					response.setResponseCode(301);
					response.setResponseDesc("Please upgread your app first.");
					response.setResponseMsg("Outdated app version.");
				}
			} else {
				response.setResponseCode(HttpStatus.UNAUTHORIZED.value());
				response.setResponseDesc(HttpStatus.UNAUTHORIZED.name());
			}

		} catch (HandledException e) {
			e.printStackTrace();
			throw new HandledException(e.getCode(), e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new HandledException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
		}
		bnkService.setSecurityHeaders(res);
		return response;
	}

	public TechDomainResponse<Map<String, Object>> getQualificationDomainList(OtpBean qual, HttpServletRequest req,
			HttpServletResponse res) {
		TechDomainResponse<Map<String, Object>> response = new TechDomainResponse<>();
		List<Map<String, Object>> wrappedList = new ArrayList<>();
		List<Object[]> schemeList = null;
		String loginId = qual.getLoginId();

		Timestamp currentTimestamp = Timestamp.from(Instant.now());
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
				String appVersion = qual.getAppVersion();
				String status = otpRepo.appVersionStatus(appVersion);
				if (status != null && status.equals("Active") && !status.equals("null")) {
					String qualCode = qual.getQual();
					schemeList = repo.getTechDomainList(qualCode);

					if (schemeList != null && schemeList.size() != 0) {

						wrappedList = schemeList.stream().map(p -> {
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("domainCode", p[0]);
							map.put("domainName", p[1]);
							return map;
						})

								.collect(Collectors.toList());
						response.setDomainList(wrappedList);
						response.setResponseCode(HttpStatus.OK.value());
						response.setResponseDesc(HttpStatus.OK.name());
						response.setResponseMsg("");
					} else {
						response.setDomainList(null);
						response.setResponseCode(304);
						response.setResponseDesc("Something went wrong.");
						response.setResponseMsg("Technical domain not available.");
					}

				} else {
					response.setResponseCode(301);
					response.setResponseDesc("Please upgread your app first.");
					response.setResponseMsg("Outdated app version.");
				}
			} else {
				response.setResponseCode(HttpStatus.UNAUTHORIZED.value());
				response.setResponseDesc(HttpStatus.UNAUTHORIZED.name());
			}

		} catch (HandledException e) {
			e.printStackTrace();
			throw new HandledException(e.getCode(), e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new HandledException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
		}
		bnkService.setSecurityHeaders(res);
		return response;
	}

	public SurveyResponse<Map<String, Object>> getSurveyList(OtpBean appversion, HttpServletRequest req,
			HttpServletResponse res) {
		SurveyResponse<Map<String, Object>> response = new SurveyResponse<>();
		List<Map<String, Object>> wrappedList = new ArrayList<>();
		List<Object[]> surveyList = null;
		String loginId = appversion.getLoginId();

		Timestamp currentTimestamp = Timestamp.from(Instant.now());
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
				String appVersion = appversion.getAppVersion();
				String status = otpRepo.appVersionStatus(appVersion);
				if (status != null && status.equals("Active") && !status.equals("null")) {
					surveyList = repo.getSurveyList();

					if (surveyList != null && surveyList.size() != 0) {

						wrappedList = surveyList.stream().map(p -> {
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("surveyCode", p[0]);
							map.put("mediumName", p[1]);
							return map;
						})

								.collect(Collectors.toList());
						response.setSurveyList(wrappedList);
						response.setResponseCode(HttpStatus.OK.value());
						response.setResponseDesc(HttpStatus.OK.name());
						response.setResponseMsg("");
					} else {
						response.setSurveyList(null);
						response.setResponseCode(304);
						response.setResponseDesc("Something went wrong.");
						response.setResponseMsg("Survey medium not available.");
					}

				} else {
					response.setResponseCode(301);
					response.setResponseDesc("Please upgread your app first.");
					response.setResponseMsg("Outdated app version.");
				}
			} else {
				response.setResponseCode(HttpStatus.UNAUTHORIZED.value());
				response.setResponseDesc(HttpStatus.UNAUTHORIZED.name());
			}

		} catch (HandledException e) {
			e.printStackTrace();
			throw new HandledException(e.getCode(), e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new HandledException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
		}
		bnkService.setSecurityHeaders(res);
		return response;
	}

	public LanguageResponse<Map<String, Object>> getLanguageList(OtpBean appversion, HttpServletRequest req,
			HttpServletResponse res) {
		LanguageResponse<Map<String, Object>> response = new LanguageResponse<>();
		List<Map<String, Object>> wrappedList = new ArrayList<>();
		List<Object[]> languageList = null;
		String loginId = appversion.getLoginId();

		Timestamp currentTimestamp = Timestamp.from(Instant.now());
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
				String appVersion = appversion.getAppVersion();
				String status = otpRepo.appVersionStatus(appVersion);
				if (status != null && status.equals("Active") && !status.equals("null")) {

					languageList = repo.getLanguageList();

					if (languageList != null && languageList.size() != 0) {

						wrappedList = languageList.stream().map(p -> {
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("languageCode", p[0]);
							map.put("languageName", p[1]);
							return map;
						})

								.collect(Collectors.toList());

						response.setLanguageList(wrappedList);
						response.setResponseCode(HttpStatus.OK.value());
						response.setResponseDesc(HttpStatus.OK.name());
						response.setResponseMsg("");
					}

				} else {
					response.setResponseCode(301);
					response.setResponseDesc("Please upgread your app first.");
					response.setResponseMsg("Outdated app version.");
				}
			} else {
				response.setResponseCode(HttpStatus.UNAUTHORIZED.value());
				response.setResponseDesc(HttpStatus.UNAUTHORIZED.name());
			}

		} catch (HandledException e) {
			e.printStackTrace();
			throw new HandledException(e.getCode(), e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new HandledException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
		}
		bnkService.setSecurityHeaders(res);
		return response;
	}

	public LanguageResponse<Map<String, Object>> getTrainingCenterList(OtpBean taining, HttpServletRequest req,
			HttpServletResponse res) {
		LanguageResponse<Map<String, Object>> response = new LanguageResponse<>();
		List<Map<String, Object>> wrappedList = new ArrayList<>();
		List<Object[]> trainingCenterList = null;

		String loginId = taining.getLoginId();

		Timestamp currentTimestamp = Timestamp.from(Instant.now());
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
				String appVersion = taining.getAppVersion();
				String status = otpRepo.appVersionStatus(appVersion);
				String sectorId = taining.getSectorId();
				String districtCode = taining.getDistrictCode();
				if (status != null && status.equals("Active") && !status.equals("null")) {

					trainingCenterList = repo.getTrainingCenterList(districtCode, sectorId);

					if (trainingCenterList != null && trainingCenterList.size() != 0) {

						wrappedList = trainingCenterList.stream().map(p -> {
							String imagePath = null;
							String imagePath1 = null;
							String imagePath2 = null;
							String imagePath3 = null;
							String imagePath4 = null;

							Map<String, Object> map = new HashMap<String, Object>();

							String line1 = (String) p[2];
							String line2 = (String) p[3];
							String line3 = (String) p[4];
							String stateName = (String) p[5];
							String districtName = (String) p[6];
							String blockName = (String) p[7];
							int pinCode = (Integer) p[8];

							StringBuilder result = new StringBuilder();

							if (line1 != null && !line1.trim().isEmpty()) {
								result.append(line1);
							}
							if (line2 != null && !line2.trim().isEmpty()) {
								if (result.length() > 0)
									result.append(", "); // or your separator
								result.append(line2);
							}
							if (line3 != null && !line3.trim().isEmpty()) {
								if (result.length() > 0)
									result.append(", ");
								result.append(line3);
							}
							if (blockName != null && !blockName.trim().isEmpty()) {
								if (result.length() > 0)
									result.append(", ");
								result.append(blockName);
							}
							if (districtName != null && !districtName.trim().isEmpty()) {
								if (result.length() > 0)
									result.append(", ");
								result.append(districtName);
							}
							if (stateName != null && !stateName.trim().isEmpty()) {
								if (result.length() > 0)
									result.append(", ");
								result.append(stateName);
							}
							result.append(" INDIA ");
							result.append(pinCode);

							String file1 = ((String) p[9]).trim();
							String extention1 = (String) p[10];
							String file2 = (String) p[11];
							String extention2 = (String) p[12];
							String file3 = (String) p[13];
							String extention3 = (String) p[14];
							String file4 = (String) p[15];
							String extention4 = (String) p[16];
							String file5 = (String) p[17];
							String extention5 = (String) p[18];
							int instituteId = (Integer) p[19];
							String lattitude = (String) p[20];
							String langitude = (String) p[21];
							BigDecimal radius = (BigDecimal) p[22];

							String photoPath = MyConstants.TRAINING_CENTER_BUILDING;
							String photoPath1 = MyConstants.TRAINING_CENTER_CLASS_ROOM1;
							String photoPath2 = MyConstants.TRAINING_CENTER_CLASS_ROOM2;
							String photoPath3 = MyConstants.TRAINING_CENTER_DORMITORY_LADIES;
							String photoPath4 = MyConstants.TRAINING_CENTER_DORMITORY_GENTS;

							String filePath = photoPath + File.separator + instituteId + "_" + file1 + "." + extention1;
							String filePath1 = photoPath1 + File.separator + instituteId + "_" + file2 + "."
									+ extention2;
							String filePath2 = photoPath2 + File.separator + instituteId + "_" + file3 + "."
									+ extention3;
							String filePath3 = photoPath3 + File.separator + instituteId + "_" + file4 + "."
									+ extention4;
							String filePath4 = photoPath4 + File.separator + instituteId + "_" + file5 + "."
									+ extention5;

							imagePath = MyConstants.getImageAsBase64(filePath);
							imagePath1 = MyConstants.getImageAsBase64(filePath1);
							imagePath2 = MyConstants.getImageAsBase64(filePath2);
							imagePath3 = MyConstants.getImageAsBase64(filePath3);
							imagePath4 = MyConstants.getImageAsBase64(filePath4);

							// System.out.println("imagePath : " + filePath);

							if (file1 != null) {
								imagePath = MyConstants.getImageAsBase64(filePath);
								map.put("centerImage", imagePath);
							} else {
								map.put("centerImage", "N/A");
							}
							if (file2 != null) {
								imagePath1 = MyConstants.getImageAsBase64(filePath1);
								map.put("calssRoom1Image", imagePath1);
							} else {
								map.put("calssRoom1Image", "N/A");
							}
							if (file3 != null) {
								imagePath2 = MyConstants.getImageAsBase64(filePath2);
								map.put("calssRoom2Image", imagePath2);
							} else {
								map.put("calssRoom2Image", "N/A");
							}
							if (file4 != null) {
								imagePath3 = MyConstants.getImageAsBase64(filePath3);
								map.put("ladiesDormitoryImage", imagePath3);
							} else {
								map.put("ladiesDormitoryImage", "N/A");
							}
							if (file5 != null) {
								imagePath4 = MyConstants.getImageAsBase64(filePath4);
								map.put("gentsDormitoryImage", imagePath4);
							} else {
								map.put("gentsDormitoryImage", "N/A");
							}

							map.put("centerName", p[0]);
							map.put("contactNo", p[1]);
							map.put("address", result);
							map.put("latitude", lattitude);
							map.put("langitude", langitude);
							map.put("radius", radius);
							map.put("centerCode", String.valueOf(instituteId));
							return map;
						})

								.collect(Collectors.toList());

						response.setCenterList(wrappedList);
						response.setResponseCode(HttpStatus.OK.value());
						response.setResponseDesc(HttpStatus.OK.name());
						response.setResponseMsg("");
					}

				} else {
					response.setResponseCode(301);
					response.setResponseDesc("Please upgread your app first.");
					response.setResponseMsg("Outdated app version.");
				}
			} else {
				response.setResponseCode(HttpStatus.UNAUTHORIZED.value());
				response.setResponseDesc(HttpStatus.UNAUTHORIZED.name());
			}

		} catch (HandledException e) {
			e.printStackTrace();
			throw new HandledException(e.getCode(), e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new HandledException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
		}
		bnkService.setSecurityHeaders(res);
		return response;
	}

	public String getImageAsBase64(String imagePath) throws IOException {
		File file = new File(imagePath);
		byte[] fileContent = Files.readAllBytes(file.toPath());
		return Base64.getEncoder().encodeToString(fileContent);
	}

	public LanguageResponse<Map<String, Object>> searchTrainingCenterList(OtpBean taining, HttpServletRequest req,
			HttpServletResponse res) {
		LanguageResponse<Map<String, Object>> response = new LanguageResponse<>();
		List<Map<String, Object>> wrappedList = new ArrayList<>();
		List<Object[]> searchTrainingCenter = null;
		String loginId = taining.getLoginId();

		Timestamp currentTimestamp = Timestamp.from(Instant.now());
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
				String appVersion = taining.getAppVersion();
				String status = otpRepo.appVersionStatus(appVersion);
				if (status != null && status.equals("Active") && !status.equals("null")) {
					String centerName = taining.getCenterName();
					String attString = "%";
					String conCenterName = centerName.concat("%");
					String finalCenterName = attString.concat(conCenterName);

					searchTrainingCenter = repo.searchTrainingCenterList(finalCenterName);

					if (searchTrainingCenter != null && searchTrainingCenter.size() != 0) {

						wrappedList = searchTrainingCenter.stream().map(p -> {
							String imagePath = null;
							String imagePath1 = null;
							String imagePath2 = null;
							String imagePath3 = null;
							String imagePath4 = null;

							Map<String, Object> map = new HashMap<String, Object>();

							String line1 = (String) p[2];
							String line2 = (String) p[3];
							String line3 = (String) p[4];
							String stateName = (String) p[5];
							String districtName = (String) p[6];
							String blockName = (String) p[7];
							int pinCode = (Integer) p[8];

							StringBuilder result = new StringBuilder();

							if (line1 != null && !line1.trim().isEmpty()) {
								result.append(line1);
							}
							if (line2 != null && !line2.trim().isEmpty()) {
								if (result.length() > 0)
									result.append(", "); // or your separator
								result.append(line2);
							}
							if (line3 != null && !line3.trim().isEmpty()) {
								if (result.length() > 0)
									result.append(", ");
								result.append(line3);
							}
							if (blockName != null && !blockName.trim().isEmpty()) {
								if (result.length() > 0)
									result.append(", ");
								result.append(blockName);
							}
							if (districtName != null && !districtName.trim().isEmpty()) {
								if (result.length() > 0)
									result.append(", ");
								result.append(districtName);
							}
							if (stateName != null && !stateName.trim().isEmpty()) {
								if (result.length() > 0)
									result.append(", ");
								result.append(stateName);
							}
							result.append(" INDIA ");
							result.append(pinCode);

							String file1 = ((String) p[9]).trim();
							String extention1 = (String) p[10];
							String file2 = (String) p[11];
							String extention2 = (String) p[12];
							String file3 = (String) p[13];
							String extention3 = (String) p[14];
							String file4 = (String) p[15];
							String extention4 = (String) p[16];
							String file5 = (String) p[17];
							String extention5 = (String) p[18];
							int instituteId = (Integer) p[19];
							String lattitude = (String) p[20];
							String langitude = (String) p[21];
							BigDecimal radius = (BigDecimal) p[22];

							String photoPath = MyConstants.TRAINING_CENTER_BUILDING;
							String photoPath1 = MyConstants.TRAINING_CENTER_CLASS_ROOM1;
							String photoPath2 = MyConstants.TRAINING_CENTER_CLASS_ROOM2;
							String photoPath3 = MyConstants.TRAINING_CENTER_DORMITORY_LADIES;
							String photoPath4 = MyConstants.TRAINING_CENTER_DORMITORY_GENTS;

							String filePath = photoPath + File.separator + instituteId + "_" + file1 + "." + extention1;
							String filePath1 = photoPath1 + File.separator + instituteId + "_" + file2 + "."
									+ extention2;
							String filePath2 = photoPath2 + File.separator + instituteId + "_" + file3 + "."
									+ extention3;
							String filePath3 = photoPath3 + File.separator + instituteId + "_" + file4 + "."
									+ extention4;
							String filePath4 = photoPath4 + File.separator + instituteId + "_" + file5 + "."
									+ extention5;

							imagePath = MyConstants.getImageAsBase64(filePath);
							imagePath1 = MyConstants.getImageAsBase64(filePath1);
							imagePath2 = MyConstants.getImageAsBase64(filePath2);
							imagePath3 = MyConstants.getImageAsBase64(filePath3);
							imagePath4 = MyConstants.getImageAsBase64(filePath4);

							// System.out.println("imagePath : " + filePath);

							if (file1 != null) {
								imagePath = MyConstants.getImageAsBase64(filePath);
								map.put("centerImage", imagePath);
							} else {
								map.put("centerImage", "N/A");
							}
							if (file2 != null) {
								imagePath1 = MyConstants.getImageAsBase64(filePath1);
								map.put("calssRoom1Image", imagePath1);
							} else {
								map.put("calssRoom1Image", "N/A");
							}
							if (file3 != null) {
								imagePath2 = MyConstants.getImageAsBase64(filePath2);
								map.put("calssRoom2Image", imagePath2);
							} else {
								map.put("calssRoom2Image", "N/A");
							}
							if (file4 != null) {
								imagePath3 = MyConstants.getImageAsBase64(filePath3);
								map.put("ladiesDormitoryImage", imagePath3);
							} else {
								map.put("ladiesDormitoryImage", "N/A");
							}
							if (file5 != null) {
								imagePath4 = MyConstants.getImageAsBase64(filePath4);
								map.put("gentsDormitoryImage", imagePath4);
							} else {
								map.put("gentsDormitoryImage", "N/A");
							}

							map.put("centerName", p[0]);
							map.put("contactNo", p[1]);
							map.put("address", result);
							map.put("centerCode", String.valueOf(instituteId));
							map.put("latitude", lattitude);
							map.put("langitude", langitude);
							map.put("radius", radius);
							return map;

						})

								.collect(Collectors.toList());

						response.setCenterList(wrappedList);
						response.setResponseCode(HttpStatus.OK.value());
						response.setResponseDesc(HttpStatus.OK.name());
						response.setResponseMsg("");
					}					

				} else {
					response.setResponseCode(301);
					response.setResponseDesc("Please upgread your app first.");
					response.setResponseMsg("Outdated app version.");
				}
			} else {
				response.setResponseCode(HttpStatus.UNAUTHORIZED.value());
				response.setResponseDesc(HttpStatus.UNAUTHORIZED.name());
			}

		} catch (HandledException e) {
			e.printStackTrace();
			throw new HandledException(e.getCode(), e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new HandledException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
		}
		bnkService.setSecurityHeaders(res);
		return response;
	}

	public LanguageResponse<Map<String, Object>> getTrainingCenter(OtpBean taining, HttpServletRequest req,
			HttpServletResponse res) {
		LanguageResponse<Map<String, Object>> response = new LanguageResponse<>();
		List<Map<String, Object>> wrappedList = new ArrayList<>();
		List<Object[]> trainingCenter = null;
		String loginId = taining.getLoginId();

		Timestamp currentTimestamp = Timestamp.from(Instant.now());
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
				String appVersion = taining.getAppVersion();
				String status = otpRepo.appVersionStatus(appVersion);
				if (status != null && status.equals("Active") && !status.equals("null")) {
					String centercode = taining.getCenterCode();

					trainingCenter = repo.getTrainingCenter(Integer.parseInt(centercode));

					if (trainingCenter != null && trainingCenter.size() != 0) {

						wrappedList = trainingCenter.stream().map(p -> {

							String imagePath = null;
							String imagePath1 = null;
							String imagePath2 = null;
							String imagePath3 = null;
							String imagePath4 = null;

							Map<String, Object> map = new HashMap<String, Object>();

							String line1 = (String) p[2];
							String line2 = (String) p[3];
							String line3 = (String) p[4];
							String stateName = (String) p[5];
							String districtName = (String) p[6];
							String blockName = (String) p[7];
							int pinCode = (Integer) p[8];

							StringBuilder result = new StringBuilder();

							if (line1 != null && !line1.trim().isEmpty()) {
								result.append(line1);
							}
							if (line2 != null && !line2.trim().isEmpty()) {
								if (result.length() > 0)
									result.append(", "); // or your separator
								result.append(line2);
							}
							if (line3 != null && !line3.trim().isEmpty()) {
								if (result.length() > 0)
									result.append(", ");
								result.append(line3);
							}
							if (blockName != null && !blockName.trim().isEmpty()) {
								if (result.length() > 0)
									result.append(", ");
								result.append(blockName);
							}
							if (districtName != null && !districtName.trim().isEmpty()) {
								if (result.length() > 0)
									result.append(", ");
								result.append(districtName);
							}
							if (stateName != null && !stateName.trim().isEmpty()) {
								if (result.length() > 0)
									result.append(", ");
								result.append(stateName);
							}
							result.append(" INDIA ");
							result.append(pinCode);

							String file1 = ((String) p[9]).trim();
							String extention1 = (String) p[10];
							String file2 = (String) p[11];
							String extention2 = (String) p[12];
							String file3 = (String) p[13];
							String extention3 = (String) p[14];
							String file4 = (String) p[15];
							String extention4 = (String) p[16];
							String file5 = (String) p[17];
							String extention5 = (String) p[18];
							int instituteId = (Integer) p[19];
							String lattitude = (String) p[20];
							String langitude = (String) p[21];
							BigDecimal radius = (BigDecimal) p[22];

							String photoPath = MyConstants.TRAINING_CENTER_BUILDING;
							String photoPath1 = MyConstants.TRAINING_CENTER_CLASS_ROOM1;
							String photoPath2 = MyConstants.TRAINING_CENTER_CLASS_ROOM2;
							String photoPath3 = MyConstants.TRAINING_CENTER_DORMITORY_LADIES;
							String photoPath4 = MyConstants.TRAINING_CENTER_DORMITORY_GENTS;

							String filePath = photoPath + File.separator + instituteId + "_" + file1 + "." + extention1;
							String filePath1 = photoPath1 + File.separator + instituteId + "_" + file2 + "."
									+ extention2;
							String filePath2 = photoPath2 + File.separator + instituteId + "_" + file3 + "."
									+ extention3;
							String filePath3 = photoPath3 + File.separator + instituteId + "_" + file4 + "."
									+ extention4;
							String filePath4 = photoPath4 + File.separator + instituteId + "_" + file5 + "."
									+ extention5;

							imagePath = MyConstants.getImageAsBase64(filePath);
							imagePath1 = MyConstants.getImageAsBase64(filePath1);
							imagePath2 = MyConstants.getImageAsBase64(filePath2);
							imagePath3 = MyConstants.getImageAsBase64(filePath3);
							imagePath4 = MyConstants.getImageAsBase64(filePath4);

							// System.out.println("imagePath : " + filePath);

							if (file1 != null) {
								imagePath = MyConstants.getImageAsBase64(filePath);
								map.put("centerImage", imagePath);
							} else {
								map.put("centerImage", "N/A");
							}
							if (file2 != null) {
								imagePath1 = MyConstants.getImageAsBase64(filePath1);
								map.put("calssRoom1Image", imagePath1);
							} else {
								map.put("calssRoom1Image", "N/A");
							}
							if (file3 != null) {
								imagePath2 = MyConstants.getImageAsBase64(filePath2);
								map.put("calssRoom2Image", imagePath2);
							} else {
								map.put("calssRoom2Image", "N/A");
							}
							if (file4 != null) {
								imagePath3 = MyConstants.getImageAsBase64(filePath3);
								map.put("ladiesDormitoryImage", imagePath3);
							} else {
								map.put("ladiesDormitoryImage", "N/A");
							}
							if (file5 != null) {
								imagePath4 = MyConstants.getImageAsBase64(filePath4);
								map.put("gentsDormitoryImage", imagePath4);
							} else {
								map.put("gentsDormitoryImage", "N/A");
							}

							map.put("centerName", p[0]);
							map.put("contactNo", p[1]);
							map.put("address", result);
							map.put("centerCode", String.valueOf(instituteId));
							map.put("latitude", lattitude);
							map.put("langitude", langitude);
							map.put("radius", radius);
							return map;
						})

								.collect(Collectors.toList());

						response.setCenterList(wrappedList);
						response.setResponseCode(HttpStatus.OK.value());
						response.setResponseDesc(HttpStatus.OK.name());
						response.setResponseMsg("");
					}

				} else {
					response.setResponseCode(301);
					response.setResponseDesc("Please upgread your app first.");
					response.setResponseMsg("Outdated app version.");
				}
			} else {
				response.setResponseCode(HttpStatus.UNAUTHORIZED.value());
				response.setResponseDesc(HttpStatus.UNAUTHORIZED.name());
			}

		} catch (HandledException e) {
			e.printStackTrace();
			throw new HandledException(e.getCode(), e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new HandledException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
		}
		bnkService.setSecurityHeaders(res);
		return response;
	}

	public BannerResponse<Map<String, Object>> getBanner(OtpBean taining, HttpServletRequest req,
			HttpServletResponse res) {
		BannerResponse<Map<String, Object>> response = new BannerResponse<>();
		List<Map<String, Object>> wrappedList = new ArrayList<>();
		List<Object[]> bannerList = null;
		String loginId = taining.getLoginId();

		Timestamp currentTimestamp = Timestamp.from(Instant.now());
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
				String appVersion = taining.getAppVersion();
				String status = otpRepo.appVersionStatus(appVersion);
				if (status != null && status.equals("Active") && !status.equals("null")) {

					bannerList = repo.getBannerList();

					if (bannerList != null && bannerList.size() != 0) {

						wrappedList = bannerList.stream().map(p -> {
							String imagePath = null;
							Map<String, Object> map = new HashMap<String, Object>();
							String bannerImage = (String) p[1];

							String photoPath = MyConstants.BANNER;
							String filePath = photoPath + File.separator + bannerImage;
							imagePath = MyConstants.getImageAsBase64(filePath);

							// System.out.println("imagePath : " + filePath);

							if (bannerImage != null) {
								imagePath = MyConstants.getImageAsBase64(filePath);
								map.put("bannerImage", imagePath);
							} else {
								map.put("bannerImage", "N/A");
							}
							map.put("bannerId", p[0]);
							return map;
						})

								.collect(Collectors.toList());

						response.setBannerList(wrappedList);
						response.setResponseCode(HttpStatus.OK.value());
						response.setResponseDesc(HttpStatus.OK.name());
						response.setResponseMsg("");
					}

				} else {
					response.setResponseCode(301);
					response.setResponseDesc("Please upgread your app first.");
					response.setResponseMsg("Outdated app version.");
				}
			} else {
				response.setResponseCode(HttpStatus.UNAUTHORIZED.value());
				response.setResponseDesc(HttpStatus.UNAUTHORIZED.name());
			}

		} catch (HandledException e) {
			e.printStackTrace();
			throw new HandledException(e.getCode(), e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new HandledException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
		}
		bnkService.setSecurityHeaders(res);
		return response;
	}

	public Response<Map<String, Object>> getSchemeList(OtpBean taining, HttpServletRequest req,
			HttpServletResponse res) {
		Response<Map<String, Object>> response = new Response<>();
		List<Map<String, Object>> wrappedList = new ArrayList<>();
		List<Object[]> bannerList = null;
		String loginId = taining.getLoginId();

		Timestamp currentTimestamp = Timestamp.from(Instant.now());
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
				String appVersion = taining.getAppVersion();
				String status = otpRepo.appVersionStatus(appVersion);
				if (status != null && status.equals("Active") && !status.equals("null")) {

					bannerList = repo.getBannerList();

					if (bannerList != null && bannerList.size() != 0) {

						wrappedList = bannerList.stream().map(p -> {
							String imagePath = null;
							Map<String, Object> map = new HashMap<String, Object>();
							String bannerImage = (String) p[1];

							String photoPath = MyConstants.BANNER;
							String filePath = photoPath + File.separator + bannerImage;
							imagePath = MyConstants.getImageAsBase64(filePath);

							// System.out.println("imagePath : " + filePath);

							if (bannerImage != null) {
								imagePath = MyConstants.getImageAsBase64(filePath);
								map.put("bannerImage", imagePath);
							} else {
								map.put("bannerImage", "N/A");
							}
							map.put("bannerId", p[0]);
							return map;
						})

								.collect(Collectors.toList());

						response.setWrappedList(wrappedList);
						response.setResponseCode(HttpStatus.OK.value());
						response.setResponseDesc(HttpStatus.OK.name());
						response.setResponseMsg("");
					}

				} else {
					response.setResponseCode(301);
					response.setResponseDesc("Please upgread your app first.");
					response.setResponseMsg("Outdated app version.");
				}
			} else {
				response.setResponseCode(HttpStatus.UNAUTHORIZED.value());
				response.setResponseDesc(HttpStatus.UNAUTHORIZED.name());
			}

		} catch (HandledException e) {
			e.printStackTrace();
			throw new HandledException(e.getCode(), e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new HandledException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
		}
		bnkService.setSecurityHeaders(res);
		return response;
	}
}
