package com.nic.KaushalPanjeeApp.unnati;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import org.springframework.http.HttpStatus
import org.apache.hc.core5.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nic.KaushalPanjeeApp.config.EncryptionUtil;
import com.nic.KaushalPanjeeApp.config.SMSBean;
import com.nic.KaushalPanjeeApp.config.SendSMSTest;
import com.nic.KaushalPanjeeApp.helper.HandledException;
import com.nic.KaushalPanjeeApp.helper.MyConstants;
import com.nic.KaushalPanjeeApp.helper.Response;
import com.nic.KaushalPanjeeApp.otp.AuthenticationEntity;
import com.nic.KaushalPanjeeApp.otp.AuthenticationRepository;
import com.nic.KaushalPanjeeApp.userdetails.UserDetails;
import com.nic.KaushalPanjeeApp.userdetails.UserDetailsRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class NregaUnnatiServiceImpl implements NregaUnnatiService {

	@Autowired
	NregaUnnatiApplicantRepository repo;

	@Autowired
	AuthenticationRepository authRepo;

	@Autowired
	UserDetailsRepository userrepo;

	@Autowired
	private EncryptionUtil enc;

	public Response<Map<String, Object>> getNregaUnnatiApplicants(NregaUnnatiApplicantBean req,
			HttpServletRequest httpReq, HttpServletResponse httpRes) {
		Response<Map<String, Object>> response = new Response<>();
		try {

			NregaUnnatiApplicantEntity instance = repo.getNregaUnnatiInstance(req.getJobCardNo(), req.getApplicantNo());
			if (instance == null) {
				NregaUnnatiApplicantEntity obj = new NregaUnnatiApplicantEntity();
				if (!req.getAdhaarHash().trim().isEmpty()) {
					obj.setAdhaarHash(req.getAdhaarHash().trim());
					obj.setUserType(req.getUserType().trim());
					obj.setJobCardNo(req.getJobCardNo().trim());
					obj.setApplicantNo(req.getApplicantNo().trim());
					obj.setApplicantNameEnglish(req.getApplicantNameEnglish().trim());
					obj.setCandidateName(req.getCandidateName().trim());
					obj.setCandidateMobileNo(req.getCandidateMobileNo().trim());
					obj.setPermanentStateId(req.getPermanentStateId().trim());
					obj.setPermanentStateName(req.getPermanentStateName().trim());
					obj.setPermanentDistrictId(req.getPermanentDistrictId().trim());
					obj.setPermanentDistrictName(req.getPermanentDistrictName().trim());
					obj.setFatherName(req.getFatherName().trim());
					obj.setGender(req.getGender().trim());
					obj.setPermanentBlockId(req.getPermanentBlockId().trim());
					obj.setPermanentPanchayatId(req.getPermanentPanchayatId().trim());
					obj.setPermanentPanchayatName(req.getPermanentPanchayatName().trim());
					obj.setPermanentVillageId(req.getPermanentVillageId().trim());
					obj.setPermanentVillageName(req.getPermanentVillageName().trim());
					obj.setCategory(req.getCategory().trim());
					obj.setDateOfBirth(req.getDateOfBirth().trim());
					obj.setCreatedBy("NREGA");
					obj.setCreatedOn(new Date());
					NregaUnnatiApplicantEntity objEntity = (NregaUnnatiApplicantEntity) repo.save(obj);

					response.setAppCode(String.valueOf(objEntity.getId()));
					response.setResponseCode(HttpStatus.SC_OK);
					response.setResponseDesc("Nrega Unnati Applicant details captured successfully.");
				} else {
					response.setResponseCode(HttpStatus.SC_NO_CONTENT);
					response.setResponseDesc("Adhaar number is empty.");
				}
			} else {
				if (!req.getAdhaarHash().trim().isEmpty()) {
					instance.setAdhaarHash(req.getAdhaarHash().trim());
					instance.setUserType(req.getUserType().trim());
					instance.setApplicantNameEnglish(req.getApplicantNameEnglish().trim());
					instance.setCandidateName(req.getCandidateName().trim());
					instance.setCandidateMobileNo(req.getCandidateMobileNo().trim());
					instance.setPermanentStateId(req.getPermanentStateId().trim());
					instance.setPermanentStateName(req.getPermanentStateName().trim());
					instance.setPermanentDistrictId(req.getPermanentDistrictId().trim());
					instance.setPermanentDistrictName(req.getPermanentDistrictName().trim());
					instance.setFatherName(req.getFatherName().trim());
					instance.setGender(req.getGender().trim());
					instance.setPermanentBlockId(req.getPermanentBlockId().trim());
					instance.setPermanentPanchayatId(req.getPermanentPanchayatId().trim());
					instance.setPermanentPanchayatName(req.getPermanentPanchayatName().trim());
					instance.setPermanentVillageId(req.getPermanentVillageId().trim());
					instance.setPermanentVillageName(req.getPermanentVillageName().trim());
					instance.setCategory(req.getCategory().trim());
					instance.setDateOfBirth(req.getDateOfBirth().trim());
					instance.setModifiedBy("NREGA");
					instance.setModifiedOn(new Date());
					repo.save(instance);

					response.setAppCode(String.valueOf(instance.getId()));
					response.setResponseCode(HttpStatus.SC_ALREADY_REPORTED);
					response.setResponseDesc("Nrega Unnati Applicant details is already captured.");

				} else {
					response.setResponseCode(HttpStatus.SC_NO_CONTENT);
					response.setResponseDesc("Adhaar number is empty.");
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return response;
	}

	// public Response<String> checkCandidateExistInUnnathi(CandidateExistsUnnathi
	// user, HttpServletRequest req, HttpServletResponse res) {
	//
	// Response<String> response = new Response<>();
	// List<String> dropdownList = new ArrayList<>();
	//
	// String loginid = user.getLoginId();
	// Timestamp currentTimestamp = Timestamp.from(Instant.now());
	// AuthenticationEntity authinstance = authRepo.getAuthInstance(loginid);
	//
	// if (authinstance == null) {
	// response.setResponseCode(HttpStatus.SC_UNAUTHORIZED);
	// response.setResponseDesc("Unauthorized");
	// return response;
	// }
	//
	// Date updatedDate = authinstance.getUpdatedOn();
	// Instant updatedInstant = updatedDate.toInstant();
	// Instant currentInstant = currentTimestamp.toInstant();
	// long resultTime = ChronoUnit.MINUTES.between(updatedInstant, currentInstant);
	// if (1440 - resultTime <= 0) {
	// authRepo.delete(authinstance);
	// response.setResponseCode(HttpStatus.SC_UNAUTHORIZED);
	// response.setResponseDesc("Unauthorized");
	// return response;
	// }
	// String authHeader = req.getHeader("Authorization");
	// String authToken = null;
	//
	// if (authHeader != null && authHeader.startsWith("Bearer ")) {
	// authToken = authHeader.substring(7); // Remove "Bearer " prefix
	// }
	//
	// boolean authFlag = authRepo.getTokenValidation(loginid, authToken);
	//
	// try {
	// if (authFlag) {
	// String candidateId = user.getCandidateId();
	// String aadhaarNumber = user.getAdhaarHash();
	// // Check if Aadhaar exists in NREGA Unnati table
	// Boolean aadharExists = repo.existsByAAdhar(aadhaarNumber);
	// if (aadharExists) {
	// // ⭐ STEP 3: Insert candidate_id into nrega_unnati_applicant
	// repo.updateCandidateIdByAadhaar(aadhaarNumber, candidateId);
	// dropdownList = Arrays.asList("ddugky", "rseti", "nrlm", "pmkvg",
	// "pmvishwakarma");
	// } else {
	// dropdownList = Arrays.asList("ddugky", "rseti");
	// }
	//
	// // FINAL RESPONSE
	// response.setResponseCode(HttpStatus.SC_OK);
	// response.setResponseDesc("Unnati applicant check completed successfully.");
	// response.setResponseMsg("Success");
	// response.setWrappedList(dropdownList);
	// }
	// else {
	// response.setResponseCode(HttpStatus.SC_UNAUTHORIZED);
	// response.setResponseDesc("Unauthorized");
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// response.setResponseCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
	// response.setResponseDesc("Error while checking Unnati applicant status.");
	// response.setResponseMsg("Failed");
	// }
	//
	// return response;
	// }
	//

	public Response<Map<String, Object>> checkCandidateExistInUnnathi(CandidateExistsUnnathi user,
			HttpServletRequest req, HttpServletResponse res) {

		// Create response object
		Response<Map<String, Object>> response = new Response<>();

		Map<String, Object> result = new HashMap<>();

		String loginId = user.getLoginId();
		Timestamp currentTimestamp = Timestamp.from(Instant.now());
		AuthenticationEntity authInstance = authRepo.getAuthInstance(loginId);

		if (authInstance == null) {
			response.setResponseCode(HttpStatus.SC_UNAUTHORIZED);
			response.setResponseDesc("Unauthorized");
			return response;
		}

		long resultTime = ChronoUnit.MINUTES.between(authInstance.getUpdatedOn().toInstant(),
				currentTimestamp.toInstant());
		// resultTime = 130; // temporary static

		if (1440 - resultTime <= 0) {
			authRepo.delete(authInstance);
			response.setResponseCode(HttpStatus.SC_UNAUTHORIZED);
			response.setResponseDesc("Unauthorized");
			return response;
		}

		String authHeader = req.getHeader("Authorization");
		String authToken = (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : null;

		boolean authFlag = authRepo.getTokenValidation(loginId, authToken);

		try {
			if (!authFlag) {
				response.setResponseCode(HttpStatus.SC_UNAUTHORIZED);
				response.setResponseDesc("Unauthorized");
				return response;
			}

			String aadhaarNumber = user.getAdhaarHash();

			boolean exists = repo.existsByAAdhar(aadhaarNumber);

			if (exists) {

				boolean existsInBatch = repo.checkExistsInBatch(loginId);
				
				repo.updateLoginIdByAadhaar(aadhaarNumber, loginId);

				if (existsInBatch) {
					result.put("status", "Yes");
					result.put("scheme", "RSETI");
				} else {
//					"PMKVY", "PMVishwakarma"
					result.put("status", "Yes");
					result.put("scheme", Arrays.asList("DDUGKY", "RSETI"));
				}

			} else {
				result.put("status", "No");
				result.put("scheme", Arrays.asList("DDUGKY", "RSETI"));
			}

			// put result object directly to wrappedList
			response.setWrappedList(Collections.singletonList(result));

			// set root level fields
			response.setResponseCode(HttpStatus.SC_OK);
			response.setResponseDesc("Unnati applicant check completed successfully.");
			response.setResponseMsg("Success");

		} catch (Exception e) {
			e.printStackTrace();
			response.setResponseCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
			response.setResponseDesc("Error while checking Unnati applicant status.");
			response.setResponseMsg("Failed");
		}

		return response;
	}

	public Response<Map<String, Object>> sendKpIdPassword(HttpServletRequest req, HttpServletResponse res) {

		Response<Map<String, Object>> response = new Response<>();
		try {
			SMSBean smsBean = new SMSBean();
			SendSMSTest sendSMS;
			sendSMS = new SendSMSTest();
			Timestamp currentTimestamp = Timestamp.from(Instant.now());

			String mobileNo = "";
			String loginId = "";
			List<Object[]> userData = repo.getUserData();

			for (Object[] user : userData) {
				loginId = (String) user[0];
				mobileNo = (String) user[1];

				// String loginId = profileRepo.getUserLoginIdPassword(mobileNo, emailId);

				UserDetails userInstance = userrepo.getByLoginId(loginId);

				StringBuilder randomAlphaNumeric = new StringBuilder(10);
				randomAlphaNumeric.append(MyConstants.randomChar(MyConstants.LOWERCASE));
				randomAlphaNumeric.append(MyConstants.randomChar(MyConstants.UPPERCASE));
				randomAlphaNumeric.append(MyConstants.randomChar(MyConstants.SPECIAL_CHARACTERS));
				randomAlphaNumeric.append(MyConstants.randomChar(MyConstants.DIGITS));
				randomAlphaNumeric.append(MyConstants.randomChar(MyConstants.SPECIAL_CHARACTERS));
				for (int i = 4; i < 9; i++) {
					randomAlphaNumeric.append(MyConstants.randomChar(MyConstants.ALL_CHARACTERS));
				}

				String password = MyConstants.shuffleString(randomAlphaNumeric.toString());
				String hashPass = enc.hash(password);
				userInstance.setPassword(hashPass);
				userInstance.setUpdatedOn(currentTimestamp);
				userInstance.setUpdatedBy(userInstance.getUserName());
				userrepo.save(userInstance);

				String sms = "Please note your Login Id " + loginId + " and Password " + password
						+ " for your future Login.";
				smsBean.setMobile(mobileNo);
				smsBean.setUnicodeMsg(true);
				smsBean.setSms(sms);
				smsBean.setTemplateId("1007040091173547449");

				sendSMS.sendSMS(smsBean);

				System.out.println("Kaushal Panjee Id And Password  : " + loginId + password);

				response.setResponseCode(200);
				response.setResponseDesc("Your Login Id and password has been sent your registered mobile number.");
			}

		} catch (HandledException e) {
			e.printStackTrace();
			throw new HandledException(e.getCode(), e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;

	}

}
