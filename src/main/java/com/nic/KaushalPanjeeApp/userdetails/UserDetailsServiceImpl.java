package com.nic.KaushalPanjeeApp.userdetails;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.nic.KaushalPanjeeApp.aadhaarLog.AadhaarTransactionLogEntity;
import com.nic.KaushalPanjeeApp.aadhaarLog.AadhaarTransactionLogRepository;
import com.nic.KaushalPanjeeApp.aadhaarLog.AadhaarTxnRequest;
import com.nic.KaushalPanjeeApp.aadhaarLog.BankDetailsResponse;
import com.nic.KaushalPanjeeApp.aadhaarLog.BankRequest;
import com.nic.KaushalPanjeeApp.config.EncryptionUtil;
import com.nic.KaushalPanjeeApp.config.SMSBean;
import com.nic.KaushalPanjeeApp.config.SecurityHeader;
import com.nic.KaushalPanjeeApp.config.SendMail;
import com.nic.KaushalPanjeeApp.config.SendSMSTest;
import com.nic.KaushalPanjeeApp.config.Validator;
import com.nic.KaushalPanjeeApp.helper.HandledException;
import com.nic.KaushalPanjeeApp.helper.MasterResponse;
import com.nic.KaushalPanjeeApp.helper.MyConstants;
import com.nic.KaushalPanjeeApp.helper.OtpResponse;
import com.nic.KaushalPanjeeApp.helper.Response;
import com.nic.KaushalPanjeeApp.helper.UserCreationBean;
import com.nic.KaushalPanjeeApp.helper.UserDetailsBean;
import com.nic.KaushalPanjeeApp.helper.UserDeviceBean;
import com.nic.KaushalPanjeeApp.helper.UserPassword;
import com.nic.KaushalPanjeeApp.otp.AuthenticationEntity;
import com.nic.KaushalPanjeeApp.otp.AuthenticationRepository;
import com.nic.KaushalPanjeeApp.otp.OtpRepository;

import jakarta.persistence.PersistenceException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	UserDetailsRepository repo;

	@Autowired
	UserProfileRepository profileRepo;

	@Autowired
	UserDeviceInfoRepository deviceRepo;

	@Autowired
	OtpRepository otpRepo;

	@Autowired
	CandidateAttachmentRepository attachRepo;

	@Autowired
	AuthenticationRepository authRepo;

	@Autowired
	SecurityHeader secure;

	@Autowired
	CandidateLogRepository logrepo;

	@Autowired
	UserAddressDetailsRepository addressRepo;

	@Autowired
	UserAdhaarDetailsRepository adhaarRepo;

	@Autowired
	UserBankDetailsRepository bankRepo;

	@Autowired
	UserProfileStatusRepository statusRepo;

	@Autowired
	private EncryptionUtil enc;

	@Autowired
	private AadhaarTransactionLogRepository aadhaarLogRepo;

	@Autowired
	private YearSequenceRepository yearSeqRepo;

	/* =======Create User implementation======= */

	public Response<Map<String, Object>> createUser(UserCreationBean user, HttpServletRequest req,
			HttpServletResponse res) throws IOException {
		/*
		 * System.out.println("UserCreation : " + "aadharNo= ' " + user.getAadharNo() +
		 * '\'' + ", candidateName= '" + user.getCandidateName() + '\'' + ", gender= '"
		 * + user.getGender() + '\'' + ", dateOfBirth= '" + user.getDateOfBirth() + '\''
		 * + ", stateName= '" + user.getStateName() + '\'' + ", stateCode= '" +
		 * user.getStateCode() + '\'' + ", stateLgdCode= '" + user.getStateLgdCode() +
		 * '\'' + ", districtName= '" + user.getDistrictName() + '\'' + ", blockName= '"
		 * + user.getBlockName() + '\'' + ", postOffice= '" + user.getPostOffice() +
		 * '\'' + ", village= '" + user.getVillage() + '\'' + ", pinCode= '" +
		 * user.getPinCode() + '\'' + ", mobileNo= '" + user.getMobileNo() + '\'' +
		 * ", email= '" + user.getMobileNo() + '\'' + ", careOf= '" + user.getCareOf() +
		 * '\'' + ", street= '" + user.getStreet() + '\'' + ", aadharImage= '" +
		 * user.getAadharImage() + '\'' + ", imeiNo= '" + user.getImeiNo() + '\'' +
		 * ", userConsent= " + user.getUserConsent());
		 */

		Response<Map<String, Object>> response = new Response<>();
		List<Map<String, Object>> wrappedList = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		String imeiNo = "";

		if (user.getImeiNo() != null && !user.getImeiNo().trim().isEmpty()) {
			imeiNo = user.getImeiNo();
		} else {
			response.setResponseCode(334);
			response.setResponseDesc("Something went wrong. Please restart the app.");
			return response;
		}

		String userID = "";
		String stateName = "";
		SMSBean smsBean = new SMSBean();
		SendSMSTest sendSMS;
		sendSMS = new SendSMSTest();
		try {

			String appVersion = user.getAppVersion();
			String status = otpRepo.appVersionStatus(appVersion);
			if (status != null && status.equals("Active") && !status.equals("null")) {
				String msg;

				String decEmailId = "";
				String adhaarNo = user.getAadharNo().replace("\n", "");
				String aadhaarNumber = "";
				String encAadhaarNumber = "";

				if (user.getEmail() != null && !user.getEmail().trim().isEmpty()) {
					decEmailId = enc.decrypt(user.getEmail());
				}

				if (user.getAadharNo() != null && !user.getAadharNo().trim().isEmpty()) {
					aadhaarNumber = enc.decrypt(adhaarNo);
					encAadhaarNumber = enc.encryptNew(aadhaarNumber);
				}

				boolean aadhaarFlag = adhaarRepo.isAadhaarExistOrNot(encAadhaarNumber);
				if (aadhaarFlag) {
					System.out.println("Adhaar already Exist");
					response.setResponseCode(333);
					response.setResponseDesc(
							"User has been already registered with this aadhaar number, please register with different aadhaar number.");
					return response;

				} else {

					UserDetails details = new UserDetails();
					UserProfile profile = new UserProfile();
					CandidateAttachment attachement = new CandidateAttachment();
					CandidateLogEntity logInstance = new CandidateLogEntity();
					UserAddressDetails addressInst = new UserAddressDetails();
					UserAdhaarDetails adhaarInst = new UserAdhaarDetails();
					// UserBankDetails bankInst = new UserBankDetails();
					UserDeviceInfo device = new UserDeviceInfo();
					UserProfileStatus statusInst = new UserProfileStatus();
					Validator validator = new Validator();
					Timestamp currentTimestamp = Timestamp.from(Instant.now());

					AuthenticationEntity authinstance = authRepo.getAuthenticationInstance(imeiNo);
					String authtoken = authRepo.getAuthenticationToken(imeiNo);

					if (user.getAadharNo() != null && !user.getAadharNo().trim().isEmpty()) {
						adhaarInst.setAadharNo(encAadhaarNumber);
						adhaarInst.setAadharSHANo(enc.hash(aadhaarNumber));
					}

					String candidateName = user.getCandidateName().replace("\n", "");
					if (user.getCandidateName() != null && !user.getCandidateName().trim().isEmpty()) {
						adhaarInst.setCandidateName(enc.decrypt(candidateName));
					}

					String careOf = user.getCareOf().replace("\n", "");
					if (user.getCareOf() != null && !user.getCareOf().trim().isEmpty()) {
						adhaarInst.setCareOf(enc.decrypt(careOf));
					}
					if (user.getGender() != null && !user.getGender().trim().isEmpty()) {
						adhaarInst.setGender(enc.decrypt(user.getGender()));
					}

					if (user.getDateOfBirth() != null && !user.getDateOfBirth().trim().isEmpty()) {
						String dateOfBirth = enc.decrypt(user.getDateOfBirth());

						boolean minAgeCheckFlag = Validator.isLessThanYears(dateOfBirth, "dd-MM-yyyy");
						// boolean maxAgeCheckFlag = Validator.isGreaterThanYears(dateOfBirth,
						// "dd-MM-yyyy");
						if (minAgeCheckFlag) {
							response.setResponseCode(334);
							response.setResponseMsg("Candidate age should be greater then 14 years old.");
							return response;
						}

						DateTimeFormatter dtformatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
						LocalDate timestamp = LocalDate.parse(dateOfBirth, dtformatter);
						adhaarInst.setDateOfBirth(timestamp);
					}

					if (user.getStateName() != null && !user.getStateName().trim().isEmpty()) {
						stateName = enc.decrypt(user.getStateName());
						adhaarInst.setAadharStateName(stateName);
					}

					if (user.getStateCode() != null) {
						String userStateCode = enc.decrypt(user.getStateCode());
						if (!userStateCode.trim().isEmpty()) {
							adhaarInst.setStateCode(userStateCode);
						} else {
							response.setResponseCode(333);
							response.setResponseMsg("Please select state");
							return response;
						}
					} else {
						response.setResponseCode(333);
						response.setResponseMsg("Please select state");
						return response;
					}

					String districtName = user.getDistrictName().replace("\n", "");
					if (user.getDistrictName() != null && !user.getDistrictName().trim().isEmpty()) {
						adhaarInst.setAadharDistrictName(enc.decrypt(districtName));
					}

					String blockName = user.getBlockName().replace("\n", "");
					if (user.getBlockName() != null && !user.getBlockName().trim().isEmpty()) {
						adhaarInst.setAadharBlockName(enc.decrypt(blockName));
					}

					String postOffice = user.getPostOffice().replace("\n", "");
					if (user.getPostOffice() != null && !user.getPostOffice().trim().isEmpty()) {
						adhaarInst.setAadharPostOffice(enc.decrypt(postOffice));
					}

					String village = user.getVillage().replace("\n", "");

					if (user.getVillage() != null && !user.getVillage().trim().isEmpty()) {
						adhaarInst.setAadharVillageName(enc.decrypt(village));
					}

					String street = user.getStreet().replace("\n", "");

					if (user.getStreet() != null && !user.getStreet().trim().isEmpty()) {
						adhaarInst.setAadharLocation(enc.decrypt(street));
					}

					if (user.getPinCode() != null && !user.getPinCode().trim().isEmpty()) {
						adhaarInst.setAadharPinCode(enc.decrypt(user.getPinCode()));
					}

					if (user.getMobileNo() != null && !user.getMobileNo().trim().isEmpty()) {
						String decMobileNo = enc.decrypt(user.getMobileNo());
						if (!validator.isMobileValid(decMobileNo)) {
							response.setResponseCode(201);
							response.setResponseDesc("Invalid mobile number format");
							return response;
						} else {
							adhaarInst.setMobileNo(decMobileNo);
						}
					}

					if (decEmailId != null && !decEmailId.trim().isEmpty()) {

						if (!validator.isEmailValid(decEmailId)) {
							response.setResponseCode(201);
							response.setResponseDesc("Invalid email Id!");
							return response;
						} else {
							adhaarInst.setEmailId(decEmailId);
						}

					}

					adhaarInst.setUserConsent(user.getUserConsent());
					adhaarInst.setIsFaceRegistered("N");
					adhaarInst.setRegisteredFrom("App");
					adhaarInst.setRemoveCount(0);
					adhaarInst.setDropOutCount(0);

					/* =====================User Id Creation ===================== */
					Date crdate = new Date();
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
					String date = formatter.format(crdate);
					LocalDate currentDate = LocalDate.parse(date);
					int year = currentDate.getYear();
					String finYear = String.valueOf(year);
					String crfinyr = finYear.substring(finYear.length() - 2);

					YearSequence yearSeq = yearSeqRepo.findByYear(year);

					if (yearSeq == null) {
						yearSeqRepo.resetYearSequence();

						yearSeq = new YearSequence(year, 1);
					} else {
						yearSeqRepo.getLatestSequence();
					}
					YearSequence yearSeqObj = (YearSequence) yearSeqRepo.save(yearSeq);
					int sequenceNumber = yearSeqObj.getLastSequence();

					String value = String.format("%06d", sequenceNumber);

					StringBuilder stringBuilder = new StringBuilder();
					String conStateCode = "";

					stringBuilder.append(crfinyr);
					if (user.getStateLgdCode() != null) {
						String lgdStateCode = enc.decrypt(user.getStateLgdCode());
						if (!lgdStateCode.trim().isEmpty()) {

							if (lgdStateCode.length() < 2) {
								conStateCode = ("0").concat(lgdStateCode);
								stringBuilder.append(conStateCode);
							} else {
								stringBuilder.append(lgdStateCode);
							}

						} else {
							response.setResponseCode(333);
							response.setResponseMsg("Please select state");
							return response;
						}
					} else {
						response.setResponseCode(333);
						response.setResponseMsg("Please select state");
						return response;
					}

					stringBuilder.append(value);

					userID = stringBuilder.toString();

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
					// System.out.println("Password hash code : " + hashPass);

					adhaarInst.setCreatedOn(currentTimestamp);
					details.setUserName(userID);
					details.setPasswordChanged("N");
					details.setFcmToken(user.getFcmToken());
					details.setCreatedBy(userID);
					details.setUpdatedBy(userID);
					adhaarInst.setCreatedBy(userID);
					adhaarInst.setUpdatedBy(userID);
					profile.setCreatedBy(userID);
					profile.setUpdatedBy(userID);
					attachement.setCreatedBy(userID);
					attachement.setUpdatedBy(userID);
					device.setCreatedBy(userID);
					device.setUpdatedBy(userID);
					// bankInst.setCreatedBy(userID);
					// bankInst.setUpdatedBy(userID);
					statusInst.setCreatedBy(userID);
					statusInst.setUpdatedBy(userID);
					addressInst.setCreatedBy(userID);
					addressInst.setUpdatedBy(userID);

					adhaarInst.setUpdatedOn(currentTimestamp);

					profile.setCreatedOn(currentTimestamp);
					profile.setUpdatedOn(currentTimestamp);
					attachement.setCreatedOn(currentTimestamp);
					attachement.setUpdatedOn(currentTimestamp);
					// bankInst.setCreatedOn(currentTimestamp);
					// bankInst.setUpdatedOn(currentTimestamp);
					statusInst.setCreatedOn(currentTimestamp);
					statusInst.setUpdatedOn(currentTimestamp);
					addressInst.setCreatedOn(currentTimestamp);
					addressInst.setUpdatedOn(currentTimestamp);

					profile.setLoginId(userID);
					UserProfile profileInst = (UserProfile) profileRepo.save(profile);

					adhaarInst.setLoginId(userID);
					adhaarInst.setUserProfileId(profileInst.getUserProfileId());
					details.setLoginId(userID);
					details.setUserProfileId(profileInst.getUserProfileId());
					// bankInst.setLoginId(userID);
					// bankInst.setUserProfileId(profileInst.getUserProfileId());
					addressInst.setLoginId(userID);
					addressInst.setUserProfileId(profileInst.getUserProfileId());
					attachement.setLoginId(userID);
					attachement.setUserProfileId(profileInst.getUserProfileId());
					statusInst.setLoginId(userID);
					statusInst.setUserProfileId(profileInst.getUserProfileId());
					addressRepo.save(addressInst);
					// bankRepo.save(bankInst);
					statusRepo.save(statusInst);
					adhaarRepo.save(adhaarInst);

					/* ==========================Save adhaar Image===================== */

					String imageData = user.getAadharImage().trim();
					byte[] decodeBytes = Base64.getDecoder().decode(imageData);
					String fileType = MyConstants.detectFileType(decodeBytes);

					if (fileType.equals("4044")) {
						response.setResponseCode(210);
						response.setResponseDesc("File format not supported");
						return response;
					}
					MultipartFile file = MyConstants.convertToMultipartFile(decodeBytes, fileType);
					File uploadsDir = null;
					String fileExtension = null;
					if (file != null) {
						uploadsDir = new File(MyConstants.UPLOAD_ADHAAR + stateName);
						if (!uploadsDir.exists()) {
							uploadsDir.mkdir();
						}
						fileExtension = MyConstants.getFileExtension(file.getOriginalFilename());
						attachement.setAadharImage("_adhaar_card." + fileExtension);

					} else {
						response.setWrappedList(null);
						response.setResponseMsg("Pleae upload the image");
						response.setResponseCode(201);
						response.setResponseDesc("File not found");
						return response;
					}

					attachRepo.save(attachement);

					file.transferTo(new File(uploadsDir + File.separator + attachement.getAttachmentId()
							+ "_adhaar_card." + fileExtension));

					/* ===================================================================== */

					details.setPassword(hashPass);
					details.setLoginStatus("logged");
					details.setLoginAttempt((short) 0);
					details.setCreatedOn(currentTimestamp);
					details.setUpdatedOn(currentTimestamp);
					repo.save(details);

					/* ================Save Device Details============== */

					device.setLoginId(userID);
					device.setImeiNo(imeiNo);
					device.setStatus("Active");
					device.setCreatedOn(currentTimestamp);
					device.setUpdatedOn(currentTimestamp);
					deviceRepo.save(device);

					if (authinstance == null) {
						AuthenticationEntity authobj = new AuthenticationEntity();
						authobj.setImeiNo(imeiNo);
						authobj.setLoginId(userID);
						Date date1 = new Date();
						authobj.setCreatedOn(date1);
						authobj.setCreatedBy("NIC");
						authobj.setUpdatedOn(date1);
						authobj.setUpdatedBy("NIC");
						authRepo.save(authinstance);

					} else {

						authinstance.setUpdatedOn(currentTimestamp);
						authinstance.setLoginId(userID);
						authRepo.save(authinstance);
					}

					/* ===========END============= */

					map.put("userId", userID);
					map.put("appCode", authtoken);
					wrappedList.add(map);
					logInstance.setUsesId(enc.encryptNew(userID));
					logInstance.setImeiNo(enc.encryptNew(imeiNo));
					logInstance.setDateAndTime(currentTimestamp);
					logInstance.setActionPerformed(enc.encryptNew("Create User Page - New user being created"));
					logInstance.setStatus(enc.encryptNew("Success"));
					logrepo.save(logInstance);
					String MobileNumber = "";
					if (user.getMobileNo() != null && !user.getMobileNo().trim().isEmpty()) {
						MobileNumber = enc.decrypt(user.getMobileNo());
					}

					String sms = "Please note your Login Id " + userID + " and Password " + password
							+ " for your future Login.";
					smsBean.setMobile(MobileNumber);
					smsBean.setUnicodeMsg(true);
					smsBean.setSms(sms);
					smsBean.setTemplateId("1007040091173547449");

					if (!smsBean.getMobile().trim().isEmpty()) {

						sendSMS.sendSMS(smsBean);
					}

					msg = "<html xmlns=\"http://www.w3.org/1999/xhtml\" xmlns=\"http://www.w3.org/1999/xhtml\"> "
							+ "<head> "
							+ "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" /> "
							+ "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" /> "
							+ "<title>Candidate Login Id and Password</title> " + "</head> "
							+ "<body style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box;width: 100% !important;height: 100%;margin: 0;line-height: 1.4;background-color: #F2F4F6;color: #74787E;-webkit-text-size-adjust: none\"> "
							+ "<span class=\"preheader\" style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box;display: none !important;visibility: hidden;mso-hide: all;font-size: 1px;line-height: 1px;max-height: 0;max-width: 0;opacity: 0;overflow: hidden\">Login Id and Password for Kaushal Panjee mobile app</span> <table class=\"email-wrapper\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box;width: 100%;margin: 0;padding: 0;-premailer-width: 100%;-premailer-cellpadding: 0;-premailer-cellspacing: 0;background-color: #F2F4F6\"> "
							+ "<tr style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box\"> "
							+ "<td align=\"center\" style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box;word-break: break-word\"> "
							+ "<table class=\"email-content\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box;width: 100%;margin: 0;padding: 0;-premailer-width: 100%;-premailer-cellpadding: 0;-premailer-cellspacing: 0\"> "
							+ "<tr style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box\"> "
							+ "<td class=\"email-masthead\" style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box;word-break: break-word;padding: 5px 0\"><a href=\"https://kaushal.rural.gov.in\" class=\"email-masthead_name\" style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box;color: #5e769c;font-size: 18px;line-height: 3;margin-left: 22px !important;font-weight: 500;text-decoration: none;text-shadow: 0 1px 0 white\"> Kaushal Panjee App </a> </td> </tr> <!-- Email Body --> "
							+ "<tr style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box\"> <td class=\"email-body\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box;word-break: break-word;width: 100%;margin: 0;padding: 0;-premailer-width: 100%;-premailer-cellpadding: 0;-premailer-cellspacing: 0;border-top: 1px solid #EDEFF2;border-bottom: 1px solid #EDEFF2;background-color: #FFF\"> <table class=\"email-body_inner\" align=\"center\" width=\"570\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box;width: 570px;margin: 0 auto;padding: 0;-premailer-width: 570px;-premailer-cellpadding: 0;-premailer-cellspacing: 0;background-color: #FFF\"> <!-- Body content --> <tr style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box\"> <td class=\"content-cell\" style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box;word-break: break-word;padding: 35px\"> <h1 style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box;margin-top: 0;color: #2F3133;font-size: 19px;font-weight: bold;text-align: left\">Dear User,</h1> <p style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box;line-height: 1.5em;text-align: left;margin-top: 0;color: #74787E;font-size: 16px\">Please note your Login Id (Kaushal Panjee ID) <b> "
							+ userID + " </b>and Password <b>" + password
							+ "</b> for your future Login on Kaushal Panjee App. <strong style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box\"></strong></p> <!-- Action --> "
							+ "<p style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box;line-height: 1.5em;text-align: left;margin-top: 0;color: #74787E;font-size: 16px\">For your security, do not share your login id and password with anyone. If you did not initiate this registration request, please ignore this email and report to <b style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box\">grameen.kaushal@nic.in</b></p> <p style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box;line-height: 1.5em;text-align: left;margin-top: 0;color: #74787E;font-size: 16px\">Thanks, <br style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box\" />Grameen Kaushal Team</p> <!-- Sub copy --> </td> </tr> </table> </td> </tr> <tr style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box\"> <td style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box;word-break: break-word\"> <table class=\"email-footer\" align=\"center\" width=\"570\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box;width: 570px;margin: 0 auto;padding: 0;-premailer-width: 570px;-premailer-cellpadding: 0;-premailer-cellspacing: 0;text-align: center\"> <tr style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box\"> <td class=\"content-cell\" align=\"center\" style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box;word-break: break-word;padding: 35px\"> <p class=\"sub align-center\" style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box;line-height: 1.5em;text-align: left;color: #AEAEAE;margin-top: 0;font-size: 12px\"></p> <p class=\"sub align-center\" style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box;line-height: 1.5em;text-align: left;color: #AEAEAE;margin-top: 0;font-size: 12px\"> Grameen Kaushalya Yojana (GKY) </p> </td> </tr> </table> </td> </tr> </table> </td> </tr> </table> </body></html>"
							+ "				  ";

					SendMail sendmail = new SendMail();
					if (decEmailId != null && !decEmailId.trim().isEmpty()) {
						sendmail.sendMail(decEmailId, "Login Id and Password for Kaushal panjee App", msg);
					}

					response.setWrappedList(wrappedList);
					response.setResponseMsg("User created successfully");
					response.setResponseCode(HttpStatus.OK.value());
					response.setResponseDesc(HttpStatus.OK.name());
				}

			} else {
				response.setResponseCode(301);
				response.setResponseDesc("Please upgread your app first.");
				response.setResponseMsg("Outdated app version.");
			}

		} catch (PersistenceException ex) {
			Throwable cause = ex.getCause();
			if (cause instanceof ConstraintViolationException) {
				response.setResponseCode(501);
				response.setResponseDesc("A record with the same details already exists.");
				return response;
			}

		} catch (UncheckedIOException e) {
			throw new UncheckedIOException(e.getCause().getMessage(), e.getCause());

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

	/* =======Login Service======= */

	public Response<Map<String, Object>> loginUser(UserDeviceBean login, HttpServletRequest req,
			HttpServletResponse res) {
		Response<Map<String, Object>> response = new Response<>();
		Timestamp currentTimestamp = Timestamp.from(Instant.now());
		String appVersion = login.getAppVersion();
		String loginId = login.getLoginId();
		String password = login.getPassword();
		// System.out.println("Android password : " + password);
		String imeiNo = login.getImeiNo();

		if (imeiNo == null || imeiNo.trim().equals("")) {
			response.setResponseMsg("IMEI number of device is not found.");
			return response;
		}

		AuthenticationEntity existInstance = authRepo.getAuthInstance(loginId);
		boolean authcheck = authRepo.checkAuthInstanceExists(loginId);
		if (authcheck) {
			authRepo.delete(existInstance);
		}

		try {

			String status = otpRepo.appVersionStatus(appVersion);
			boolean deviceFlag = deviceRepo.deviceDataExistOrNot(loginId);
			UserDetails userDetails = repo.getByLoginId(loginId);
			if (status != null && status.equals("Active") && !status.equals("null")) {

				if (userDetails == null) {

					response.setResponseMsg("Invalid Credentials!");
					response.setResponseCode(203);
					response.setResponseDesc("Invalid");
					return response;
				}

				if (userDetails.getLoginAttempt() > (short) 4) {
					Date updatedDate = userDetails.getUpdatedOn();
					Instant updatedInstant = updatedDate.toInstant();
					Instant currentInstant = currentTimestamp.toInstant();
					long resultTime = ChronoUnit.MINUTES.between(updatedInstant, currentInstant);

					if ((15 - resultTime) <= 0) {
						userDetails.setLoginAttempt((short) 0);
						repo.save(userDetails);
					} else {

						response.setResponseMsg(
								"You have exceeded the maximum number of login attempts. Please try again after "
										+ (15 - resultTime) + " minutes.");
						response.setResponseCode(203);
						response.setResponseDesc("Invalid");
						userDetails.setLoginAttempt((short) (userDetails.getLoginAttempt() + 1));
						repo.save(userDetails);
						return response;
					}
				}

				String passString = authRepo.getPassString(imeiNo);
				String decString = enc.decrypt(passString);
				String existPassword = repo.getExistingPassword(loginId);
				String conpass = existPassword.concat(decString);
				String hashpass = enc.hash(conpass);
				// System.out.println("decString : " + decString);
				System.out.println("password : " + hashpass);

				if (password.equals(hashpass)) {
					AuthenticationEntity authinstance = authRepo.getAuthenticationInstance(imeiNo);
					String authtoken = authRepo.getAuthenticationToken(imeiNo);
					if (deviceFlag) {

						UserDeviceInfo deviceInst = deviceRepo.getDeviceinfoInstance(loginId);
						deviceInst.setImeiNo(login.getImeiNo());
						deviceInst.setStatus("Active");
						deviceInst.setDeviceName(login.getDeviceName());
						deviceInst.setUpdatedBy(userDetails.getUserName());
						deviceInst.setUpdatedOn(currentTimestamp);
						deviceRepo.save(deviceInst);

						userDetails.setLoginStatus("logged");
						userDetails.setLoginAttempt((short) 0);
						userDetails.setFcmToken(login.getFcmToken());
						repo.save(userDetails);

						response.setAppCode(authtoken);
						response.setResponseMsg("User login sucessfully");
						response.setResponseCode(HttpStatus.OK.value());
						response.setResponseDesc(HttpStatus.OK.name());
					} else {
						UserDeviceInfo device = new UserDeviceInfo();

						device.setLoginId(login.getLoginId());
						device.setImeiNo(login.getImeiNo());
						device.setStatus("Active");

						if (login.getDeviceName() != null && login.getDeviceName() != "") {
							device.setDeviceName(login.getDeviceName());
						}

						device.setCreatedBy(userDetails.getUserName());
						device.setCreatedOn(currentTimestamp);
						device.setUpdatedBy(userDetails.getUserName());
						device.setUpdatedOn(currentTimestamp);

						deviceRepo.save(device);

						userDetails.setLoginStatus("logged");
						userDetails.setLoginAttempt((short) 0);
						userDetails.setFcmToken(login.getFcmToken());
						repo.save(userDetails);

						response.setAppCode(authtoken);
						response.setResponseMsg("User login sucessfully");
						response.setResponseCode(HttpStatus.OK.value());
						response.setResponseDesc(HttpStatus.OK.name());

					}

					authinstance.setUpdatedOn(currentTimestamp);
					authinstance.setLoginId(loginId);
					authRepo.save(authinstance);

				} else {
					if (userDetails.getLoginAttempt() > (short) 3) {

						userDetails.setLoginAttempt((short) (userDetails.getLoginAttempt() + 1));
						userDetails.setUpdatedOn(currentTimestamp);
						repo.save(userDetails);

						response.setResponseMsg(
								"You have exceeded the maximum number of login attempts. Please try again in 15 minutes.");
						response.setResponseCode(203);
						response.setResponseDesc("Invalid");

					} else {

						userDetails.setLoginAttempt((short) (userDetails.getLoginAttempt() + 1));
						userDetails.setUpdatedOn(currentTimestamp);
						repo.save(userDetails);

						response.setResponseMsg(
								"Invalid! You have " + (5 - userDetails.getLoginAttempt()) + " attempts left.");
						response.setResponseCode(203);
						response.setResponseDesc("Invalid");

					}

				}

			} else {
				response.setResponseCode(301);
				response.setResponseDesc("Please upgread your app first.");
				response.setResponseMsg("Outdated app version.");
			}

		} catch (UncheckedIOException e) {
			throw new UncheckedIOException(e.getCause().getMessage(), e.getCause());
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

	public Response<Map<String, Object>> getUserDetails(UserDeviceBean user, HttpServletRequest req,
			HttpServletResponse res) {
		Response<Map<String, Object>> response = new Response<>();
		List<Map<String, Object>> wrappedList = new ArrayList<>();
		Timestamp currentTimestamp = Timestamp.from(Instant.now());
		List<Object[]> userProfileList = null;
		String loginid = user.getLoginId();

		AuthenticationEntity authinstance = authRepo.getAuthInstance(loginid);
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
		boolean authFlag = authRepo.getTokenValidation(loginid, authToken);

		try {
			if (authFlag) {
				String appVersion = user.getAppVersion();
				String status = otpRepo.appVersionStatus(appVersion);

				if (status != null && status.equals("Active") && !status.equals("null")) {

					userProfileList = repo.getUserProfileList(loginid);

					if (userProfileList != null && userProfileList.size() != 0) {
						wrappedList = userProfileList.stream().map(p -> {
							String imagePath = null;
							String filePath;
							Map<String, Object> map = new HashMap<String, Object>();
							String adhaarNo = (String) p[0];
							String emailId = (String) p[5];
							String gender = (String) p[6];
							String login = (String) p[8];
							String mobileNo = (String) p[9];
							String userName = (String) p[10];
							String careOf = (String) p[7];

							String location = p[13] != null ? p[13].toString() : "";
							String vill = p[14] != null ? p[14].toString() : "";
							String po = p[11] != null ? p[11].toString() : "";
							String block = p[2] != null ? p[2].toString() : "";
							String district = p[4] != null ? p[4].toString() : "";
							String state = p[12] != null ? p[12].toString() : "";
							String pin = p[15] != null ? p[15].toString() : "";

							String conCareOf = (careOf != null ? careOf : "").concat(", ");
							String conLocation = location.isEmpty() ? "" : location + ", ";
							String conVill = vill.isEmpty() ? "" : vill + ", ";
							String conPo = po.isEmpty() ? "" : po + ", ";
							String conBlock = block.isEmpty() ? "" : block + ", ";
							String conDistrict = district.isEmpty() ? "" : district + ", ";
							String conState = state.isEmpty() ? "" : state + ", ";
							String conPin = pin.isEmpty() ? "" : pin;
							String regStateCode = (String) p[16];
							String regState = (String) p[17];
							String encBlock = "N/A";
							String encDistrict = "N/A";
							String encEmailId = "N/A";
							String encCareOf = "N/A";
							String encLocation = "N/A";

							String checkIn = p[19] != null ? (String) p[19] : "00:00:00";
							String checkOut = p[20] != null ? (String) p[20] : "00:00:00";

							String[] checkinparts = checkIn.split(" ");
							String checkIntimePart = "";

							if (checkinparts.length >= 2) {
								checkIntimePart = checkinparts[1]; // the second part is the time
							} else {
								checkIntimePart = "00:00:00";
							}

							String[] checkoutparts = checkOut.split(" ");
							String checkouttimePart = "";

							if (checkoutparts.length >= 2) {
								checkouttimePart = checkoutparts[1]; // the second part is the time
							} else {
								checkouttimePart = "00:00:00";
							}

							try {

								if (block != null && !block.trim().isEmpty()) {
									encBlock = enc.encrypt(block);
								}

								if (district != null && !district.trim().isEmpty()) {
									encDistrict = enc.encrypt(district);
								}
								if (emailId != null && !emailId.trim().isEmpty()) {
									encEmailId = enc.encrypt(emailId);
								}

								if (careOf != null && !careOf.trim().isEmpty()) {
									encCareOf = enc.encrypt(careOf);
								}

								if (location != null && !location.trim().isEmpty()) {
									encLocation = enc.encrypt(location);
								}

								Date sqlDate = (Date) p[3];
								SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
								String dateOfBirth = sdf.format(sqlDate);

								long attachmentId = (Long) p[18];
								String photoPath = MyConstants.UPLOAD_ADHAAR + state;
								String adhaarImage = (String) p[1];
								filePath = photoPath + File.separator + attachmentId + adhaarImage;
								imagePath = MyConstants.getImageAsBase64(filePath);

								map.put("imagePath", imagePath);
								map.put("blockName", encBlock);
								map.put("aadharNo", enc.encrypt(adhaarNo));
								map.put("dateOfBirth", enc.encrypt(dateOfBirth));
								map.put("districtName", encDistrict);
								map.put("emailId", encEmailId);
								map.put("gender", enc.encrypt(gender));
								map.put("careOf", encCareOf);
								map.put("loginId", enc.encrypt(login));
								map.put("mobileNo", enc.encrypt(mobileNo));
								map.put("userName", enc.encrypt(userName));
								map.put("postOffice", enc.encrypt(po));
								map.put("statename", enc.encrypt(state));
								map.put("street", encLocation);
								map.put("village", enc.encrypt(vill));
								map.put("pinCode", enc.encrypt(pin));

								map.put("regStateCode", regStateCode);
								map.put("regState", regState);

								String address = conCareOf + conLocation + conVill + conPo + conBlock + conDistrict
										+ conState + conPin + ", India";
								map.put("comAddress", enc.encrypt(address));
								map.put("checkIn", checkIntimePart);
								map.put("checkOut", checkouttimePart);
								map.put("totalHours", p[21] != null ? p[21] : "00:00:00");
								map.put("attendanceFlag", p[22] != null ? p[22] : "checkin");
								map.put("workplaceId", p[23]);
								map.put("workplaceName", p[24]);
								map.put("employerId", p[25]);
								map.put("employerName", p[26]);
								map.put("latitute", p[27]);
								map.put("longitute", p[28]);
								map.put("radius", p[29]);
								map.put("batchId", p[30]);

							} catch (Exception e) {
								e.printStackTrace();
							}
							return map;

						})

								.collect(Collectors.toList());

						response.setWrappedList(wrappedList);
						response.setResponseCode(HttpStatus.OK.value());
						response.setResponseDesc(HttpStatus.OK.name());
						response.setResponseMsg("");
					} else {
						response.setWrappedList(null);
						response.setResponseCode(202);
						response.setResponseDesc("User Id not valid");
						response.setResponseMsg("User details not found.");
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
		secure.setSecurityHeaders(res);
		return response;
	}

	@Transactional(isolation = Isolation.SERIALIZABLE)
	public Response<Map<String, Object>> insertCandidateDetails(UserDetailsBean user, HttpServletRequest req,
			HttpServletResponse res) {
		Response<Map<String, Object>> response = new Response<>();
		String loginid = user.getLoginId();
		Validator validate = new Validator();

		Timestamp currentTimestamp = Timestamp.from(Instant.now());
		AuthenticationEntity authinstance = authRepo.getAuthInstance(loginid);
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

		boolean authFlag = authRepo.getTokenValidation(loginid, authToken);

		try {
			if (authFlag) {
				String appVersion = user.getAppVersion();
				String status = otpRepo.appVersionStatus(appVersion);
				String stateName = profileRepo.getUserState(loginid);
				if (status != null && status.equals("Active") && !status.equals("null")) {
					String existingImeiNo = deviceRepo.getExistingImeiNo(loginid);
					if (existingImeiNo.equals(user.getImeiNo())) {
						UserAddressDetails addressInst = addressRepo.getInstanceByLogin(loginid);
						UserAdhaarDetails adhaarInst = adhaarRepo.getAdhaarInstanceByLogin(loginid);
						UserProfileStatus statusInst = statusRepo.getStatusInstanceByLogin(loginid);
						UserProfile profile = profileRepo.getprofileInstance(loginid);
						CandidateAttachment attachement = attachRepo.getAttachmentByLoginId(loginid);
						CandidateAttachment attachInstance = new CandidateAttachment();

						/* ==========To Insert personal information ============= */
						int sectionCount = user.getSectionCount();

						if (sectionCount == 1) {
							if (user.getGuardianName() != null && user.getGuardianName() != ""
									&& !(user.getGuardianName()).equals("N/A")) {
								adhaarInst.setGuardianName(user.getGuardianName());
							} else {
								response.setResponseCode(306);
								response.setResponseDesc("Please enter guardian name.");
								return response;
							}

							if (user.getMotherName() != null && user.getMotherName() != ""
									&& !(user.getMotherName()).equals("N/A")) {
								adhaarInst.setMotherName(user.getMotherName());
							}

							if (user.getGuardianMobileNo() != null && user.getGuardianMobileNo() != ""
									&& !(user.getGuardianMobileNo()).equals("N/A")) {
								adhaarInst.setGuardianMobileNo(user.getGuardianMobileNo());
							} else {
								response.setResponseCode(306);
								response.setResponseDesc("Please enter guardian mobile number.");
								return response;
							}

							if (user.getAnnualFamilyIncome() != 0) {
								profile.setAnnualFamilyIncome(user.getAnnualFamilyIncome());
							}

							if (user.getVoterId() != null && user.getVoterId() != ""
									&& !(user.getVoterId()).equals("N/A")) {
								if (validate.isVoterCardValid(user.getVoterId())) {
									profile.setVoterId(user.getVoterId());
								} else {
									response.setResponseCode(306);
									response.setResponseDesc("Please enter valid Voter Id.");
									return response;
								}

							}

							/* ==============Voter ID Upload=============== */
							String voterImage = user.getVoterIdCard();

							if (voterImage != null && voterImage != "" && !(voterImage).equals("N/A")) {
								if (attachement.getVoterIdCard() != null) {

									String fileExtension = null;

									byte[] voterBytes = Base64.getDecoder().decode(voterImage);
									String fileType = MyConstants.detectFileType(voterBytes);

									if (fileType == "4044") {
										response.setResponseCode(210);
										response.setResponseDesc("File format not supported");
										return response;
									}
									MultipartFile voterFile = MyConstants.convertToMultipartFile(voterBytes, fileType);
									fileExtension = MyConstants.getFileExtension(voterFile.getOriginalFilename());
									if (voterImage != null && voterImage != "") {
										File uploadsDir = new File(MyConstants.UPLOAD_VOTER + stateName);
										if (!uploadsDir.exists()) {
											uploadsDir.mkdir();
										}
										attachInstance.setVoterIdCard("_voter_card." + fileExtension);
										attachInstance.setLoginId(loginid);
										attachInstance.setUserProfileId(profile.getUserProfileId());
										attachInstance.setUpdatedBy(loginid);
										attachInstance.setUpdatedOn(currentTimestamp);
										CandidateAttachment attachmentObj = (CandidateAttachment) attachRepo
												.save(attachInstance);

										voterFile.transferTo(new File(uploadsDir + File.separator
												+ attachmentObj.getAttachmentId() + "_voter_card." + fileExtension));
									}
								} else {
									String fileExtension = null;

									byte[] voterBytes = Base64.getDecoder().decode(voterImage);
									String fileType = MyConstants.detectFileType(voterBytes);
									if (fileType == "4044") {
										response.setResponseCode(210);
										response.setResponseDesc("File format not supported");
										return response;
									}
									MultipartFile voterFile = MyConstants.convertToMultipartFile(voterBytes, fileType);
									fileExtension = MyConstants.getFileExtension(voterFile.getOriginalFilename());
									if (voterImage != null && voterImage != "") {
										File uploadsDir = new File(MyConstants.UPLOAD_VOTER + stateName);
										if (!uploadsDir.exists()) {
											uploadsDir.mkdir();
										}
										attachement.setVoterIdCard("_voter_card." + fileExtension);
										attachRepo.save(attachement);

										voterFile.transferTo(new File(uploadsDir + File.separator
												+ attachement.getAttachmentId() + "_voter_card." + fileExtension));
									}
								}
							}

							if (user.getDlNo() != null && user.getDlNo() != "" && !(user.getDlNo()).equals("N/A")) {
								if (validate.isDrivingLicenseValid(user.getDlNo())) {
									profile.setDlNo(user.getDlNo());
								} else {
									response.setResponseCode(306);
									response.setResponseDesc("Please enter valid driving license number.");
									return response;
								}

							}

							/* =============Driving License upload================ */
							String dlImage = user.getDlCard();
							if (dlImage != null && dlImage != "" && !(dlImage.equals("N/A"))) {
								if (attachement.getDlCard() != null) {
									String fileExtension = null;
									byte[] dlBytes = Base64.getDecoder().decode(dlImage);
									String fileType = MyConstants.detectFileType(dlBytes);
									if (fileType == "4044") {
										response.setResponseCode(210);
										response.setResponseDesc("File format not supported");
										return response;
									}
									MultipartFile dlFile = MyConstants.convertToMultipartFile(dlBytes, fileType);
									fileExtension = MyConstants.getFileExtension(dlFile.getOriginalFilename());

									if (dlFile != null) {
										File uploadsDir = new File(MyConstants.UPLOAD_DL + stateName);
										if (!uploadsDir.exists()) {
											uploadsDir.mkdir();
										}
										attachInstance.setDlCard("_dl." + fileExtension);
										attachInstance.setLoginId(loginid);
										attachInstance.setUserProfileId(profile.getUserProfileId());
										attachInstance.setUpdatedBy(loginid);
										attachInstance.setUpdatedOn(currentTimestamp);
										CandidateAttachment attachmentObj = (CandidateAttachment) attachRepo
												.save(attachInstance);

										dlFile.transferTo(new File(uploadsDir + File.separator
												+ attachmentObj.getAttachmentId() + "_dl." + fileExtension));

									}

								} else {
									String fileExtension = null;
									byte[] dlBytes = Base64.getDecoder().decode(dlImage);
									String fileType = MyConstants.detectFileType(dlBytes);
									if (fileType == "4044") {
										response.setResponseCode(210);
										response.setResponseDesc("File format not supported");
										return response;
									}
									MultipartFile dlFile = MyConstants.convertToMultipartFile(dlBytes, fileType);
									fileExtension = MyConstants.getFileExtension(dlFile.getOriginalFilename());

									if (dlFile != null) {
										File uploadsDir = new File(MyConstants.UPLOAD_DL + stateName);
										if (!uploadsDir.exists()) {
											uploadsDir.mkdir();
										}

										attachement.setDlCard("_dl." + fileExtension);
										attachRepo.save(attachement);

										dlFile.transferTo(new File(uploadsDir + File.separator
												+ attachement.getAttachmentId() + "_dl." + fileExtension));
									}

								}
							}

							/* =============Cast Category Certificate upload================ */

							if (user.getCastCategory() != null && user.getCastCategory() != ""
									&& !(user.getCastCategory()).equals("N/A")) {
								profile.setCastCategory(user.getCastCategory());
							}

							if ((user.getCastCategory()).equals("SC") || (user.getCastCategory()).equals("ST")
									|| (user.getCastCategory()).equals("OBC")) {

								String castImage = user.getCategoryCertificate();
								if (castImage != null && castImage != "" && !castImage.equals("N/A")) {
									if (attachement.getCategoryCert() != null) {
										String fileExtension = null;
										byte[] castBytes = Base64.getDecoder().decode(castImage);
										String fileType = MyConstants.detectFileType(castBytes);
										if (fileType == "4044") {
											response.setResponseCode(210);
											response.setResponseDesc("File format not supported");
											return response;
										}
										MultipartFile castFile = MyConstants.convertToMultipartFile(castBytes,
												fileType);
										fileExtension = MyConstants.getFileExtension(castFile.getOriginalFilename());

										if (castFile != null) {
											File uploadsDir = new File(MyConstants.UPLOAD_CAST + stateName);
											if (!uploadsDir.exists()) {
												uploadsDir.mkdir();
											}

											attachInstance.setCategoryCert("_cast_category." + fileExtension);
											attachInstance.setLoginId(loginid);
											attachInstance.setUserProfileId(profile.getUserProfileId());
											attachInstance.setUpdatedBy(loginid);
											attachInstance.setUpdatedOn(currentTimestamp);
											CandidateAttachment attachmentObj = (CandidateAttachment) attachRepo
													.save(attachInstance);

											castFile.transferTo(new File(
													uploadsDir + File.separator + attachmentObj.getAttachmentId()
															+ "_cast_category." + fileExtension));

										}
									} else {
										String fileExtension = null;
										byte[] castBytes = Base64.getDecoder().decode(castImage);
										String fileType = MyConstants.detectFileType(castBytes);
										if (fileType == "4044") {
											response.setResponseCode(210);
											response.setResponseDesc("File format not supported");
											return response;
										}
										MultipartFile castFile = MyConstants.convertToMultipartFile(castBytes,
												fileType);
										fileExtension = MyConstants.getFileExtension(castFile.getOriginalFilename());

										if (castFile != null) {
											File uploadsDir = new File(MyConstants.UPLOAD_CAST + stateName);
											if (!uploadsDir.exists()) {
												uploadsDir.mkdir();
											}

											attachement.setCategoryCert("_cast_category." + fileExtension);
											attachRepo.save(attachement);

											castFile.transferTo(
													new File(uploadsDir + File.separator + attachement.getAttachmentId()
															+ "_cast_category." + fileExtension));

										}
									}
								} else {
									response.setResponseCode(306);
									response.setResponseDesc("Please upload category certificate.");
									return response;
								}
							}

							if (user.getMaritalStatus() != null && user.getMaritalStatus() != ""
									&& !(user.getMaritalStatus()).equals("N/A")) {
								profile.setMaritalStatus(user.getMaritalStatus());
							} else {
								response.setResponseCode(306);
								response.setResponseDesc("Please select your marital status.");
								return response;
							}

							/* =============Minority Certificate upload================ */

							if (user.getIsMinority() != null && user.getIsMinority() != ""
									&& !(user.getIsMinority()).equals("N/A")) {
								profile.setIsMinority(user.getIsMinority());
							}

							if ((user.getIsMinority()).equals("Yes")) {

								String minorImage = user.getMinorityCert();
								if (minorImage != null && minorImage != "" && !minorImage.equals("N/A")) {
									if (attachement.getMinorityCert() != null) {
										String fileExtension = null;
										byte[] minorBytes = Base64.getDecoder().decode(minorImage);
										String fileType = MyConstants.detectFileType(minorBytes);
										if (fileType == "4044") {
											response.setResponseCode(210);
											response.setResponseDesc("File format not supported");
											return response;
										}
										MultipartFile minorFile = MyConstants.convertToMultipartFile(minorBytes,
												fileType);
										fileExtension = MyConstants.getFileExtension(minorFile.getOriginalFilename());

										if (minorFile != null) {
											File uploadsDir = new File(MyConstants.UPLOAD_MINORITY + stateName);
											if (!uploadsDir.exists()) {
												uploadsDir.mkdir();
											}

											attachInstance.setMinorityCert("_minority." + fileExtension);
											attachInstance.setLoginId(loginid);
											attachInstance.setUserProfileId(profile.getUserProfileId());
											attachInstance.setUpdatedBy(loginid);
											attachInstance.setUpdatedOn(currentTimestamp);
											CandidateAttachment attachmentObj = (CandidateAttachment) attachRepo
													.save(attachInstance);
											minorFile.transferTo(new File(uploadsDir + File.separator
													+ attachmentObj.getAttachmentId() + "_minority." + fileExtension));
										}
									} else {
										String fileExtension = null;
										byte[] minorBytes = Base64.getDecoder().decode(minorImage);
										String fileType = MyConstants.detectFileType(minorBytes);
										if (fileType == "4044") {
											response.setResponseCode(210);
											response.setResponseDesc("File format not supported");
											return response;
										}
										MultipartFile minorFile = MyConstants.convertToMultipartFile(minorBytes,
												fileType);
										fileExtension = MyConstants.getFileExtension(minorFile.getOriginalFilename());

										if (minorFile != null) {
											File uploadsDir = new File(MyConstants.UPLOAD_MINORITY + stateName);
											if (!uploadsDir.exists()) {
												uploadsDir.mkdir();
											}

											attachement.setMinorityCert("_minority." + fileExtension);
											attachRepo.save(attachement);
											minorFile.transferTo(new File(uploadsDir + File.separator
													+ attachement.getAttachmentId() + "_minority." + fileExtension));
										}

									}
								} else {
									response.setResponseCode(306);
									response.setResponseDesc("Please upload minority certificate.");
									return response;
								}
							}

							/* =============Disability Certificate upload================ */

							if (user.getIsDisability() != null && user.getIsDisability() != ""
									&& !(user.getIsDisability()).equals("N/A")) {
								profile.setIsDisability(user.getIsDisability());
							}
							if ((user.getIsDisability()).equals("Yes")) {

								String disableImage = user.getDisablityCert();
								if (disableImage != null && disableImage != "" && !disableImage.equals("N/A")) {
									if (attachement.getDisablityCert() != null) {
										String fileExtension = null;
										byte[] disableBytes = Base64.getDecoder().decode(disableImage);
										String fileType = MyConstants.detectFileType(disableBytes);
										if (fileType == "4044") {
											response.setResponseCode(210);
											response.setResponseDesc("File format not supported");
											return response;
										}
										MultipartFile disableFile = MyConstants.convertToMultipartFile(disableBytes,
												fileType);
										fileExtension = MyConstants.getFileExtension(disableFile.getOriginalFilename());

										if (disableFile != null) {
											File uploadsDir = new File(MyConstants.UPLOAD_DISABLITY + stateName);
											if (!uploadsDir.exists()) {
												uploadsDir.mkdir();
											}

											attachInstance.setDisablityCert("_disablity_cert." + fileExtension);
											attachInstance.setLoginId(loginid);
											attachInstance.setUserProfileId(profile.getUserProfileId());
											attachInstance.setUpdatedBy(loginid);
											attachInstance.setUpdatedOn(currentTimestamp);
											CandidateAttachment attachmentObj = (CandidateAttachment) attachRepo
													.save(attachInstance);
											disableFile.transferTo(new File(
													uploadsDir + File.separator + attachmentObj.getAttachmentId()
															+ "_disablity_cert." + fileExtension));
										}
									} else {
										String fileExtension = null;
										byte[] disableBytes = Base64.getDecoder().decode(disableImage);
										String fileType = MyConstants.detectFileType(disableBytes);
										if (fileType == "4044") {
											response.setResponseCode(210);
											response.setResponseDesc("File format not supported");
											return response;
										}
										MultipartFile disableFile = MyConstants.convertToMultipartFile(disableBytes,
												fileType);
										fileExtension = MyConstants.getFileExtension(disableFile.getOriginalFilename());

										if (disableFile != null) {
											File uploadsDir = new File(MyConstants.UPLOAD_DISABLITY + stateName);
											if (!uploadsDir.exists()) {
												uploadsDir.mkdir();
											}

											attachement.setDisablityCert("_disablity_cert." + fileExtension);
											attachRepo.save(attachement);

											disableFile.transferTo(
													new File(uploadsDir + File.separator + attachement.getAttachmentId()
															+ "_disablity_cert." + fileExtension));
										}

									}
								} else {
									response.setResponseCode(306);
									response.setResponseDesc("Please upload physical disablity certificate.");
									return response;
								}
							}

							/* =============NREGA card upload================ */

							if (user.getIsNrega() != null && user.getIsNrega() != ""
									&& !(user.getIsNrega()).equals("N/A")) {
								profile.setIsNrega(user.getIsNrega());
							}

							if ((user.getIsNrega()).equals("Yes")) {
								if (!(user.getNregaJobCard()).equals("N/A") && user.getNregaJobCard() != null
										&& user.getNregaJobCard() != "") {
									profile.setNregaJobCard(user.getNregaJobCard());
								} else {
									response.setResponseCode(306);
									response.setResponseDesc("Please enter NREGA job card number.");
									return response;
								}

							}

							if (user.getIsNrega().trim().equals("Yes")) {
								String nregaImage = user.getNregaCard();
								if (nregaImage != null && nregaImage != "" && !nregaImage.equals("N/A")) {
									if (attachement.getNaregaCard() != null) {
										String fileExtension = null;
										byte[] nregaBytes = Base64.getDecoder().decode(nregaImage);
										String fileType = MyConstants.detectFileType(nregaBytes);
										if (fileType == "4044") {
											response.setResponseCode(210);
											response.setResponseDesc("File format not supported");
											return response;
										}
										MultipartFile nregaFile = MyConstants.convertToMultipartFile(nregaBytes,
												fileType);
										fileExtension = MyConstants.getFileExtension(nregaFile.getOriginalFilename());

										if (nregaFile != null) {
											File uploadsDir = new File(MyConstants.UPLOAD_NREGA + stateName);
											if (!uploadsDir.exists()) {
												uploadsDir.mkdir();
											}
											attachInstance.setNaregaCard("_nrega_card." + fileExtension);
											attachInstance.setLoginId(loginid);
											attachInstance.setUserProfileId(profile.getUserProfileId());
											attachInstance.setUpdatedBy(loginid);
											attachInstance.setUpdatedOn(currentTimestamp);
											CandidateAttachment attachmentObj = (CandidateAttachment) attachRepo
													.save(attachInstance);
											nregaFile.transferTo(new File(
													uploadsDir + File.separator + attachmentObj.getAttachmentId()
															+ "_nrega_card." + fileExtension));
										}
									} else {
										String fileExtension = null;
										byte[] nregaBytes = Base64.getDecoder().decode(nregaImage);
										String fileType = MyConstants.detectFileType(nregaBytes);
										if (fileType == "4044") {
											response.setResponseCode(210);
											response.setResponseDesc("File format not supported");
											return response;
										}
										MultipartFile nregaFile = MyConstants.convertToMultipartFile(nregaBytes,
												fileType);
										fileExtension = MyConstants.getFileExtension(nregaFile.getOriginalFilename());

										if (nregaFile != null) {
											File uploadsDir = new File(MyConstants.UPLOAD_NREGA + stateName);
											if (!uploadsDir.exists()) {
												uploadsDir.mkdir();
											}
											attachement.setNaregaCard("_nrega_card." + fileExtension);
											attachRepo.save(attachement);
											nregaFile.transferTo(new File(uploadsDir + File.separator
													+ attachement.getAttachmentId() + "_nrega_card." + fileExtension));
										}
									}
								} else {
									response.setResponseCode(306);
									response.setResponseDesc("Please upload NREGA certificate.");
									return response;
								}
							}

							/* =============SHG Image upload================ */

							if (user.getIsShg() != null && user.getIsShg() != "" && !(user.getIsShg()).equals("N/A")) {
								profile.setIsShg(user.getIsShg());
							}

							if (user.getShgNo() != null && user.getShgNo() != "" && !(user.getShgNo()).equals("N/A")) {
								profile.setShgNo(user.getShgNo());
							}

							if ((user.getIsShg()).trim().equals("Yes")) {
								String shgImage = user.getShgImage();
								if (shgImage != null && shgImage != "" && !shgImage.equals("N/A")) {
									if (attachement.getShgImage() != null) {
										String fileExtension = null;
										byte[] shgImageBytes = Base64.getDecoder().decode(shgImage);
										String fileType = MyConstants.detectFileType(shgImageBytes);
										if (fileType == "4044") {
											response.setResponseCode(210);
											response.setResponseDesc("File format not supported");
											return response;
										}
										MultipartFile shgImageFile = MyConstants.convertToMultipartFile(shgImageBytes,
												fileType);
										fileExtension = MyConstants
												.getFileExtension(shgImageFile.getOriginalFilename());

										if (shgImageFile != null) {
											File uploadsDir = new File(MyConstants.UPLOAD_SHG + stateName);
											if (!uploadsDir.exists()) {
												uploadsDir.mkdir();
											}

											attachInstance.setShgImage("_shg_image." + fileExtension);
											attachInstance.setLoginId(loginid);
											attachInstance.setUserProfileId(profile.getUserProfileId());
											attachInstance.setUpdatedBy(loginid);
											attachInstance.setUpdatedOn(currentTimestamp);
											CandidateAttachment attachmentObj = (CandidateAttachment) attachRepo
													.save(attachInstance);
											shgImageFile.transferTo(new File(uploadsDir + File.separator
													+ attachmentObj.getAttachmentId() + "_shg_image." + fileExtension));
										}
									} else {
										String fileExtension = null;
										byte[] shgImageBytes = Base64.getDecoder().decode(shgImage);
										String fileType = MyConstants.detectFileType(shgImageBytes);
										if (fileType == "4044") {
											response.setResponseCode(210);
											response.setResponseDesc("File format not supported");
											return response;
										}
										MultipartFile shgImageFile = MyConstants.convertToMultipartFile(shgImageBytes,
												fileType);
										fileExtension = MyConstants
												.getFileExtension(shgImageFile.getOriginalFilename());

										if (shgImageFile != null) {
											File uploadsDir = new File(MyConstants.UPLOAD_SHG + stateName);
											if (!uploadsDir.exists()) {
												uploadsDir.mkdir();
											}

											attachement.setShgImage("_shg_image." + fileExtension);
											attachRepo.save(attachement);
											shgImageFile.transferTo(new File(uploadsDir + File.separator
													+ attachement.getAttachmentId() + "_shg_image." + fileExtension));
										}

									}
								} else {
									response.setResponseCode(306);
									response.setResponseDesc("Please upload SHG image.");
									return response;
								}
							}

							/* =============Ration card upload================ */

							if (user.getAntyodaya() != null && user.getAntyodaya() != ""
									&& !(user.getAntyodaya()).equals("N/A")) {
								profile.setAntyodaya(user.getAntyodaya());
							}

							if ((user.getAntyodaya()).equals("Yes")) {
								String rationImage = user.getRationCard();
								if (rationImage != null && rationImage != "" && !rationImage.equals("N/A")) {
									if (attachement.getRationCard() != null) {
										String fileExtension = null;
										byte[] rationBytes = Base64.getDecoder().decode(rationImage);
										String fileType = MyConstants.detectFileType(rationBytes);
										if (fileType == "4044") {
											response.setResponseCode(210);
											response.setResponseDesc("File format not supported");
											return response;
										}
										MultipartFile rationFile = MyConstants.convertToMultipartFile(rationBytes,
												fileType);
										fileExtension = MyConstants.getFileExtension(rationFile.getOriginalFilename());

										if (rationFile != null) {
											File uploadsDir = new File(MyConstants.UPLOAD_RASAN + stateName);
											if (!uploadsDir.exists()) {
												uploadsDir.mkdir();
											}

											attachInstance.setRationCard("_ration_card." + fileExtension);
											attachInstance.setLoginId(loginid);
											attachInstance.setUserProfileId(profile.getUserProfileId());
											attachInstance.setUpdatedBy(loginid);
											attachInstance.setUpdatedOn(currentTimestamp);
											CandidateAttachment attachmentObj = (CandidateAttachment) attachRepo
													.save(attachInstance);
											rationFile.transferTo(new File(
													uploadsDir + File.separator + attachmentObj.getAttachmentId()
															+ "_ration_card." + fileExtension));
										}
									} else {
										String fileExtension = null;
										byte[] rationBytes = Base64.getDecoder().decode(rationImage);
										String fileType = MyConstants.detectFileType(rationBytes);
										if (fileType == "4044") {
											response.setResponseCode(210);
											response.setResponseDesc("File format not supported");
											return response;
										}
										MultipartFile rationFile = MyConstants.convertToMultipartFile(rationBytes,
												fileType);
										fileExtension = MyConstants.getFileExtension(rationFile.getOriginalFilename());

										if (rationFile != null) {
											File uploadsDir = new File(MyConstants.UPLOAD_RASAN + stateName);
											if (!uploadsDir.exists()) {
												uploadsDir.mkdir();
											}

											attachement.setRationCard("_ration_card." + fileExtension);
											attachRepo.save(attachement);
											rationFile.transferTo(new File(uploadsDir + File.separator
													+ attachement.getAttachmentId() + "_ration_card." + fileExtension));
										}
									}
								} else {
									response.setResponseCode(306);
									response.setResponseDesc("Please upload antyodaya card.");
									return response;
								}
							}

							/* =============RSBY card upload================ */

							if (user.getIsRsby() != null && user.getIsRsby() != ""
									&& !(user.getIsRsby()).equals("N/A")) {
								profile.setIsRsby(user.getIsRsby());
							}

							if ((user.getIsRsby()).equals("Yes")) {
								String rsbyImage = user.getRsbyCard();
								if (rsbyImage != null && rsbyImage != "" && !rsbyImage.equals("N/A")) {
									if (attachement.getRsbyCard() != null) {
										String fileExtension = null;
										byte[] rsbyBytes = Base64.getDecoder().decode(rsbyImage);
										String fileType = MyConstants.detectFileType(rsbyBytes);
										if (fileType == "4044") {
											response.setResponseCode(210);
											response.setResponseDesc("File format not supported");
											return response;
										}
										MultipartFile rsbyFile = MyConstants.convertToMultipartFile(rsbyBytes,
												fileType);
										fileExtension = MyConstants.getFileExtension(rsbyFile.getOriginalFilename());

										if (rsbyFile != null) {
											File uploadsDir = new File(MyConstants.UPLOAD_RSBY + stateName);
											if (!uploadsDir.exists()) {
												uploadsDir.mkdir();
											}

											attachInstance.setRsbyCard("_rsby_card." + fileExtension);
											attachInstance.setLoginId(loginid);
											attachInstance.setUserProfileId(profile.getUserProfileId());
											attachInstance.setUpdatedBy(loginid);
											attachInstance.setUpdatedOn(currentTimestamp);
											CandidateAttachment attachmentObj = (CandidateAttachment) attachRepo
													.save(attachInstance);
											rsbyFile.transferTo(new File(uploadsDir + File.separator
													+ attachmentObj.getAttachmentId() + "_rsby_card." + fileExtension));
										}
									} else {
										String fileExtension = null;
										byte[] rsbyBytes = Base64.getDecoder().decode(rsbyImage);
										String fileType = MyConstants.detectFileType(rsbyBytes);
										if (fileType == "4044") {
											response.setResponseCode(210);
											response.setResponseDesc("File format not supported");
											return response;
										}
										MultipartFile rsbyFile = MyConstants.convertToMultipartFile(rsbyBytes,
												fileType);
										fileExtension = MyConstants.getFileExtension(rsbyFile.getOriginalFilename());

										if (rsbyFile != null) {
											File uploadsDir = new File(MyConstants.UPLOAD_RSBY + stateName);
											if (!uploadsDir.exists()) {
												uploadsDir.mkdir();
											}

											attachement.setRsbyCard("_rsby_card." + fileExtension);
											attachRepo.save(attachement);
											rsbyFile.transferTo(new File(uploadsDir + File.separator
													+ attachement.getAttachmentId() + "_rsby_card." + fileExtension));
										}

									}
								} else {
									response.setResponseCode(306);
									response.setResponseDesc("Please upload RSBY/PM-JAY card.");
									return response;
								}
							}

							/* =============PIP certificate upload================ */

							if (user.getIsPip() != null && user.getIsPip() != "" && !(user.getIsPip()).equals("N/A")) {
								profile.setIsPip(user.getIsPip());
							}

							if ((user.getIsPip()).trim().equals("Yes")) {
								String pipcertificate = user.getPipCert();
								if (pipcertificate != null && pipcertificate != "" && !pipcertificate.equals("N/A")) {
									if (attachement.getPipCert() != null) {
										String fileExtension = null;
										byte[] pipBytes = Base64.getDecoder().decode(pipcertificate);
										String fileType = MyConstants.detectFileType(pipBytes);
										if (fileType == "4044") {
											response.setResponseCode(210);
											response.setResponseDesc("File format not supported");
											return response;
										}
										MultipartFile pipFile = MyConstants.convertToMultipartFile(pipBytes, fileType);
										fileExtension = MyConstants.getFileExtension(pipFile.getOriginalFilename());

										if (pipFile != null) {
											File uploadsDir = new File(MyConstants.UPLOAD_PIP + stateName);
											if (!uploadsDir.exists()) {
												uploadsDir.mkdir();
											}

											attachInstance.setPipCert("_pip_cert." + fileExtension);
											attachInstance.setLoginId(loginid);
											attachInstance.setUserProfileId(profile.getUserProfileId());
											attachInstance.setUpdatedBy(loginid);
											attachInstance.setUpdatedOn(currentTimestamp);
											CandidateAttachment attachmentObj = (CandidateAttachment) attachRepo
													.save(attachInstance);
											pipFile.transferTo(new File(uploadsDir + File.separator
													+ attachmentObj.getAttachmentId() + "_pip_cert." + fileExtension));
										}
									} else {
										String fileExtension = null;
										byte[] pipBytes = Base64.getDecoder().decode(pipcertificate);
										String fileType = MyConstants.detectFileType(pipBytes);
										if (fileType == "4044") {
											response.setResponseCode(210);
											response.setResponseDesc("File format not supported");
											return response;
										}
										MultipartFile pipFile = MyConstants.convertToMultipartFile(pipBytes, fileType);
										fileExtension = MyConstants.getFileExtension(pipFile.getOriginalFilename());

										if (pipFile != null) {
											File uploadsDir = new File(MyConstants.UPLOAD_PIP + stateName);
											if (!uploadsDir.exists()) {
												uploadsDir.mkdir();
											}

											attachement.setPipCert("_pip_cert." + fileExtension);
											attachRepo.save(attachement);
											pipFile.transferTo(new File(uploadsDir + File.separator
													+ attachement.getAttachmentId() + "_pip_cert." + fileExtension));
										}

									}
								} else {
									response.setResponseCode(306);
									response.setResponseDesc("Please upload PIP certificate.");
									return response;
								}
							}

							if (user.getIsShg().trim().equals("Yes") || user.getAntyodaya().trim().equals("Yes")
									|| user.getIsNrega().trim().equals("Yes") || user.getIsPip().trim().equals("Yes")
									|| user.getIsPmayg().trim().equals("Yes")
									|| user.getIsRsby().trim().equals("Yes")) {
								profile.setPovertyStatus("RP");
							} else {
								profile.setPovertyStatus("NRP");
							}

							/* =============PMAY-G certificate upload================ */

							if (user.getIsPmayg() != null || !user.getIsPmayg().trim().isEmpty()
									|| !(user.getIsPmayg()).trim().equals("N/A")) {
								profile.setIsPmayg(user.getIsPmayg());
							}

							if (user.getIsPmayg().trim().equals("Yes")) {
								String pmaygCert = user.getPmaygAttachment();
								if (pmaygCert != null && pmaygCert != "" && !pmaygCert.equals("N/A")) {
									if (attachement.getPmaygAttachment() != null) {
										String fileExtension = null;
										byte[] pmaygBytes = Base64.getDecoder().decode(pmaygCert);
										String fileType = MyConstants.detectFileType(pmaygBytes);
										if (fileType == "4044") {
											response.setResponseCode(210);
											response.setResponseDesc("File format not supported");
											return response;
										}
										MultipartFile pmaygFile = MyConstants.convertToMultipartFile(pmaygBytes,
												fileType);
										fileExtension = MyConstants.getFileExtension(pmaygFile.getOriginalFilename());

										if (pmaygFile != null) {
											File uploadsDir = new File(MyConstants.UPLOAD_PMAYG + stateName);
											if (!uploadsDir.exists()) {
												uploadsDir.mkdir();
											}

											attachInstance.setPmaygAttachment("_pmayg_cert." + fileExtension);
											attachInstance.setLoginId(loginid);
											attachInstance.setUserProfileId(profile.getUserProfileId());
											attachInstance.setUpdatedBy(loginid);
											attachInstance.setUpdatedOn(currentTimestamp);
											CandidateAttachment attachmentObj = (CandidateAttachment) attachRepo
													.save(attachInstance);
											pmaygFile.transferTo(new File(
													uploadsDir + File.separator + attachmentObj.getAttachmentId()
															+ "_pmayg_cert." + fileExtension));
										}
									} else {
										String fileExtension = null;
										byte[] pmaygBytes = Base64.getDecoder().decode(pmaygCert);
										String fileType = MyConstants.detectFileType(pmaygBytes);
										if (fileType == "4044") {
											response.setResponseCode(210);
											response.setResponseDesc("File format not supported");
											return response;
										}
										MultipartFile pmaygFile = MyConstants.convertToMultipartFile(pmaygBytes,
												fileType);
										fileExtension = MyConstants.getFileExtension(pmaygFile.getOriginalFilename());

										if (pmaygFile != null) {
											File uploadsDir = new File(MyConstants.UPLOAD_PMAYG + stateName);
											if (!uploadsDir.exists()) {
												uploadsDir.mkdir();
											}

											attachement.setPmaygAttachment("_pmayg_cert." + fileExtension);
											attachRepo.save(attachement);
											pmaygFile.transferTo(new File(uploadsDir + File.separator
													+ attachement.getAttachmentId() + "_pmayg_cert." + fileExtension));
										}

									}
								} else {
									response.setResponseCode(306);
									response.setResponseDesc("Please upload pmayg certificate.");
									return response;
								}
							}

							statusInst.setUpdatedOn(currentTimestamp);
							statusInst.setPersonalStatus((short) 1);
							profileRepo.save(profile);
							adhaarRepo.save(adhaarInst);
							statusRepo.save(statusInst);

							response.setResponseMsg("Personal information saved sucessfully");
							response.setResponseCode(HttpStatus.OK.value());
							response.setResponseDesc(HttpStatus.OK.name());
							return response;

							/* ======== To insert Address details ======== */
						} else if (sectionCount == 2) {

							if (adhaarInst.isInBatch()) {
								response.setResponseCode(403);
								response.setResponseDesc(
										"Once a candidate is enrolled in a batch, they cannot change their address.");
								return response;
							}

							if (user.getPrLocality() != null && !user.getPrLocality().trim().isEmpty()
									&& !user.getPrLocality().trim().equals("N/A")) {
								addressInst.setPrLocality(user.getPrLocality());
							} else {
								addressInst.setPrLocality("RURAL");
							}

							if (user.getPrStateCode() != null && user.getPrStateCode() != ""
									&& !(user.getPrStateCode()).equals("N/A")) {
								addressInst.setPrStateCode(user.getPrStateCode());
								adhaarInst.setStateCode(user.getPrStateCode());
							} else {
								response.setResponseCode(306);
								response.setResponseDesc("Please select permanent state.");
								return response;
							}

							if (user.getPrDistrictCode() != null && user.getPrDistrictCode() != ""
									&& !(user.getPrDistrictCode()).equals("N/A")) {
								addressInst.setPrDistrictCode(user.getPrDistrictCode());
							} else {
								response.setResponseCode(306);
								response.setResponseDesc("Please select permanent district.");
								return response;
							}

							if (user.getPrBlockCode() != null && user.getPrBlockCode() != ""
									&& !(user.getPrBlockCode()).equals("N/A")) {
								addressInst.setPrBlockCode(user.getPrBlockCode());
							}

							if (user.getPrGpCode() != null && user.getPrGpCode() != ""
									&& !(user.getPrGpCode()).equals("N/A")) {
								addressInst.setPrGpCode(user.getPrGpCode());
							}

							if (user.getPrVillageCode() != null && user.getPrVillageCode() != ""
									&& !(user.getPrVillageCode()).equals("N/A")) {
								addressInst.setPrVillageCode(user.getPrVillageCode());
							}

							if (user.getPrStreet1() != null && user.getPrStreet1() != ""
									&& !(user.getPrStreet1()).equals("N/A")) {
								addressInst.setPrStreet1(user.getPrStreet1());
							}

							if (user.getPrStreet2() != null && user.getPrStreet2() != ""
									&& !(user.getPrStreet2()).equals("N/A")) {
								addressInst.setPrStreet2(user.getPrStreet2());
							}

							if (user.getPrUlbCode() != null && !user.getPrUlbCode().trim().isEmpty()
									&& !(user.getPrUlbCode()).equals("N/A")) {
								addressInst.setPrUlbCode(user.getPrUlbCode());
							}

							if (user.getPrWardCode() != null && !user.getPrWardCode().trim().isEmpty()
									&& !(user.getPrWardCode()).equals("N/A")) {
								addressInst.setPrWardCode(user.getPrWardCode());
							}

							if (user.getPrPinCode() != null && user.getPrPinCode() != ""
									&& !(user.getPrPinCode()).equals("N/A")) {
								addressInst.setPr_pin_code(user.getPrPinCode());
							} else {
								response.setResponseCode(306);
								response.setResponseDesc("Please enter permanent pin code.");
								return response;
							}

							if (user.getIsTempAddressSame() != null && user.getIsTempAddressSame() != ""
									&& !(user.getIsTempAddressSame()).equals("N/A")) {
								addressInst.setIsTempAddressSame(user.getIsTempAddressSame());
							} else {
								response.setResponseCode(306);
								response.setResponseDesc("Please select is present address same as permanent address.");
								return response;
							}

							if (user.getTempLocality() != null && !user.getTempLocality().trim().isEmpty()
									&& !user.getTempLocality().trim().equals("N/A")) {
								addressInst.setTempLocality(user.getTempLocality());
							} else {
								addressInst.setTempLocality("RURAL");
							}

							if (user.getTempStateCode() != null && user.getTempStateCode() != ""
									&& !(user.getTempStateCode()).equals("N/A")) {
								addressInst.setTempStateCode(user.getTempStateCode());
							} else {
								response.setResponseCode(306);
								response.setResponseDesc("Please select present state.");
								return response;
							}

							if (user.getTempDistrictCode() != null && user.getTempDistrictCode() != ""
									&& !(user.getTempDistrictCode()).equals("N/A")) {
								addressInst.setTempDistrictCode(user.getTempDistrictCode());
							} else {
								response.setResponseCode(306);
								response.setResponseDesc("Please select present district.");
								return response;
							}

							if (user.getTempUlbCode() != null && !user.getTempUlbCode().trim().isEmpty()
									&& !(user.getTempUlbCode()).equals("N/A")) {
								addressInst.setTempUlbCode(user.getTempUlbCode());
							}

							if (user.getTempWardCode() != null && !user.getTempWardCode().trim().isEmpty()
									&& !(user.getTempWardCode()).equals("N/A")) {
								addressInst.setTempWardCode(user.getTempWardCode());
							}

							if (user.getTempBlockCode() != null && user.getTempBlockCode() != ""
									&& !(user.getTempBlockCode()).equals("N/A")) {
								addressInst.setTempBlockCode(user.getTempBlockCode());
							}

							if (user.getTempGpCode() != null && user.getTempGpCode() != ""
									&& !(user.getTempGpCode()).equals("N/A")) {
								addressInst.setTempGpCode(user.getTempGpCode());
							}

							if (user.getTempVillageCode() != null && user.getTempVillageCode() != ""
									&& !(user.getTempVillageCode()).equals("N/A")) {
								addressInst.setTempVillageCode(user.getTempVillageCode());
							}

							if (user.getTempStreet1() != null && user.getTempStreet1() != ""
									&& !(user.getTempStreet1()).equals("N/A")) {
								addressInst.setTempStreet1(user.getTempStreet1());
							}

							if (user.getTempStreet2() != null && user.getTempStreet2() != ""
									&& !(user.getTempStreet2()).equals("N/A")) {
								addressInst.setTempStreet2(user.getTempStreet2());
							}

							if (user.getTempPinCode() != null && user.getTempPinCode() != ""
									&& !(user.getTempPinCode()).equals("N/A")) {
								addressInst.setTempPinCode(user.getTempPinCode());
							} else {
								response.setResponseCode(306);
								response.setResponseDesc("Please select present pin code.");
								return response;
							}

							/* =============Residence certificate upload================ */
							String addressImage = user.getResidenceCert();
							if (addressImage != null && addressImage != "" && !addressImage.equals("N/A")) {
								if (attachement.getResidenceCert() != null) {
									String fileExtension = null;
									byte[] addressBytes = Base64.getDecoder().decode(addressImage);
									String fileType = MyConstants.detectFileType(addressBytes);
									if (fileType == "4044") {
										response.setResponseCode(210);
										response.setResponseDesc("File format not supported");
										return response;
									}
									MultipartFile addressFile = MyConstants.convertToMultipartFile(addressBytes,
											fileType);
									fileExtension = MyConstants.getFileExtension(addressFile.getOriginalFilename());

									if (addressFile != null) {
										File uploadsDir = new File(MyConstants.UPLOAD_RESIDENT + stateName);
										if (!uploadsDir.exists()) {
											uploadsDir.mkdir();
										}

										attachInstance.setResidenceCert("_residance_cert." + fileExtension);
										attachInstance.setLoginId(loginid);
										attachInstance.setUserProfileId(profile.getUserProfileId());
										attachInstance.setUpdatedBy(loginid);
										attachInstance.setUpdatedOn(currentTimestamp);
										CandidateAttachment attachmentObj = (CandidateAttachment) attachRepo
												.save(attachInstance);
										addressFile.transferTo(
												new File(uploadsDir + File.separator + attachmentObj.getAttachmentId()
														+ "_residance_cert." + fileExtension));
									}
								} else {
									String fileExtension = null;
									byte[] addressBytes = Base64.getDecoder().decode(addressImage);
									String fileType = MyConstants.detectFileType(addressBytes);
									if (fileType == "4044") {
										response.setResponseCode(210);
										response.setResponseDesc("File format not supported");
										return response;
									}
									MultipartFile addressFile = MyConstants.convertToMultipartFile(addressBytes,
											fileType);
									fileExtension = MyConstants.getFileExtension(addressFile.getOriginalFilename());

									if (addressFile != null) {
										File uploadsDir = new File(MyConstants.UPLOAD_RESIDENT + stateName);
										if (!uploadsDir.exists()) {
											uploadsDir.mkdir();
										}

										attachement.setResidenceCert("_residance_cert." + fileExtension);
										attachRepo.save(attachement);
										addressFile.transferTo(new File(uploadsDir + File.separator
												+ attachement.getAttachmentId() + "_residance_cert." + fileExtension));
									}

								}
							} else {
								response.setResponseCode(306);
								response.setResponseDesc("Please upload residance certificate.");
								return response;
							}

							statusInst.setUpdatedOn(currentTimestamp);
							statusInst.setAddressStatus((short) 1);
							addressInst.setChangeCount((addressInst.getChangeCount() + 1));
							addressRepo.save(addressInst);

							adhaarRepo.save(adhaarInst);
							statusRepo.save(statusInst);

							response.setResponseMsg("Address details saved sucessfully");
							response.setResponseCode(HttpStatus.OK.value());
							response.setResponseDesc(HttpStatus.OK.name());
							return response;

						} else if (sectionCount == 3) {
							if (user.getSeccStateCode() != null && user.getSeccStateCode() != ""
									&& !(user.getSeccStateCode()).equals("N/A")) {
								addressInst.setSeccStateCode(user.getSeccStateCode());
							} else {
								response.setResponseCode(306);
								response.setResponseDesc("Please select SECC state.");
								return response;
							}

							if (user.getSeccDistrictCode() != null && user.getSeccDistrictCode() != ""
									&& !(user.getSeccDistrictCode()).equals("N/A")) {
								addressInst.setSeccDistrictCode(user.getSeccDistrictCode());
							} else {
								response.setResponseCode(306);
								response.setResponseDesc("Please select SECC district.");
								return response;
							}

							if (user.getSeccBlockCode() != null && user.getSeccBlockCode() != ""
									&& !(user.getSeccBlockCode()).equals("N/A")) {
								addressInst.setSeccBlockCode(user.getSeccBlockCode());
							} else {
								response.setResponseCode(306);
								response.setResponseDesc("Please select SECC block.");
								return response;
							}

							if (user.getSeccGpCode() != null && user.getSeccGpCode() != ""
									&& !(user.getSeccGpCode()).equals("N/A")) {
								addressInst.setSecc_gp_code(user.getSeccGpCode());
							} else {
								response.setResponseCode(306);
								response.setResponseDesc("Please select SECC grampanchayat.");
								return response;
							}

							if (user.getSeccVillageCode() != null && user.getSeccVillageCode() != ""
									&& !(user.getSeccVillageCode()).equals("N/A")) {
								addressInst.setSeccVillageCode(user.getSeccVillageCode());
							} else {
								response.setResponseCode(306);
								response.setResponseDesc("Please select SECC village.");
								return response;
							}

							if (user.getSeccCandidateName() != null && user.getSeccCandidateName() != ""
									&& !(user.getSeccCandidateName()).equals("N/A")) {
								addressInst.setSeccCandidateName(user.getSeccCandidateName());
							} else {
								response.setResponseCode(306);
								response.setResponseDesc("Please select SECC candidate name.");
								return response;
							}

							addressInst.setSeccAhlTin(user.getSeccAhlTin());

							if (user.getSeccAhlTin() != null || !user.getSeccAhlTin().isEmpty()) {

								profile.setPovertyStatus("RP");

							} else if (!profile.getPovertyStatus().equals("RP")) {

								profile.setPovertyStatus("NRP");
							}

							statusInst.setUpdatedOn(currentTimestamp);
							statusInst.setSeccStatus((short) 1);
							profileRepo.save(profile);
							addressRepo.save(addressInst);
							statusRepo.save(statusInst);

							response.setResponseMsg("SECC details saved sucessfully");
							response.setResponseCode(HttpStatus.OK.value());
							response.setResponseDesc(HttpStatus.OK.name());
							return response;

						} else if (sectionCount == 4) {

							if (user.getLanguageKnown() != null && user.getLanguageKnown() != ""
									&& !(user.getLanguageKnown()).equals("N/A")) {
								profile.setLanguageKnown(user.getLanguageKnown());
							} else {
								response.setResponseCode(306);
								response.setResponseDesc("Please select language you known.");
								return response;
							}

							if (user.getMonthYearOfPassing() != null && user.getMonthYearOfPassing() != ""
									&& !(user.getMonthYearOfPassing()).equals("N/A")) {
								profile.setMonthYearOfPassing(user.getMonthYearOfPassing());
							} else {
								response.setResponseCode(306);
								response.setResponseDesc("Please select passing month year.");
								return response;
							}

							if (user.getTechEducationDomain() != 0) {
								profile.setTechEducationDomain(user.getTechEducationDomain());
							} else {
								profile.setTechEducationDomain(0);
							}

							if (user.getTechQualification() != 0) {
								profile.setHighestClass(""); // comment: suggest
								profile.setTechQualification(user.getTechQualification());
							} else {
								profile.setTechQualification(0);
							}

							String techQual = user.getHighestClass(); // <-- assuming string

							if (techQual != "") {
								profile.setTechQualification(0);
								profile.setTechEducationDomain(0);
								profile.setHighestClass(techQual); // store original value
							} else {
								profile.setHighestClass(""); // store original value
							}

							// System.out.println("Education Details Saved");
							// if (user.getHighestEducation() != null && user.getHighestEducation() != ""
							// && !(user.getHighestEducation()).equals("N/A")) {
							// profile.setHighestEducation(user.getHighestEducation());
							// } else {
							// response.setResponseCode(306);
							// response.setResponseDesc("Please select highest education.");
							// return response;
							// }

							// TODO: remove getIsTechEducate() -- Abhishek
							// if (user.getIsTechEducate() != null && user.getIsTechEducate() != ""
							// && !(user.getIsTechEducate()).equals("N/A")) {
							// profile.setIsTechEducate(user.getIsTechEducate());
							// } else {
							// response.setResponseCode(306);
							// response.setResponseDesc("Please select Technical Education.");
							// return response;
							// }

							// TODO : handle school case -- Abhishek
							// String highestEdu = user.getHighestEducation();
							// highestEdu = "Schooling";
							// if ("Schooling".equalsIgnoreCase(highestEdu)) {
							// int techQual = user.getHighestClass();
							// // check if techQualification is between 1 and 12
							// if (techQual >= 1 && techQual <= 12) {
							// profile.setTechQualification(techQual);
							// profile.setHighestClass(techQual);
							// } else {
							// response.setResponseCode(306);
							// response.setResponseDesc("Technical Qualification not valid for School
							// category!");
							// return response;
							// }
							//
							// } else {
							//
							// if (user.getTechQualification() != 0) {
							// profile.setTechQualification(user.getTechQualification());
							// }
							// }

							// TODO :- remove this field -- Abhishek
							// if (user.getMonthYearOfPassingTechEdu() != null &&
							// user.getMonthYearOfPassingTechEdu() != ""
							// && !(user.getMonthYearOfPassingTechEdu()).equals("N/A")) {
							// profile.setMonthYearOfPassingTechEdu(user.getMonthYearOfPassingTechEdu());
							// }

							statusInst.setUpdatedOn(currentTimestamp);
							statusInst.setEducationalStatus((short) 1);
							profileRepo.save(profile);
							statusRepo.save(statusInst);

							response.setResponseMsg("Educational details saved sucessfully");
							response.setResponseCode(HttpStatus.OK.value());
							response.setResponseDesc(HttpStatus.OK.name());
							System.out.println("Education Details Saved");
							return response;

						} else if (sectionCount == 5) {
							if (user.getIsEmployeed() != null && user.getIsEmployeed() != ""
									&& !(user.getIsEmployeed()).equals("N/A")) {
								profile.setIsEmployeed(user.getIsEmployeed());
							}

							if (user.getEmploymentMode() != null && user.getEmploymentMode() != ""
									&& !(user.getEmploymentMode()).equals("N/A")) {
								profile.setEmploymentMode(user.getEmploymentMode());
							}

							// TODO :Remove this code
							// if (user.getIntrestedIn() != null && user.getIntrestedIn() != ""
							// && !(user.getIntrestedIn()).equals("N/A")) {
							// profile.setIntrestedIn(user.getIntrestedIn());
							// }

							if (user.getEmploymentPreference() != null && user.getEmploymentPreference() != ""
									&& !(user.getEmploymentPreference()).equals("N/A")) {
								profile.setEmploymentPreference(user.getEmploymentPreference());
							}

							if (user.getJobLocationPreference() != null && user.getJobLocationPreference() != ""
									&& !(user.getJobLocationPreference()).equals("N/A")) {
								profile.setJobLocationPreference(user.getJobLocationPreference());
							}

							if (user.getMonthlyEarning() != 0) {
								profile.setMonthlyEarning(user.getMonthlyEarning());
							}

							if (user.getExpectedMonthlySalary() != 0) {
								profile.setExpectedMonthlySalary(user.getExpectedMonthlySalary());
							}

							statusInst.setUpdatedOn(currentTimestamp);
							statusInst.setEmploymentStatus((short) 1);
							profileRepo.save(profile);
							statusRepo.save(statusInst);

							response.setResponseMsg("Employement details saved sucessfully");
							response.setResponseCode(HttpStatus.OK.value());
							response.setResponseDesc(HttpStatus.OK.name());
							return response;

						} else if (sectionCount == 6) {

							if (adhaarInst.isInBatch()) {
								response.setResponseCode(403);
								response.setResponseDesc(
										"Once a candidate is enrolled in a batch, they cannot change their training details.");
								return response;
							}

							if (user.getIsPreTraining() != null && user.getIsPreTraining() != ""
									&& !(user.getIsPreTraining()).equals("N/A")) {
								profile.setIsPreTraining(user.getIsPreTraining());
							}

							if (user.getPreCompletedTraining() != null && user.getPreCompletedTraining() != ""
									&& !(user.getPreCompletedTraining()).equals("N/A")) {
								profile.setPreCompletedTraining(user.getPreCompletedTraining());
							}

							if (user.getCompTrainingDuration() != null && user.getCompTrainingDuration() != ""
									&& !(user.getCompTrainingDuration()).equals("N/A")) {
								profile.setCompTrainingDuration(user.getCompTrainingDuration());
							}

							// if (user.getHearedAboutScheme() != null && user.getHearedAboutScheme() != ""
							// && !(user.getHearedAboutScheme()).equals("N/A")) {
							// profile.setHearedAboutScheme(user.getHearedAboutScheme());
							// }

							// if (user.getHearedFrom() != null && user.getHearedFrom() != ""
							// && !(user.getHearedFrom()).equals("N/A")) {
							// profile.setHearedFrom(user.getHearedFrom());
							// }

							if (user.getIntrestedSector() != null && user.getIntrestedSector() != ""
									&& !(user.getIntrestedSector()).equals("N/A")) {
								profile.setIntrestedSector(user.getIntrestedSector());
							}

							if (user.getIntrestedTrade() != null && user.getIntrestedTrade() != ""
									&& !(user.getIntrestedTrade()).equals("N/A")) {
								profile.setIntrestedTrade(user.getIntrestedTrade());
							}

							if (!user.getTradeCode().trim().isEmpty() && !(user.getIntrestedTrade()).equals("N/A")) {
								profile.setTradeCode(user.getTradeCode());
							}

							if (!user.getSchemeType().trim().isEmpty() && !(user.getSchemeType()).equals("N/A")) {
								profile.setSchemeType(user.getSchemeType());
							}

							statusInst.setUpdatedOn(currentTimestamp);
							statusInst.setTrainingStatus((short) 1);
							profileRepo.save(profile);
							statusRepo.save(statusInst);

							response.setResponseMsg("Training details saved sucessfully");
							response.setResponseCode(HttpStatus.OK.value());
							response.setResponseDesc(HttpStatus.OK.name());
							return response;

						} else if (sectionCount == 7) {

							// UserBankDetails bankInst = bankRepo.getBankInstanceByLogin(loginid);
							UserBankDetails bankInst = new UserBankDetails();
							if (user.getBankCode() != null && user.getBankCode() != ""
									&& !(user.getBankCode()).equals("N/A")) {
								String bankCode = (enc.decrypt(user.getBankCode()));
								bankInst.setBankCode(Short.parseShort(bankCode));
							}

							if (user.getBankBranchCode() != null && user.getBankBranchCode() != ""
									&& !(user.getBankBranchCode()).equals("N/A")) {
								String bankBranchCode = (enc.decrypt(user.getBankBranchCode()));
								bankInst.setBankBranchCode(Integer.parseInt(bankBranchCode));
							}

							if (user.getBankAccountNo() != null && user.getBankAccountNo() != ""
									&& !(user.getBankAccountNo()).equals("N/A")) {
								bankInst.setBankAccountNo(user.getBankAccountNo());
							}

							if (user.getIfscCode() != null && user.getIfscCode() != ""
									&& !(user.getIfscCode()).equals("N/A")) {
								bankInst.setIfscCode(enc.decrypt(user.getIfscCode()));
							}

							if (user.getPanNo() != null && user.getPanNo() != "" && !(user.getPanNo()).equals("N/A")) {
								bankInst.setPanNo(user.getPanNo());
							}

							statusInst.setUpdatedOn(currentTimestamp);
							statusInst.setBankingStatus((short) 1);
							bankRepo.save(bankInst);
							statusRepo.save(statusInst);

							response.setResponseMsg("Bank details saved sucessfully");
							response.setResponseCode(HttpStatus.OK.value());
							response.setResponseDesc(HttpStatus.OK.name());
							return response;
						} else {
							response.setResponseMsg("Please select atleast one section.");
							response.setResponseCode(306);
							response.setResponseDesc("Invalid section request");

						}

					} else {
						authRepo.delete(authinstance);
						response.setResponseCode(204);
						response.setResponseDesc("Invalid request");
						return response;
					}

				} else {
					response.setResponseCode(301);
					response.setResponseDesc("Please upgread your app first.");
					return response;
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
		secure.setSecurityHeaders(res);
		return response;
	}

	public Response<Map<String, Object>> sectionStatus(UserDetailsBean user, HttpServletRequest req,
			HttpServletResponse res) {
		Response<Map<String, Object>> response = new Response<>();
		List<Map<String, Object>> wrappedList = new ArrayList<>();
		List<Object[]> sectionStatusList = null;
		String loginId = user.getLoginId();
		String imeiNo = user.getImeiNo();
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

		boolean authFlag = authRepo.getTokenValidationSection(loginId, authToken, imeiNo);

		try {
			if (authFlag) {
				String appVersion = user.getAppVersion();
				String status = otpRepo.appVersionStatus(appVersion);

				// Check if the app version is active
				if (status != null && status.equals("Active") && !status.equals("null")) {

					sectionStatusList = repo.getSectionStatus(loginId);

					if (sectionStatusList != null && !sectionStatusList.isEmpty()) {
						wrappedList = sectionStatusList.stream().map(p -> {
							Map<String, Object> map = new HashMap<>();
							int personalStatus = (short) p[0];
							int addressStatus = (short) p[1];
							int seccStatus = (short) p[2];
							int educationalStatus = (short) p[3];
							int employmentStatus = (short) p[4];
							int trainingStatus = (short) p[5];
							int bankingStatus = (short) p[6];
							String adhaarImage = (String) p[7];
							long attachmentId = (long) p[8];
							String state = (String) p[9];
							String aadhaarNo = (String) p[12];
							String decAadhaarNo = null;
							String encAadhaarNo = null;
							try {
								decAadhaarNo = enc.decryptNew(aadhaarNo);
								encAadhaarNo = enc.encrypt(decAadhaarNo);
							} catch (Exception e) {
								e.printStackTrace();
							}
							String imagePath = null;
							String filePath;

							// Calculate total percentage based on the highest status
							String totalPercentage = calculateTotalPercentage(personalStatus, addressStatus, seccStatus,
									educationalStatus, employmentStatus, trainingStatus, bankingStatus);
							if (totalPercentage == null) {
								totalPercentage = "0";
							}

							// Populate the map with status values
							map.put("personalStatus", personalStatus); // 15
							map.put("addressStatus", addressStatus); // 15
							map.put("seccStatus", seccStatus); // 15
							map.put("educationalStatus", educationalStatus); // 15
							map.put("employmentStatus", employmentStatus); // 15
							map.put("trainingStatus", trainingStatus); // 15
							map.put("bankingStatus", bankingStatus); // 15
							map.put("totalPercentage", totalPercentage); // 10
							map.put("isFaceRegistred", p[10]);
							map.put("candidateName", p[11]);
							map.put("aadhaarEnc", encAadhaarNo);
							map.put("certFlag", "N");
//							map.put("certFlag", "https://kaushal.dord.gov.in/demo/#/certificate-details/");

							String photoPath = MyConstants.UPLOAD_ADHAAR + state;
							filePath = photoPath + File.separator + attachmentId + adhaarImage;
							imagePath = MyConstants.getImageAsBase64(filePath);

							// System.out.println("imagePath : " + filePath);

							if (adhaarImage != null) {
								imagePath = MyConstants.getImageAsBase64(filePath);
								map.put("imagePath", imagePath);
							} else {
								map.put("imagePath", "N/A");
							}
							map.put("stateLgdCode", p[13]);
							map.put("firstLogin", p[14]);
							map.put("schemeType", p[15]);
							map.put("stateCode", p[16]);
							map.put("ojtFlag", p[17]);

							return map;
						}).collect(Collectors.toList());

						response.setWrappedList(wrappedList);
						response.setResponseCode(HttpStatus.OK.value());
						response.setResponseDesc(HttpStatus.OK.name());
						response.setResponseMsg("");
					}

				} else {
					// Outdated app version
					response.setResponseCode(301);
					response.setResponseDesc("Please upgrade your app first.");
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
		secure.setSecurityHeaders(res);
		return response;
	}

	// Helper method to calculate total percentage based on status
	private String calculateTotalPercentage(int personalStatus, int addressStatus, int seccStatus,
			int educationalStatus, int employmentStatus, int trainingStatus, int bankingStatus) {
		float picount = 0.0f, adcount = 0.0f, secount = 0.0f, edcount = 0.0f, empcount = 0.0f, trancount = 0.0f,
				bankcount = 0.0f;
		float countResult = 0.0f;
		if (bankingStatus == 1) {
			bankcount = 10.0f;
		}
		if (trainingStatus == 1) {
			trancount = 15.0f;
		}
		if (employmentStatus == 1) {
			empcount = 15.0f;
		}
		if (educationalStatus == 1) {
			edcount = 15.0f;
		}
		if (seccStatus == 1) {
			secount = 15.0f;
		}
		if (addressStatus == 1) {
			adcount = 15.0f;
		}
		if (personalStatus == 1) {
			picount = 15.0f;
		}
		countResult = countResult + (picount + adcount + secount + edcount + empcount + trancount + bankcount);
		String result = String.valueOf(countResult);
		return result;
	}

	public MasterResponse<Map<String, Object>> getCandidateMasterList(UserDetailsBean user, HttpServletRequest req,
			HttpServletResponse res) {

		MasterResponse<Map<String, Object>> response = new MasterResponse<>();
		List<Map<String, Object>> wrappedList = new ArrayList<>();
		List<Map<String, Object>> addressWrappedList = new ArrayList<>();

		List<Map<String, Object>> personalList = new ArrayList<>();
		List<Map<String, Object>> addressList = new ArrayList<>();
		List<Map<String, Object>> seccList = new ArrayList<>();
		List<Map<String, Object>> educationList = new ArrayList<>();
		List<Map<String, Object>> employeeList = new ArrayList<>();
		List<Map<String, Object>> trainingList = new ArrayList<>();
		List<Map<String, Object>> bankingList = new ArrayList<>();

		List<Object[]> candidatePersonalData = null;
		List<Object[]> addressData = null;
		List<Object[]> seccData = null;
		List<Object[]> educationData = null;
		List<Object[]> employeeData = null;
		List<Object[]> trainingData = null;
		List<Object[]> bankingData = null;
		Map<String, Object> map1 = new HashMap<>();
		Map<String, Object> map2 = new HashMap<>();
		String loginid = user.getLoginId();

		Timestamp currentTimestamp = Timestamp.from(Instant.now());
		AuthenticationEntity authinstance = authRepo.getAuthInstance(loginid);
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

		boolean authFlag = authRepo.getTokenValidation(loginid, authToken);

		try {
			if (authFlag) {

				String appVersion = user.getAppVersion();
				String status = otpRepo.appVersionStatus(appVersion);
				String voterIdCard = "";
				String VoterImagePath = null;
				String voterFilePath = "";
				String dlCard = "";
				String dlImagePath = null;
				String dlFilePath = "";
				String categoryCert = "";
				String categoryCertPath = null;
				String categoryCertFilePath = "";
				String minorityCert = "";
				String minorityCertFilePath = "";
				String minorityCertPath = null;
				String disablityCert = "";
				String disablityCertFilePath = "";
				String disablityCertPath = null;
				String naregaCard = "";
				String naregaCardFilePath = "";
				String naregaCardPath = null;
				String rationCard = "";
				String rationCardFilePath = "";
				String rationCardPath = null;
				String rsbyCard = "";
				String rsbyCardFilePath = "";
				String rsbyCardPath = null;
				String pipCert = "";
				String pipCertFilePath = "";
				String pipCertPath = null;
				String shgImage = "";
				String shgImageFilePath = "";
				String shgImagePath = null;
				String pmaygCert = "";
				String pmaygCertFilePath = "";
				String pmaygCertPath = null;
				String residenceCert = "";
				String residenceCertFilePath = "";
				String residenceCertPath = null;
				long attachVoterId = 0;
				String voterState = "";
				long attachIdDl = 0;
				String dlState = "";
				long attachIdcategoryCert = 0;
				String categoryCertState = "";
				long attachIdminorityCert = 0;
				String minorityCertState = "";
				long attachIddisablityCert = 0;
				String disablityCertState = "";
				long attachIdnaregaCard = 0;
				String naregaCardState = "";
				long attachIdrationCard = 0;
				String rationCardState = "";
				long attachIdrsbyCard = 0;
				String rsbyCardState = "";
				long attachIdpipCert = 0;
				String pipCertState = "";
				long attachIdShgImage = 0;
				String shgImageState = "";
				long attachIdpmaygCert = 0;
				String pmaygCertState = "";
				long attachIdresidenceCert = 0;
				String residenceCertState = "";

				if (status != null && status.equals("Active") && !status.equals("null")) {

					/* ==============Personal Data============= */

					/* ============Voter Id Card============= */
					List<Object[]> voterIdCardList = repo.getCandidateVoterCard(loginid);
					if (voterIdCardList != null && voterIdCardList.size() != 0) {
						for (Object[] voterIdCard1 : voterIdCardList) {
							voterIdCard = (String) voterIdCard1[0];
							attachVoterId = (long) voterIdCard1[1];
							voterState = (String) voterIdCard1[2];

						}

					}
					if (voterIdCard != null) {
						String voterPhotoPath = MyConstants.UPLOAD_VOTER + voterState;
						voterFilePath = voterPhotoPath + File.separator + attachVoterId + voterIdCard;
						VoterImagePath = MyConstants.getImageAsBase64(voterFilePath);
						map1.put("VoterImagePath", VoterImagePath);
					} else {
						map1.put("VoterImagePath", "N/A");
					}

					/* ============SHG Image============= */
					List<Object[]> shgImageList = repo.getCandidateSHGImage(loginid);
					if (shgImageList != null && shgImageList.size() != 0) {
						for (Object[] shgImage1 : shgImageList) {
							shgImage = (String) shgImage1[0];
							attachIdShgImage = (long) shgImage1[1];
							shgImageState = (String) shgImage1[2];

						}

					}
					if (shgImage != null) {
						String shgImagePhotoPath = MyConstants.UPLOAD_SHG + shgImageState;
						shgImageFilePath = shgImagePhotoPath + File.separator + attachIdShgImage + shgImage;
						shgImagePath = MyConstants.getImageAsBase64(shgImageFilePath);
						map1.put("shgImage", shgImagePath);
					} else {
						map1.put("shgImage", "N/A");
					}

					/* ============Driving License============= */
					List<Object[]> dlCardList = repo.getCandidateDrivingLicenseCard(loginid);
					if (dlCardList != null && dlCardList.size() != 0) {
						for (Object[] dlCard1 : dlCardList) {
							dlCard = (String) dlCard1[0];
							attachIdDl = (long) dlCard1[1];
							dlState = (String) dlCard1[2];

						}

					}
					if (dlCard != null) {
						String dlPhotoPath = MyConstants.UPLOAD_DL + dlState;
						dlFilePath = dlPhotoPath + File.separator + attachIdDl + dlCard;
						dlImagePath = MyConstants.getImageAsBase64(dlFilePath);
						map1.put("dlImagePath", dlImagePath);
					} else {
						map1.put("dlImagePath", "N/A");
					}

					/* ============Category Certificate============= */
					List<Object[]> categoryCertList = repo.getCandidateCategoryCertificate(loginid);
					if (categoryCertList != null && categoryCertList.size() != 0) {
						for (Object[] categoryCert1 : categoryCertList) {
							categoryCert = (String) categoryCert1[0];
							attachIdcategoryCert = (long) categoryCert1[1];
							categoryCertState = (String) categoryCert1[2];

						}

					}
					if (categoryCert != null) {
						String categoryCertPhotoPath = MyConstants.UPLOAD_CAST + categoryCertState;
						categoryCertFilePath = categoryCertPhotoPath + File.separator + attachIdcategoryCert
								+ categoryCert;
						categoryCertPath = MyConstants.getImageAsBase64(categoryCertFilePath);
						map1.put("categoryCertPath", categoryCertPath);
					} else {
						map1.put("categoryCertPath", "N/A");
					}

					/* ===========Minority Certificate============== */
					List<Object[]> minorityCertList = repo.getCandidateMinorityCertificate(loginid);
					if (minorityCertList != null && minorityCertList.size() != 0) {
						for (Object[] minorityCert1 : minorityCertList) {
							minorityCert = (String) minorityCert1[0];
							attachIdminorityCert = (long) minorityCert1[1];
							minorityCertState = (String) minorityCert1[2];

						}

					}
					if (minorityCert != null) {
						String minorityCertPhotoPath = MyConstants.UPLOAD_MINORITY + minorityCertState;
						minorityCertFilePath = minorityCertPhotoPath + File.separator + attachIdminorityCert
								+ minorityCert;
						minorityCertPath = MyConstants.getImageAsBase64(minorityCertFilePath);
						map1.put("minorityCertPath", minorityCertPath);
					} else {
						map1.put("minorityCertPath", "N/A");
					}

					/* ===========disability Certificate============== */
					List<Object[]> disablityCertList = repo.getCandidateDisabilityCertificate(loginid);
					if (disablityCertList != null && disablityCertList.size() != 0) {
						for (Object[] disablityCert1 : disablityCertList) {
							disablityCert = (String) disablityCert1[0];
							attachIddisablityCert = (long) disablityCert1[1];
							disablityCertState = (String) disablityCert1[2];

						}

					}
					if (disablityCert != null) {
						String disablityCertPhotoPath = MyConstants.UPLOAD_DISABLITY + disablityCertState;
						disablityCertFilePath = disablityCertPhotoPath + File.separator + attachIddisablityCert
								+ disablityCert;
						disablityCertPath = MyConstants.getImageAsBase64(disablityCertFilePath);
						map1.put("disablityCertPath", disablityCertPath);
					} else {
						map1.put("disablityCertPath", "N/A");
					}

					/* ===========Narega Card============== */
					List<Object[]> naregaCardList = repo.getCandidateNaregaCard(loginid);
					if (naregaCardList != null && naregaCardList.size() != 0) {
						for (Object[] naregaCard1 : naregaCardList) {
							naregaCard = (String) naregaCard1[0];
							attachIdnaregaCard = (long) naregaCard1[1];
							naregaCardState = (String) naregaCard1[2];

						}

					}
					if (naregaCard != null) {
						String naregaCardPhotoPath = MyConstants.UPLOAD_NREGA + naregaCardState;
						naregaCardFilePath = naregaCardPhotoPath + File.separator + attachIdnaregaCard + naregaCard;
						naregaCardPath = MyConstants.getImageAsBase64(naregaCardFilePath);
						map1.put("naregaCardPath", naregaCardPath);
					} else {
						map1.put("naregaCardPath", "N/A");
					}

					/* ===========Antyodaya Card============== */
					List<Object[]> rationCardList = repo.getCandidateRationCard(loginid);
					if (rationCardList != null && rationCardList.size() != 0) {
						for (Object[] rationCard1 : rationCardList) {
							rationCard = (String) rationCard1[0];
							attachIdrationCard = (long) rationCard1[1];
							rationCardState = (String) rationCard1[2];

						}

					}
					if (rationCard != null) {
						String rationCardPhotoPath = MyConstants.UPLOAD_RASAN + rationCardState;
						rationCardFilePath = rationCardPhotoPath + File.separator + attachIdrationCard + rationCard;
						rationCardPath = MyConstants.getImageAsBase64(rationCardFilePath);
						map1.put("rationCardPath", rationCardPath);
					} else {
						map1.put("rationCardPath", "N/A");
					}

					/* ===========RSBY Card============== */
					List<Object[]> rsbyCardList = repo.getCandidateRsbyCard(loginid);
					if (rsbyCardList != null && rsbyCardList.size() != 0) {
						for (Object[] rsbyCard1 : rsbyCardList) {
							rsbyCard = (String) rsbyCard1[0];
							attachIdrsbyCard = (long) rsbyCard1[1];
							rsbyCardState = (String) rsbyCard1[2];

						}

					}
					if (rsbyCard != null) {
						String rsbyCardPhotoPath = MyConstants.UPLOAD_RSBY + rsbyCardState;
						rsbyCardFilePath = rsbyCardPhotoPath + File.separator + attachIdrsbyCard + rsbyCard;
						rsbyCardPath = MyConstants.getImageAsBase64(rsbyCardFilePath);
						map1.put("rsbyCardPath", rsbyCardPath);
					} else {
						map1.put("rsbyCardPath", "N/A");
					}

					/* ===========PIP Cert============== */

					List<Object[]> pipCertList = repo.getCandidatePipCert(loginid);
					if (pipCertList != null && pipCertList.size() != 0) {
						for (Object[] pipCertId : pipCertList) {
							pipCert = (String) pipCertId[0];
							attachIdpipCert = (long) pipCertId[1];
							pipCertState = (String) pipCertId[2];

						}

					}
					if (pipCert != null) {
						String pipCertPhotoPath = MyConstants.UPLOAD_PIP + pipCertState;
						pipCertFilePath = pipCertPhotoPath + File.separator + attachIdpipCert + pipCert;
						pipCertPath = MyConstants.getImageAsBase64(pipCertFilePath);
						map1.put("pipCert", pipCertPath);
					} else {
						map1.put("pipCert", "N/A");
					}

					/* ===========PMAY-G Cert============== */

					List<Object[]> pmaygCertList = repo.getCandidatePmaygCert(loginid);
					if (pmaygCertList != null && pmaygCertList.size() != 0) {
						for (Object[] pmaygCertId : pmaygCertList) {
							pmaygCert = (String) pmaygCertId[0];
							attachIdpmaygCert = (long) pmaygCertId[1];
							pmaygCertState = (String) pmaygCertId[2];

						}

					}
					if (pmaygCert != null) {
						String pmaygCertPhotoPath = MyConstants.UPLOAD_PMAYG + pmaygCertState;
						pmaygCertFilePath = pmaygCertPhotoPath + File.separator + attachIdpmaygCert + pmaygCert;
						pmaygCertPath = MyConstants.getImageAsBase64(pmaygCertFilePath);
						map1.put("pmaygAttachment", pmaygCertPath);
					} else {
						map1.put("pmaygAttachment", "N/A");
					}

					candidatePersonalData = repo.getCandidatePersonalDetails(loginid);
					// System.out.println("candidatePersonalData : " + candidatePersonalData);
					if (candidatePersonalData != null && candidatePersonalData.size() != 0) {
						personalList = candidatePersonalData.stream().map(p -> {
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("guardianName",
									(p[0] != null && !p[0].toString().trim().isEmpty()) ? (String) p[0] : "N/A");
							map.put("motherName",
									(p[1] != null && !p[1].toString().trim().isEmpty()) ? (String) p[1] : "N/A");
							map.put("guardianMobilNo",
									(p[2] != null && !p[2].toString().trim().isEmpty()) ? (String) p[2] : "N/A");
							map.put("annualFamilyIncome",
									(p[3] != null && !p[3].toString().trim().isEmpty()) ? p[3].toString() : "N/A");
							map.put("voterId",
									(p[4] != null && !p[4].toString().trim().isEmpty()) ? (String) p[4] : "N/A");
							map.put("dlNo",
									(p[5] != null && !p[5].toString().trim().isEmpty()) ? (String) p[5] : "N/A");
							map.put("castCategory",
									(p[6] != null && !p[6].toString().trim().isEmpty()) ? (String) p[6] : "N/A");
							map.put("maritalStatus",
									(p[7] != null && !p[7].toString().trim().isEmpty()) ? (String) p[7] : "N/A");
							map.put("isMinority",
									(p[8] != null && !p[8].toString().trim().isEmpty()) ? (String) p[8] : "N/A");
							map.put("isDisablity",
									(p[9] != null && !p[9].toString().trim().isEmpty()) ? (String) p[9] : "N/A");
							map.put("isNarega",
									(p[10] != null && !p[10].toString().trim().isEmpty()) ? (String) p[10] : "N/A");
							map.put("naregaJobCard",
									(p[11] != null && !p[11].toString().trim().isEmpty()) ? (String) p[11] : "N/A");
							map.put("isSHG",
									(p[12] != null && !p[12].toString().trim().isEmpty()) ? (String) p[12] : "N/A");
							map.put("shgNo",
									(p[13] != null && !p[13].toString().trim().isEmpty()) ? (String) p[13] : "N/A");
							map.put("antyodaya",
									(p[14] != null && !p[14].toString().trim().isEmpty()) ? (String) p[14] : "N/A");
							map.put("isRSBY",
									(p[15] != null && !p[15].toString().trim().isEmpty()) ? (String) p[15] : "N/A");
							map.put("isPIP",
									(p[16] != null && !p[16].toString().trim().isEmpty()) ? (String) p[16] : "N/A");
							map.put("isPmayg",
									(p[17] != null && !p[17].toString().trim().isEmpty()) ? (String) p[17] : "N/A");

							return map;
						})

								.collect(Collectors.toList());

					}

					/* ==============Address Data============= */
					/* ===========Residence Certificate============== */
					List<Object[]> residenceCertList = repo.getCandidateResidenceCert(loginid);
					if (residenceCertList != null && residenceCertList.size() != 0) {
						for (Object[] residenceCert1 : residenceCertList) {
							residenceCert = (String) residenceCert1[0];
							attachIdresidenceCert = (long) residenceCert1[1];
							residenceCertState = (String) residenceCert1[2];

						}

					}
					if (residenceCert != null) {
						String residenceCertPhotoPath = MyConstants.UPLOAD_RESIDENT + residenceCertState;
						residenceCertFilePath = residenceCertPhotoPath + File.separator + attachIdresidenceCert
								+ residenceCert;
						residenceCertPath = MyConstants.getImageAsBase64(residenceCertFilePath);
						map2.put("residenceCertPath", residenceCertPath);
					} else {
						map2.put("residenceCertPath", "N/A");
					}

					addressData = repo.getCandidateAddressDetails(loginid);
					// System.out.println("addressData : " + addressData);
					if (addressData != null && addressData.size() != 0) {
						addressList = addressData.stream().map(p -> {
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("permanentStateCode",
									(p[0] != null && !p[0].toString().trim().isEmpty()) ? (String) p[0] : "N/A");
							map.put("permanentStateName",
									(p[1] != null && !p[1].toString().trim().isEmpty()) ? (String) p[1] : "N/A");
							map.put("permanentDistrictCode",
									(p[2] != null && !p[2].toString().trim().isEmpty()) ? (String) p[2] : "N/A");
							map.put("permanentDistrictName",
									(p[3] != null && !p[3].toString().trim().isEmpty()) ? (String) p[3] : "N/A");
							map.put("permanentBlcokCode",
									(p[4] != null && !p[4].toString().trim().isEmpty()) ? (String) p[4] : "N/A");
							map.put("permanentBlockName",
									(p[5] != null && !p[5].toString().trim().isEmpty()) ? (String) p[5] : "N/A");
							map.put("permanentGPCode",
									(p[6] != null && !p[6].toString().trim().isEmpty()) ? (String) p[6] : "N/A");
							map.put("permanentGPName",
									(p[7] != null && !p[7].toString().trim().isEmpty()) ? (String) p[7] : "N/A");
							map.put("permanentVillageCode",
									(p[8] != null && !p[8].toString().trim().isEmpty()) ? (String) p[8] : "N/A");
							map.put("permanentVillageName",
									(p[9] != null && !p[9].toString().trim().isEmpty()) ? (String) p[9] : "N/A");
							map.put("permanentStreet1",
									(p[10] != null && !p[10].toString().trim().isEmpty()) ? (String) p[10] : "N/A");
							map.put("permanentStreet2",
									(p[11] != null && !p[11].toString().trim().isEmpty()) ? (String) p[11] : "N/A");
							map.put("permanentPinCode",
									(p[12] != null && !p[12].toString().trim().isEmpty()) ? (String) p[12] : "N/A");
							map.put("isPresentAddressSame",
									(p[13] != null && !p[13].toString().trim().isEmpty()) ? (String) p[13] : "N/A");
							map.put("presentStateCode",
									(p[14] != null && !p[14].toString().trim().isEmpty()) ? (String) p[14] : "N/A");
							map.put("presentStateName",
									(p[15] != null && !p[15].toString().trim().isEmpty()) ? (String) p[15] : "N/A");
							map.put("presentDistrictCode",
									(p[16] != null && !p[16].toString().trim().isEmpty()) ? (String) p[16] : "N/A");
							map.put("presentDistrictName",
									(p[17] != null && !p[17].toString().trim().isEmpty()) ? (String) p[17] : "N/A");
							map.put("presentBlcokCode",
									(p[18] != null && !p[18].toString().trim().isEmpty()) ? (String) p[18] : "N/A");
							map.put("presentBlockName",
									(p[19] != null && !p[19].toString().trim().isEmpty()) ? (String) p[19] : "N/A");
							map.put("presentGPCode",
									(p[20] != null && !p[20].toString().trim().isEmpty()) ? (String) p[20] : "N/A");
							map.put("presentGPName",
									(p[21] != null && !p[21].toString().trim().isEmpty()) ? (String) p[21] : "N/A");
							map.put("presentVillageCode",
									(p[22] != null && !p[22].toString().trim().isEmpty()) ? (String) p[22] : "N/A");
							map.put("presentVillageName",
									(p[23] != null && !p[23].toString().trim().isEmpty()) ? (String) p[23] : "N/A");
							map.put("presentStreet1",
									(p[24] != null && !p[24].toString().trim().isEmpty()) ? (String) p[24] : "N/A");
							map.put("presentStreet2",
									(p[25] != null && !p[25].toString().trim().isEmpty()) ? (String) p[25] : "N/A");
							map.put("presentPinCode",
									(p[26] != null && !p[26].toString().trim().isEmpty()) ? (String) p[26] : "N/A");
							map.put("permanentLocality",
									(p[28] != null && !p[28].toString().trim().isEmpty()) ? (String) p[28] : "N/A");
							map.put("permanentulbCode",
									(p[29] != null && !p[29].toString().trim().isEmpty()) ? (String) p[29] : "N/A");
							map.put("permanentulbName",
									(p[30] != null && !p[30].toString().trim().isEmpty()) ? (String) p[30] : "N/A");
							map.put("permanentWardCode",
									(p[31] != null && !p[31].toString().trim().isEmpty()) ? (String) p[31] : "N/A");
							map.put("permanentWardName",
									(p[32] != null && !p[32].toString().trim().isEmpty()) ? (String) p[32] : "N/A");
							map.put("presentUlbCode",
									(p[33] != null && !p[33].toString().trim().isEmpty()) ? (String) p[33] : "N/A");
							map.put("presentUlbName",
									(p[34] != null && !p[34].toString().trim().isEmpty()) ? (String) p[34] : "N/A");
							map.put("presentWardCode",
									(p[35] != null && !p[35].toString().trim().isEmpty()) ? (String) p[35] : "N/A");
							map.put("presentWardName",
									(p[36] != null && !p[36].toString().trim().isEmpty()) ? (String) p[36] : "N/A");
							map.put("presentLocality",
									(p[37] != null && !p[37].toString().trim().isEmpty()) ? (String) p[37] : "N/A");
							map.put("permanentDistrictLgdCode",
									(p[38] != null && !p[38].toString().trim().isEmpty()) ? (String) p[38] : "N/A");
							map.put("presentDistrictLgdCode",
									(p[39] != null && !p[39].toString().trim().isEmpty()) ? (String) p[39] : "N/A");

							return map;
						})

								.collect(Collectors.toList());

					}

					/* ==============SECC Data============= */
					seccData = repo.getCandidateSeccDetails(loginid);
					// System.out.println("seccData : " + seccData);
					if (seccData != null && seccData.size() != 0) {
						seccList = seccData.stream().map(p -> {
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("seccStateCode",
									(p[0] != null && !p[0].toString().trim().isEmpty()) ? (String) p[0] : "N/A");
							map.put("seccStateName",
									(p[1] != null && !p[1].toString().trim().isEmpty()) ? (String) p[1] : "N/A");
							map.put("seccDistrictCode",
									(p[2] != null && !p[2].toString().trim().isEmpty()) ? (String) p[2] : "N/A");
							map.put("seccDistrictName",
									(p[3] != null && !p[3].toString().trim().isEmpty()) ? (String) p[3] : "N/A");
							map.put("seccBlcokCode",
									(p[4] != null && !p[4].toString().trim().isEmpty()) ? (String) p[4] : "N/A");
							map.put("seccBlockName",
									(p[5] != null && !p[5].toString().trim().isEmpty()) ? (String) p[5] : "N/A");
							map.put("seccGPCode",
									(p[6] != null && !p[6].toString().trim().isEmpty()) ? (String) p[6] : "N/A");
							map.put("seccGPName",
									(p[7] != null && !p[7].toString().trim().isEmpty()) ? (String) p[7] : "N/A");
							map.put("seccVillageCode",
									(p[8] != null && !p[8].toString().trim().isEmpty()) ? (String) p[8] : "N/A");
							map.put("seccVillageName",
									(p[9] != null && !p[9].toString().trim().isEmpty()) ? (String) p[9] : "N/A");
							map.put("seccCandidateName",
									(p[10] != null && !p[10].toString().trim().isEmpty()) ? (String) p[10] : "N/A");
							map.put("seccAHLTIN",
									(p[11] != null && !p[11].toString().trim().isEmpty()) ? (String) p[11] : "N/A");
							map.put("seccLgdVillCode",
									(p[13] != null && !p[13].toString().trim().isEmpty()) ? (String) p[13] : "N/A");

							return map;
						})

								.collect(Collectors.toList());

					}

					/* ==============Education Data============= */
					educationData = repo.getCandidateEducationDetails(loginid);
					// System.out.println("educationData : " + educationData);
					if (educationData != null && educationData.size() != 0) {
						educationList = educationData.stream().map(p -> {
							Map<String, Object> map = new HashMap<String, Object>();

							// private String highestEducation;// remove
							// private String highestClass;
							// private String monthYearOfPassing;
							// private String isTechEducate;// remove
							// private int techQualification;
							// private int techEducationDomain;
							// private String monthYearOfPassingTechEdu; // remove
							// private String languageKnown;

							// TODO :Removing this code 0-- Abhishek
							// map.put("highesteducation",
							// (p[0] != null && !p[0].toString().trim().isEmpty()) ? p[0].toString() :
							// "N/A");

							// TODO :Removing this code 0-- Abhishek
							// map.put("monthYearOfPassing",
							// (p[1] != null && !p[1].toString().trim().isEmpty()) ? p[1].toString() :
							// "N/A");

							// map.put("passingTechYear",
							// (p[5] != null && !p[5].toString().trim().isEmpty()) ? p[5].toString() :
							// "N/A");

							// TODO :Removing this code 0-- Abhishek
							// map.put("isTechEducate",
							// (p[3] != null && !p[3].toString().trim().isEmpty()) ? p[3].toString() :
							// "N/A");

							// map.put("passingTechYear",
							// (p[5] != null && !p[5].toString().trim().isEmpty()) ? p[5].toString() :
							// "N/A");

							map.put("highesteducation",
									(p[0] != null && !p[0].toString().trim().isEmpty()) ? p[0].toString() : "");

							map.put("monthYearOfPassing",
									(p[1] != null && !p[1].toString().trim().isEmpty()) ? p[1].toString() : "N/A");

							map.put("language",
									(p[2] != null && !p[2].toString().trim().isEmpty()) ? p[2].toString() : "N/A");

							map.put("techQualificationId",
									(p[4] != null && !p[4].toString().trim().isEmpty()) ? p[4].toString() : "N/A");
							// map.put("monthYearOfPassing",
							// (p[5] != null && !p[5].toString().trim().isEmpty()) ? p[5].toString() :
							// "N/A");

							map.put("techDomainId",
									(p[6] != null && !p[6].toString().trim().isEmpty()) ? p[6].toString() : "N/A");
							map.put("techQualification",
									(p[8] != null && !p[8].toString().trim().isEmpty()) ? p[8].toString() : "N/A");
							map.put("techDomain",
									(p[9] != null && !p[9].toString().trim().isEmpty()) ? p[9].toString() : "N/A");
							map.put("highestClass",
									(p[10] != null && !p[10].toString().trim().isEmpty()) ? p[10].toString() : "N/A");
							return map;
						}).collect(Collectors.toList());

					}

					/* ==============Employee Data============= */
					employeeData = repo.getCandidateEmployeeDetails(loginid);
					// System.out.println("employeeData : " + employeeData);
					if (employeeData != null && employeeData.size() != 0) {
						employeeList = employeeData.stream().map(p -> {
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("isEmployeed",
									(p[0] != null && !p[0].toString().trim().isEmpty()) ? (String) p[0] : "N/A");
							map.put("empNature",
									(p[1] != null && !p[1].toString().trim().isEmpty()) ? (String) p[1] : "N/A");
							// map.put("intrestedIn",
							// (p[2] != null && !p[2].toString().trim().isEmpty()) ? (String) p[2] : "N/A");
							map.put("empPreference",
									(p[3] != null && !p[3].toString().trim().isEmpty()) ? (String) p[3] : "N/A");
							map.put("preferJobLocation",
									(p[4] != null && !p[4].toString().trim().isEmpty()) ? (String) p[4] : "N/A");
							map.put("monthlyEarning",
									(p[5] != null && !p[5].toString().trim().isEmpty()) ? p[5].toString() : "N/A");
							map.put("expectedSalary",
									(p[6] != null && !p[6].toString().trim().isEmpty()) ? p[6].toString() : "N/A");

							return map;
						})

								.collect(Collectors.toList());

					}

					/* ==============Training Data============= */
					trainingData = repo.getCandidateTrainingDetails(loginid);
					// System.out.println("trainingData : " + trainingData);
					if (trainingData != null && trainingData.size() != 0) {
						trainingList = trainingData.stream().map(p -> {
//							Map<String, Object> map = new HashMap<String, Object>();
//							map.put("isPreTraining",
//									(p[0] != null && !p[0].toString().trim().isEmpty()) ? (String) p[0] : "N/A");
//							map.put("preCompTraining",
//									(p[1] != null && !p[1].toString().trim().isEmpty()) ? (String) p[1] : "N/A");
//							map.put("compTrainingDuration",
//									(p[2] != null && !p[2].toString().trim().isEmpty()) ? (String) p[2] : "N/A");
//							// map.put("hearedAboutScheme",
//							//            p[3] != null && !p[3].toString().trim().isEmpty()) ? (String) p[3] : "N/A");
//							// map.put("hearedFrom",
//							//									(p[4] != null && !p[4].toString().trim().isEmpty()) ? (String) p[4] : "N/A");
//							map.put("sectorId",
//									(p[5] != null && !p[5].toString().trim().isEmpty()) ? (String) p[5] : "N/A");
//							map.put("sectorName",
//									(p[6] != null && !p[6].toString().trim().isEmpty()) ? (String) p[6] : "N/A");
//							map.put("trade",
//									(p[7] != null && !p[7].toString().trim().isEmpty()) ? (String) p[7] : "N/A");
//							//							
//							map.put("schemeType",
//									(p[8] != null && !p[8].toString().trim().isEmpty()) ? (String) p[8] : "N/A");
//
//							return map;

							Map<String, Object> map = new HashMap<>();
							map.put("isPreTraining",
									(p[0] != null && !p[0].toString().trim().isEmpty()) ? p[0].toString() : "N/A");

							map.put("preCompTraining",
									(p[1] != null && !p[1].toString().trim().isEmpty()) ? p[1].toString() : "N/A");

							map.put("compTrainingDuration",
									(p[2] != null && !p[2].toString().trim().isEmpty()) ? p[2].toString() : "N/A");

//							map.put("hearedAboutScheme",
//							        (p[3] != null && !p[3].toString().trim().isEmpty()) ? p[3].toString() : "N/A");

//							map.put("hearedFrom",
//							        (p[4] != null && !p[4].toString().trim().isEmpty()) ? p[4].toString() : "N/A");

							map.put("sectorId",
									(p[5] != null && !p[5].toString().trim().isEmpty()) ? p[5].toString() : "N/A");

							map.put("sectorName",
									(p[6] != null && !p[6].toString().trim().isEmpty()) ? p[6].toString() : "N/A");

							map.put("trade",
									(p[7] != null && !p[7].toString().trim().isEmpty()) ? p[7].toString() : "N/A");

							map.put("schemeType",
									(p[8] != null && !p[8].toString().trim().isEmpty()) ? p[8].toString() : "");

							return map;

						})

								.collect(Collectors.toList());

					}

					/* ==============Banking Data============= */
					bankingData = repo.getCandidateBankingDetails(loginid);
					// System.out.println("bankingData : " + bankingData);
					if (bankingData != null && bankingData.size() != 0) {
						bankingList = bankingData.stream().map(p -> {
							Map<String, Object> map = new HashMap<String, Object>();

							Short bankCodeSh = (Short) p[0];
							String bankCode = bankCodeSh.toString();
							String bankName = (String) p[1];
							int bankBranchCodeInt = (Integer) p[2];
							String bankBranchCode = String.valueOf(bankBranchCodeInt);
							String bankBranchName = (String) p[3];
							String bankAccNumber = (String) p[4];
							String ifscCode = (String) p[5];
							String panNo = (String) p[6];
							try {
								if (bankCode != null && bankCode != "") {
									String encBankCode = enc.encrypt(bankCode);
									map.put("bankCode", encBankCode);
								} else {
									map.put("bankCode",
											(p[0] != null && !p[0].toString().trim().isEmpty()) ? (String) p[0]
													: "N/A");
								}

								if (bankName != null && bankName != "") {
									String encbankName = enc.encrypt(bankName);
									map.put("bankName", encbankName);
								} else {
									map.put("bankName",
											(p[1] != null && !p[1].toString().trim().isEmpty()) ? (String) p[1]
													: "N/A");
								}

								if (bankBranchCode != null && bankBranchCode != "") {
									String encbankBranchCode = enc.encrypt(bankBranchCode);
									map.put("bankBranchCode", encbankBranchCode);
								} else {
									map.put("bankBranchCode",
											(p[2] != null && !p[2].toString().trim().isEmpty()) ? (String) p[2]
													: "N/A");
								}

								if (bankBranchName != null && bankBranchName != "") {
									String encbankBranchName = enc.encrypt(bankBranchName);
									map.put("bankBranchName", encbankBranchName);
								} else {
									map.put("bankBranchName",
											(p[3] != null && !p[3].toString().trim().isEmpty()) ? (String) p[3]
													: "N/A");
								}

								if (bankAccNumber != null && bankAccNumber != "") {
									map.put("bankAccNumber", bankAccNumber);
								} else {
									map.put("bankAccNumber",
											(p[4] != null && !p[4].toString().trim().isEmpty()) ? (String) p[4]
													: "N/A");
								}

								if (ifscCode != null && ifscCode != "") {
									String encifscCode = enc.encrypt(ifscCode);
									map.put("ifscCode", encifscCode);
								} else {
									map.put("ifscCode",
											(p[5] != null && !p[5].toString().trim().isEmpty()) ? (String) p[5]
													: "N/A");
								}

								if (panNo != null && panNo != "") {
									map.put("panNo", panNo);
								} else {
									map.put("panNo", (p[6] != null && !p[6].toString().trim().isEmpty()) ? (String) p[6]
											: "N/A");
								}

							} catch (Exception e) {
								e.printStackTrace();
							}

							return map;
						})

								.collect(Collectors.toList());

					}

					map1.put("personaldetails", personalList);
					wrappedList.add(map1);
					map2.put("addressDetails", addressList);
					addressWrappedList.add(map2);

					response.setPersonalList(wrappedList);
					response.setAddressList(addressWrappedList);
					response.setSeccList(seccList);
					response.setEducationalList(educationList);
					response.setEmployementList(employeeList);
					response.setTrainingList(trainingList);
					response.setBankList(bankingList);
					response.setResponseCode(HttpStatus.OK.value());
					response.setResponseDesc(HttpStatus.OK.name());

				} else {
					response.setResponseCode(301);
					response.setResponseDesc("Please upgread your app first.");
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
		secure.setSecurityHeaders(res);
		return response;

	}

	public Response<Map<String, Object>> changeProfileImage(UserDetailsBean user, HttpServletRequest req,
			HttpServletResponse res) {
		Response<Map<String, Object>> response = new Response<>();
		String loginId = user.getLoginId();

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
				String appVersion = user.getAppVersion();
				String status = otpRepo.appVersionStatus(appVersion);
				String stateName = profileRepo.getUserState(loginId);
				CandidateAttachment existAttach = attachRepo.getAttachmentByLoginId(loginId);

				// Check if the app version is active
				if (status != null && status.equals("Active") && !status.equals("null")) {

					String profileImage = user.getAadharPhoto();
					if (profileImage != null && profileImage != "") {
						if (existAttach.getAadharImage() != null) {
							CandidateAttachment attachement = new CandidateAttachment();
							File uploadsDir = null;
							String fileExtension = null;

							byte[] profileBytes = Base64.getDecoder().decode(profileImage);
							String fileType = MyConstants.detectFileType(profileBytes);
							if (fileType == "4044") {
								response.setResponseCode(210);
								response.setResponseDesc("File format not supported");
								return response;
							}
							MultipartFile profileFile = MyConstants.convertToMultipartFile(profileBytes, fileType);

							if (profileImage != null && profileImage != "") {

								UserDetails instance = repo.getByLoginId(loginId);
								uploadsDir = new File(MyConstants.UPLOAD_ADHAAR + stateName);
								if (!uploadsDir.exists()) {
									uploadsDir.mkdir();
								}

								fileExtension = MyConstants.getFileExtension(profileFile.getOriginalFilename());
								attachement.setAadharImage("_adhaar_card." + fileExtension);
								// attachement.setAadharImage(profileFilePath);
								attachement.setLoginId(loginId);
								attachement.setCreatedOn(currentTimestamp);
								attachement.setCreatedBy(instance.getUserName());
								attachement.setUpdatedBy(instance.getUserName());
								attachement.setUpdatedOn(currentTimestamp);
								attachRepo.save(attachement);

								profileFile.transferTo(new File(uploadsDir + File.separator
										+ attachement.getAttachmentId() + "_adhaar_card." + fileExtension));
							}
						} else {
							String fileExtensionup = null;
							byte[] profileBytes = Base64.getDecoder().decode(profileImage);
							String fileType = MyConstants.detectFileType(profileBytes);
							if (fileType == "4044") {
								response.setResponseCode(210);
								response.setResponseDesc("File format not supported");
								return response;
							}
							MultipartFile profileFile = MyConstants.convertToMultipartFile(profileBytes, fileType);

							if (profileImage != null && profileImage != "") {
								File uploadsDir = new File(MyConstants.UPLOAD_ADHAAR + stateName);
								if (!uploadsDir.exists()) {
									uploadsDir.mkdir();
								}

								fileExtensionup = MyConstants.getFileExtension(profileFile.getOriginalFilename());
								existAttach.setAadharImage("_adhaar_card." + fileExtensionup);
								attachRepo.save(existAttach);

								profileFile.transferTo(new File(uploadsDir + File.separator
										+ existAttach.getAttachmentId() + "_adhaar_card." + fileExtensionup));

								/*
								 * profileFilePath = uploadsDir + File.separator + loginId + "_" +
								 * MyConstants.randomNo() + profileFile.getOriginalFilename(); path =
								 * Paths.get(profileFilePath); if (Files.exists(path)) { String profileFileName
								 * = loginId + "_" + MyConstants.randomNo() + profileFile.getOriginalFilename();
								 * profileFilePath = uploadsDir + File.separator + profileFileName; path =
								 * Paths.get(profileFilePath); } Files.copy(profileFile.getInputStream(), path);
								 */
							}
						}
					}

					response.setResponseMsg("Profile Photo upload sucessfully");
					response.setResponseCode(HttpStatus.OK.value());
					response.setResponseDesc(HttpStatus.OK.name());

				} else {
					// Outdated app version
					response.setResponseCode(301);
					response.setResponseDesc("Please upgrade your app first.");
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
		secure.setSecurityHeaders(res);
		return response;
	}

	public OtpResponse<Map<String, Object>> getUserLoginIdPassword(UserPassword pass, HttpServletRequest req,
			HttpServletResponse res) {
		OtpResponse<Map<String, Object>> response = new OtpResponse<>();
		try {
			SMSBean smsBean = new SMSBean();
			SendSMSTest sendSMS;
			String msg = "";
			sendSMS = new SendSMSTest();
			String appVersion = pass.getAppVersion();
			String status = otpRepo.appVersionStatus(appVersion);
			Timestamp currentTimestamp = Timestamp.from(Instant.now());

			if (status != null && status.equals("Active") && !status.equals("null")) {

				Validator validator = new Validator();

				String mobileNo = pass.getMobileNo();
				String emailId = pass.getEmail();
				String loginId = pass.getLoginId();

				if (!validator.isMobileValid(mobileNo)) {
					response.setResponseCode(201);
					response.setResponseDesc("Invalid mobile number format");
					return response;
				}

				if (!validator.isEmailValid(emailId)) {
					response.setResponseCode(201);
					response.setResponseDesc("Invalid email Id!");
					return response;
				}

				// String loginId = profileRepo.getUserLoginIdPassword(mobileNo, emailId);

				UserDetails userInstance = repo.getByLoginId(loginId);

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
				repo.save(userInstance);
				// System.out.println("change password : " + password);

				String sms = "Please note your Login Id " + loginId + " and Password " + password
						+ " for your future Login.";
				smsBean.setMobile(pass.getMobileNo());
				smsBean.setUnicodeMsg(true);
				smsBean.setSms(sms);
				smsBean.setTemplateId("1007040091173547449");

				sendSMS.sendSMS(smsBean);

				msg = "<html xmlns=\"http://www.w3.org/1999/xhtml\" xmlns=\"http://www.w3.org/1999/xhtml\"> "
						+ "<head> " + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" /> "
						+ "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" /> "
						+ "<title>Candidate Login Id and Password</title> " + "</head> "
						+ "<body style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box;width: 100% !important;height: 100%;margin: 0;line-height: 1.4;background-color: #F2F4F6;color: #74787E;-webkit-text-size-adjust: none\"> "
						+ "<span class=\"preheader\" style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box;display: none !important;visibility: hidden;mso-hide: all;font-size: 1px;line-height: 1px;max-height: 0;max-width: 0;opacity: 0;overflow: hidden\">Login Id and Password for Kaushal Panjee mobile app</span> <table class=\"email-wrapper\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box;width: 100%;margin: 0;padding: 0;-premailer-width: 100%;-premailer-cellpadding: 0;-premailer-cellspacing: 0;background-color: #F2F4F6\"> "
						+ "<tr style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box\"> "
						+ "<td align=\"center\" style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box;word-break: break-word\"> "
						+ "<table class=\"email-content\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box;width: 100%;margin: 0;padding: 0;-premailer-width: 100%;-premailer-cellpadding: 0;-premailer-cellspacing: 0\"> "
						+ "<tr style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box\"> "
						+ "<td class=\"email-masthead\" style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box;word-break: break-word;padding: 5px 0\"><a href=\"https://kaushal.rural.gov.in\" class=\"email-masthead_name\" style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box;color: #5e769c;font-size: 18px;line-height: 3;margin-left: 22px !important;font-weight: 500;text-decoration: none;text-shadow: 0 1px 0 white\"> Kaushal Panjee App </a> </td> </tr> <!-- Email Body --> "
						+ "<tr style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box\"> <td class=\"email-body\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box;word-break: break-word;width: 100%;margin: 0;padding: 0;-premailer-width: 100%;-premailer-cellpadding: 0;-premailer-cellspacing: 0;border-top: 1px solid #EDEFF2;border-bottom: 1px solid #EDEFF2;background-color: #FFF\"> <table class=\"email-body_inner\" align=\"center\" width=\"570\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box;width: 570px;margin: 0 auto;padding: 0;-premailer-width: 570px;-premailer-cellpadding: 0;-premailer-cellspacing: 0;background-color: #FFF\"> <!-- Body content --> <tr style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box\"> <td class=\"content-cell\" style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box;word-break: break-word;padding: 35px\"> <h1 style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box;margin-top: 0;color: #2F3133;font-size: 19px;font-weight: bold;text-align: left\">Dear User,</h1> <p style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box;line-height: 1.5em;text-align: left;margin-top: 0;color: #74787E;font-size: 16px\">Please note your Login Id (Kaushal Panjee ID)<b> "
						+ loginId + " </b>and Password <b>" + password
						+ "</b> for your future Login on Kaushal Panjee App. <strong style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box\"></strong></p> <!-- Action --> "
						+ "<p style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box;line-height: 1.5em;text-align: left;margin-top: 0;color: #74787E;font-size: 16px\">For your security, do not share your login id and password with anyone. If you did not initiate this registration request, please ignore this email and report to <b style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box\">grameen.kaushal@nic.in</b></p> <p style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box;line-height: 1.5em;text-align: left;margin-top: 0;color: #74787E;font-size: 16px\">Thanks, <br style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box\" />Grameen Kaushal Team</p> <!-- Sub copy --> </td> </tr> </table> </td> </tr> <tr style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box\"> <td style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box;word-break: break-word\"> <table class=\"email-footer\" align=\"center\" width=\"570\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box;width: 570px;margin: 0 auto;padding: 0;-premailer-width: 570px;-premailer-cellpadding: 0;-premailer-cellspacing: 0;text-align: center\"> <tr style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box\"> <td class=\"content-cell\" align=\"center\" style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box;word-break: break-word;padding: 35px\"> <p class=\"sub align-center\" style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box;line-height: 1.5em;text-align: left;color: #AEAEAE;margin-top: 0;font-size: 12px\"></p> <p class=\"sub align-center\" style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box;line-height: 1.5em;text-align: left;color: #AEAEAE;margin-top: 0;font-size: 12px\"> Grameen Kaushalya Yojana (GKY) </p> </td> </tr> </table> </td> </tr> </table> </td> </tr> </table> </body></html>"
						+ "				  ";

				SendMail sendmail = new SendMail();
				if (emailId != null && !emailId.equalsIgnoreCase("")) {

					sendmail.sendMail(emailId, "Reset Kaushal Panjee Id and Password for Kaushal panjee App", msg);
				}

				response.setResponseCode(HttpStatus.OK.value());
				response.setResponseDesc("Your Login Id and password has been sent your registered mobile number.");

			} else {
				response.setResponseCode(301);
				response.setResponseDesc("Please upgread your app first.");
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

	public Response<Map<String, Object>> checkUserExistance(UserPassword pass, HttpServletRequest req,
			HttpServletResponse res) {
		Response<Map<String, Object>> response = new Response<>();
		try {
			String appVersion = pass.getAppVersion();
			String status = otpRepo.appVersionStatus(appVersion);

			if (status != null && status.equals("Active") && !status.equals("null")) {
				String aadhaarNumber = enc.decrypt(pass.getUserInput());
				String encAadhaarNumber = enc.encryptNew(aadhaarNumber);

				boolean aadhaarFlag = adhaarRepo.isAadhaarExistOrNot(encAadhaarNumber);
				if (aadhaarFlag) {
					// System.out.println("Adhaar already Exist");
					response.setResponseCode(333);
					response.setResponseDesc(
							"User has been already registered with this aadhaar number, please register with different aadhaar number.");

				} else {
					// System.out.println("Adhaar not Exist" + aadhaarFlag + " : " +
					// pass.getUserInput());
					response.setResponseCode(HttpStatus.OK.value());
					response.setResponseDesc(HttpStatus.OK.name());
				}

			} else {
				response.setResponseCode(301);
				response.setResponseDesc("Please upgread your app first.");
				response.setResponseMsg("Outdated app version.");
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

	public Response<Map<String, Object>> updateUserAadhaarSHA() {
		Response<Map<String, Object>> response = new Response<>();
		List<Object[]> userList = null;

		try {

			userList = adhaarRepo.getUserAddharList();
			if (userList != null && userList.size() != 0) {
				for (int i = 0; i < userList.size(); i++) {
					Object[] userData = userList.get(i);
					String loginId = (String) userData[0];
					String adhaarNo = (String) userData[1];

					String encAadhaar = enc.decryptNew(adhaarNo);

					// System.out.println("Aadhaar Number : " + encAadhaar);

					String hashAadhaar = enc.hash(encAadhaar);

					// System.out.println("Hash Aadhaar Number : " + hashAadhaar);

					UserAdhaarDetails obj = adhaarRepo.getAdhaarInstanceByLogin(loginId);

					obj.setAadharSHANo(hashAadhaar);
					obj.setSchemeFlag("N");
					obj.setSchemeId((long) 0);

					adhaarRepo.save(obj);
					response.setResponseCode(HttpStatus.OK.value());
					response.setResponseDesc(HttpStatus.OK.name());
				}

			}

		} catch (HandledException e) {
			e.printStackTrace();
			throw new HandledException(e.getCode(), e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new HandledException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
		}
		return response;
	}

	public Response<Map<String, Object>> updateFaceRegistred(UserPassword request, HttpServletRequest req,
			HttpServletResponse res) {
		Response<Map<String, Object>> response = new Response<>();
		String loginId = request.getLoginId();
		try {
			String appVersion = request.getAppVersion();
			String status = otpRepo.appVersionStatus(appVersion);

			if (status != null && status.equals("Active") && !status.equals("null")) {
				UserAdhaarDetails adhaarInstance = adhaarRepo.getAdhaarInstanceByLogin(loginId);

				adhaarInstance.setIsFaceRegistered(request.getIsFaceRegistered());
				adhaarRepo.save(adhaarInstance);

				response.setResponseCode(HttpStatus.OK.value());
				response.setResponseDesc(HttpStatus.OK.name());
				response.setResponseMsg("Face registration successfully updated.");

			} else {
				response.setResponseCode(301);
				response.setResponseDesc("Please upgread your app first.");
				response.setResponseMsg("Outdated app version.");
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

	public Response<Map<String, Object>> updateUserAadhaarDetails(UserCreationBean user, HttpServletRequest req,
			HttpServletResponse res) {
		Response<Map<String, Object>> response = new Response<>();
		String loginId = user.getLoginId();
		String stateName = "";

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
				String appVersion = user.getAppVersion();
				String status = otpRepo.appVersionStatus(appVersion);

				// Check if the app version is active
				if (status != null && status.equals("Active") && !status.equals("null")) {
					UserAdhaarDetails adhInstance = adhaarRepo.getAdhaarInstanceByLogin(loginId);
					CandidateAttachment attachement = attachRepo.getAttachmentByLoginId(loginId);

					if (user.getCandidateName() != null && user.getCandidateName() != "") {
						adhInstance.setCandidateName(enc.decrypt(user.getCandidateName()));
					}

					if (user.getCareOf() != null && user.getCareOf() != "") {
						adhInstance.setCareOf(enc.decrypt(user.getCareOf().replace("\n", "")));
					}

					if (user.getGender() != null && user.getGender() != "") {
						adhInstance.setGender(enc.decrypt(user.getGender()));
					}

					if (user.getDateOfBirth() != null && user.getDateOfBirth() != "") {
						String dateOfBirth = enc.decrypt(user.getDateOfBirth());
						DateTimeFormatter dtformatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
						LocalDate timestamp = LocalDate.parse(dateOfBirth, dtformatter);
						adhInstance.setDateOfBirth(timestamp);
					}

					if (user.getStateName() != null && user.getStateName() != "") {
						stateName = enc.decrypt(user.getStateName());
						adhInstance.setAadharStateName(stateName);
					}

					if (user.getDistrictName() != null && user.getDistrictName() != "") {
						adhInstance.setAadharDistrictName(enc.decrypt(user.getDistrictName()));
					}

					if (user.getBlockName() != null && user.getBlockName() != "") {
						adhInstance.setAadharBlockName(enc.decrypt(user.getBlockName()));
					}

					if (user.getPostOffice() != null && user.getPostOffice() != "") {
						adhInstance.setAadharPostOffice(enc.decrypt(user.getPostOffice().replace("\n", "")));
					}

					if (user.getVillage() != null && user.getVillage() != "") {
						adhInstance.setAadharVillageName(enc.decrypt(user.getVillage().replace("\n", "")));
					}

					if (user.getStreet() != null && user.getStreet() != "") {
						adhInstance.setAadharLocation(enc.decrypt(user.getStreet().replace("\n", "")));
					}

					if (user.getPinCode() != null && user.getPinCode() != "") {
						adhInstance.setAadharPinCode(enc.decrypt(user.getPinCode()));
					}

					adhInstance.setUpdatedOn(currentTimestamp);
					adhInstance.setUpdatedBy(loginId);

					adhaarRepo.save(adhInstance);

					/* ==========================Save adhaar Image===================== */

					String imageData = user.getAadharImage();
					byte[] decodeBytes = Base64.getDecoder().decode(imageData);
					String fileType = MyConstants.detectFileType(decodeBytes);

					if (fileType.equals("4044")) {
						response.setResponseCode(210);
						response.setResponseDesc("File format not supported");
						return response;
					}
					MultipartFile file = MyConstants.convertToMultipartFile(decodeBytes, fileType);
					File uploadsDir = null;
					String fileExtension = null;
					if (file != null) {
						uploadsDir = new File(MyConstants.UPLOAD_ADHAAR + stateName);
						if (!uploadsDir.exists()) {
							uploadsDir.mkdir();
						}
						fileExtension = MyConstants.getFileExtension(file.getOriginalFilename());
						attachement.setAadharImage("_adhaar_card." + fileExtension);
						attachement.setUpdatedOn(currentTimestamp);
						attachement.setUpdatedBy(loginId);

					} else {
						response.setWrappedList(null);
						response.setResponseMsg("Pleae upload the image");
						response.setResponseCode(201);
						response.setResponseDesc("File not found");
						return response;
					}

					attachRepo.save(attachement);

					file.transferTo(new File(uploadsDir + File.separator + attachement.getAttachmentId()
							+ "_adhaar_card." + fileExtension));

					/* ===================================================================== */

					response.setResponseMsg("Candidate aadhaar details has been updated sucessfully");
					response.setResponseCode(HttpStatus.OK.value());
					response.setResponseDesc(HttpStatus.OK.name());

				} else {
					// Outdated app version
					response.setResponseCode(301);
					response.setResponseDesc("Please upgrade your app first.");
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
		secure.setSecurityHeaders(res);
		return response;
	}

	public Response<Map<String, Object>> updateCandidateEmailId(UserCreationBean user, HttpServletRequest req,
			HttpServletResponse res) {
		Response<Map<String, Object>> response = new Response<>();
		String loginId = user.getLoginId();

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
				String appVersion = user.getAppVersion();
				String status = otpRepo.appVersionStatus(appVersion);

				// Check if the app version is active
				if (status != null && status.equals("Active") && !status.equals("null")) {
					UserAdhaarDetails adhInstance = adhaarRepo.getAdhaarInstanceByLogin(loginId);
					if (user.getEmail() != null && user.getEmail() != "") {

						adhInstance.setEmailId(user.getEmail());

					} else {
						adhInstance.setEmailId(user.getEmail());
					}

					adhInstance.setUpdatedOn(currentTimestamp);
					adhInstance.setUpdatedBy(loginId);

					adhaarRepo.save(adhInstance);

					response.setResponseMsg("Candidate email Id has been updated sucessfully");
					response.setResponseCode(HttpStatus.OK.value());
					response.setResponseDesc(HttpStatus.OK.name());

				} else {
					// Outdated app version
					response.setResponseCode(301);
					response.setResponseDesc("Please upgrade your app first.");
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
		secure.setSecurityHeaders(res);
		return response;

	}

	public Response<Map<String, Object>> userLogOut(UserDeviceBean login, HttpServletRequest req,
			HttpServletResponse res) {
		Response<Map<String, Object>> response = new Response<>();
		String loginId = login.getLoginId();
		try {
			String appVersion = login.getAppVersion();
			String status = otpRepo.appVersionStatus(appVersion);

			if (status != null && status.equals("Active") && !status.equals("null")) {
				AuthenticationEntity authinstance = authRepo.getAuthInstance(loginId);
				if (authinstance != null) {
					authRepo.delete(authinstance);
					response.setResponseMsg("You are logged out Successfully.");
					response.setResponseCode(HttpStatus.OK.value());
					response.setResponseDesc(HttpStatus.OK.name());
				} else {
					response.setResponseMsg("Something went wrong. Please try again.");
					response.setResponseCode(HttpStatus.NOT_FOUND.value());
					response.setResponseDesc(HttpStatus.NOT_FOUND.name());
				}
			} else {
				response.setResponseCode(301);
				response.setResponseDesc("Please upgread your app first.");
				response.setResponseMsg("Outdated app version.");
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

	public BankDetailsResponse<Map<String, Object>> getBankList(BankRequest request, HttpServletRequest req,
			HttpServletResponse res) {

		BankDetailsResponse<Map<String, Object>> response = new BankDetailsResponse<>();
		List<Map<String, Object>> bankList = new ArrayList<>();

		String loginId = request.getLoginId();
		Timestamp currentTimestamp = Timestamp.from(Instant.now());

//  Step 1: Auth Instance Check
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
			authToken = authHeader.substring(7);
		}

		boolean authFlag = authRepo.getTokenValidation(loginId, authToken);

		try {
			if (authFlag) {

				String appVersion = request.getAppVersion();
				String status = otpRepo.appVersionStatus(appVersion);

				if (status != null && status.equals("Active") && !status.equals("null")) {

					List<Object[]> banks = bankRepo.getBankListByLoginId(loginId);

					if (banks != null && !banks.isEmpty()) {

						bankList = banks.stream().map(p -> {
							Map<String, Object> map = new HashMap<>();

							map.put("accountNumber", p[0]);
							map.put("ifscCode", p[1]);
							map.put("panNo", p[2]);
							map.put("bankCode", p[3]);
							map.put("bankName", p[4]);

							return map;
						}).collect(Collectors.toList());

						response.setData(bankList);
						response.setResponseCode(HttpStatus.OK.value());
						response.setResponseDesc(HttpStatus.OK.name());
						response.setResponseMsg("");

					} else {
						response.setData(null);
						response.setResponseCode(303);
						response.setResponseDesc("No Data");
						response.setResponseMsg("Bank details not found.");
					}

				} else {
					response.setResponseCode(301);
					response.setResponseDesc("Please upgrade your app first.");
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

		secure.setSecurityHeaders(res);
		return response;
	}

	@Override
	public Response<Map<String, Object>> saveAadhaarTxn(AadhaarTxnRequest request, HttpServletResponse res) {

		Response<Map<String, Object>> response = new Response<>();

		try {
			Timestamp currentTimestamp = Timestamp.from(Instant.now());

			AadhaarTransactionLogEntity obj = new AadhaarTransactionLogEntity();

			if (hasText(request.getTxnAadhaar())) {
			    obj.setAadhaarTxnId(enc.encryptNew(request.getTxnAadhaar()));
			}

			if (hasText(request.getTxnApp())) {
			    obj.setAppTxnId(enc.encryptNew(request.getTxnApp()));
			}

			if (hasText(request.getAadhaarCode())) {
			    obj.setAadhaarCode(enc.encryptNew(request.getAadhaarCode()));
			}

			obj.setStatus(request.getRet());
			obj.setCreatedOn(currentTimestamp);

			aadhaarLogRepo.save(obj);

			response.setResponseCode(HttpStatus.OK.value());
			response.setResponseDesc(HttpStatus.OK.name());
			response.setResponseMsg("Aadhaar transaction saved successfully");

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Internal Server Error");
		}

		secure.setSecurityHeaders(res);
		return response;
	}
	
	private boolean hasText(String s) {
	    return s != null && !s.trim().isEmpty();
	}

}
