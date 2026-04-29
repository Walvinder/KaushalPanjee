package com.nic.KaushalPanjeeApp.otp;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.nic.KaushalPanjeeApp.config.EncryptionUtil;
import com.nic.KaushalPanjeeApp.config.SMSBean;
import com.nic.KaushalPanjeeApp.config.SecurityHeader;
import com.nic.KaushalPanjeeApp.config.SendMail;
import com.nic.KaushalPanjeeApp.config.SendSMSTest;
import com.nic.KaushalPanjeeApp.config.Validator;
import com.nic.KaushalPanjeeApp.helper.AuthResponse;
import com.nic.KaushalPanjeeApp.helper.AuthTokenBean;
import com.nic.KaushalPanjeeApp.helper.HandledException;
import com.nic.KaushalPanjeeApp.helper.MyConstants;
import com.nic.KaushalPanjeeApp.helper.OtpBean;
import com.nic.KaushalPanjeeApp.helper.OtpResponse;
import com.nic.KaushalPanjeeApp.helper.UserPassword;
import com.nic.KaushalPanjeeApp.userdetails.UserAdhaarDetailsRepository;
import com.nic.KaushalPanjeeApp.userdetails.UserDetails;
import com.nic.KaushalPanjeeApp.userdetails.UserDetailsRepository;
import com.nic.KaushalPanjeeApp.userdetails.UserProfileRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

@Service
public class OtpServiceImpl implements OtpService {
	@Autowired
	OtpRepository repo;

	@Autowired
	UserDetailsRepository userRepo;

	@Autowired
	UserProfileRepository userPrRepo;

	@Autowired
	private EncryptionUtil enc;

	@Autowired
	AuthenticationRepository authrepo;

	@Autowired
	SecurityHeader bnkService;

	@Autowired
	UserAdhaarDetailsRepository adhaRepo;

	/*
	 * public static void main(String[] args) throws Exception { EncryptionUtil encr
	 * = new EncryptionUtil(); String adhaarNo = "8gbXisVCLiNNUZK2tslpkA==";
	 * System.out.println("Decrypted Adhaar : " + encr.decrypt(adhaarNo)); String
	 * blockName = "Patan"; String candidateName = "Laxmi Kumari"; String careOf =
	 * "D/O: Jitendra Ram"; String dateOfBirth = "05-03-2005"; String districtName =
	 * "Palamu"; String email = "laxmigaharpathra56@gmail.com"; String gender = "F";
	 * String mobileNo = "9304775300"; String pinCode = "822123"; String postOffice
	 * = "Gahar Pathra"; String stateCode = "34"; String stateLgdCode = "20"; String
	 * stateName = "Jharkhand"; String street =
	 * "suFQk0F2hhYZieRFNUXKCfPPMF82NLKPwRafmSxdFHiNn+6+OTHnMdvj1+b6ZwNHIbONecZzBWyh\ninp5B7lOEQ=="
	 * .replace("\n", ""); String village = "Gahar Pathra";
	 * 
	 * System.out.println("Adhar number : " + encr.encrypt(adhaarNo));
	 * System.out.println("Adhar number decript: " +
	 * encr.decrypt("GJNlCtGTL+8JiL6jjKW1Ig==")); System.out.println("blockName : "
	 * + encr.decrypt("7TJGXR8g3R7ioAIjzuno9g=="));
	 * System.out.println("candidateName : " +
	 * encr.decrypt("TdCr/DlZT7RNbuXJtLLYQQ==")); System.out.println("careOf : " +
	 * encr.decrypt("5rfzAgWuNfR+Q42cG3G/wNAskGjRCiKvVJp05olR1MA="));
	 * System.out.println("dateOfBirth : " +
	 * encr.decrypt("6NGBZiaxRNQoRz2nn0LEIA=="));
	 * System.out.println("districtName : " +
	 * encr.decrypt("eF4WjYykAdGeqFBvhljsag==")); System.out.println("email : " +
	 * encr.decrypt("7TJGXR8g3R7ioAIjzuno9g==")); System.out.println("gender : " +
	 * encr.decrypt("h6l7faFlqa+95SQuti2EPg==")); System.out.println("mobileNo : " +
	 * encr.decrypt("AU8W+fS4Tfbv3QBZouGIoA==")); System.out.println("pinCode : " +
	 * encr.decrypt("PK8WdGEPA1oAmwK9D2ODWQ==")); System.out.println("postOffice : "
	 * + encr.decrypt("lGlpMz5Te2G0+SotyU87Jw=="));
	 * System.out.println("stateCode : " +
	 * encr.decrypt("JP7zEXxym1lc4nFh5rgmcA=="));
	 * System.out.println("stateLgdCode : " +
	 * encr.decrypt("clnkoV8TAkR/DFTzC2leNQ==")); System.out.println("stateName : "
	 * + encr.decrypt("KQHC/A6U3e6hcPEbm0jAcg==")); System.out.println("street : " +
	 * encr.decrypt(street)); System.out.println("village : " +
	 * encr.decrypt("0zrvZosMYPl6kwEWI7nepg==")); }
	 */

	public static String generateSecureRandomString(int length) {
		StringBuilder result = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			int index = MyConstants.random.nextInt(MyConstants.ALPHABET.length());
			result.append(MyConstants.ALPHABET.charAt(index));
		}
		return result.toString();
	}

	public static boolean isPasswordValid(final String password) {
		Matcher matcher = MyConstants.pattern.matcher(password);
		return matcher.matches();
	}

	/*
	 * To generate OTP for mobile number verification
	 */

	@SuppressWarnings("unused")
	private String safeString(Map<String, Object> map, String key, String defaultValue) {
		return map != null && map.containsKey(key) && map.get(key) != null ? map.get(key).toString() : defaultValue;
	}

	@Override
	@SuppressWarnings("unused")
	public OtpResponse<Map<String, Object>> otpGenerationForMobile(OtpBean otp, HttpServletRequest req,
			HttpServletResponse res) {
		OtpResponse<Map<String, Object>> response = new OtpResponse<>();
		Timestamp currentTimestamp = Timestamp.from(Instant.now());
		try {

			String appVersion = otp.getAppVersion();
			String status = repo.appVersionStatus(appVersion);
			String msg = "";
			String sms = "";
			String smsVar = "verifying mobile number";
			SendSMSTest sendSMS;
			sendSMS = new SendSMSTest();
			String encodedSMS = sms;
			SMSBean smsBean = new SMSBean();
			String decMobile = otp.getMobileNo();
			// System.out.println("Decrypt Mobile :" + decMobile);
			String imeiNo = otp.getImeiNo();

			if (status != null && status.equals("Active") && !status.equals("null")) {
				Validator validator = new Validator();
				

				if (!validator.isMobileValid(decMobile)) {
					response.setResponseCode(201);
					response.setResponseDesc("Invalid mobile number format");
					return response;
				}

				long mobileNoCount = adhaRepo.getMobileNoCount(decMobile);
				boolean otpFlag = repo.checkOTPIntervel(decMobile, imeiNo);
				if (mobileNoCount > 10) {
					response.setResponseCode(320);
					response.setResponseDesc("Mobile number already registred.");
					return response;
				}
				if (otpFlag) {
					response.setResponseCode(320);
					response.setResponseDesc("Too many OTP requests.");
					return response;
				}

				Random random = new Random();
				StringBuilder oneTimePassword = new StringBuilder();
				for (int i = 0; i < 4; i++) {
					int randomNumber = random.nextInt(10);
					oneTimePassword.append(randomNumber);
				}
				String oneTimePasswrd = oneTimePassword.toString().trim();
				// System.out.println("OTP Mobile : " + oneTimePasswrd);
				CandidateOtp newotpinst = repo.getMobileCandidateInst(decMobile, imeiNo);
				if (newotpinst == null) {
					CandidateOtp otpInst = new CandidateOtp();
					otpInst.setEmailId("");
					otpInst.setMobileNo(decMobile);
					otpInst.setImeiNo(imeiNo);
					otpInst.setOtp(oneTimePasswrd);
					// otpInst.setOtp("0000");
					otpInst.setVerified(false);
					otpInst.setOtpAttempt((short) 0);
					otpInst.setCreatedOn(currentTimestamp);
					otpInst.setCreatedBy("NIC");
					otpInst.setUpdatedBy("NIC");
					otpInst.setUpdatedOn(currentTimestamp);
					repo.save(otpInst);
				} else {
					
					if (newotpinst.getOtpAttempt() > 0) {
						Date updatedDate = newotpinst.getUpdatedOn();
						Instant updatedInstant = updatedDate.toInstant();
						Instant currentInstant = currentTimestamp.toInstant();
						long resultTime = ChronoUnit.MINUTES.between(updatedInstant, currentInstant);

						if ((5 - resultTime) <= 0) {
							newotpinst.setOtpAttempt(0);
							;
							repo.save(newotpinst);
						} else {
							response.setResponseDesc(
									"You have exceeded the maximum number of OTP attempts. Please try again after "
											+ (5 - resultTime) + " minutes.");
							response.setResponseCode(320);
							newotpinst.setOtpAttempt((short) (newotpinst.getOtpAttempt() + 1));
							repo.save(newotpinst);
							return response;
						}

					}
					
					
					newotpinst.setEmailId("");
					newotpinst.setMobileNo(decMobile);
					newotpinst.setImeiNo(imeiNo);
					newotpinst.setOtp(oneTimePasswrd);
					// newotpinst.setOtp("0000");
					newotpinst.setVerified(false);
					newotpinst.setOtpAttempt((short) 0);
					newotpinst.setCreatedOn(currentTimestamp);
					newotpinst.setCreatedBy("NIC");
					newotpinst.setUpdatedBy("NIC");
					newotpinst.setUpdatedOn(currentTimestamp);
					repo.save(newotpinst);
				}

				sms = oneTimePasswrd
						+ " is your One Time Password for online candidate registration under DDUGKY-KAUSHAL PANJEE. Don't share this with anyone.";
				smsBean.setMobile(otp.getMobileNo());
				// smsBean.setMsg(encodedMsg);
				smsBean.setUnicodeMsg(true);
				smsBean.setSms(sms);
				smsBean.setTemplateId("1007753511310303775");

				if (!smsBean.getMobile().equalsIgnoreCase("")) {

					sendSMS.sendSMS(smsBean);
					// System.out.println(" is sms send " + result);
				}

				response.setResponseCode(HttpStatus.OK.value());
				response.setResponseDesc(HttpStatus.OK.name());
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
		bnkService.setSecurityHeaders(res);
		return response;
	}

	public String encodeMsg(String msg) {

		String hexString = "";
		for (char ch : msg.trim().toCharArray()) {
			hexString = hexString.trim().concat(String.format("%04x", (int) ch));
		}
		return hexString;
	}

	/*
	 * To generate OTP for email Id verification
	 */
	@Override
	public OtpResponse<Map<String, Object>> otpGenerationForMail(OtpBean otp, HttpServletRequest req,
			HttpServletResponse res) {
		Timestamp currentTimestamp = Timestamp.from(Instant.now());
		OtpResponse<Map<String, Object>> response = new OtpResponse<>();
		// System.out.println("Email Otp: ");
		try {
			String appVersion = otp.getAppVersion();
			String status = repo.appVersionStatus(appVersion);
			String imeiNo = otp.getImeiNo();
			String emailId = otp.getEmail();
			String msg = "";

			if (status != null && status.equals("Active") && !status.equals("null")) {
				Validator validator = new Validator();

				if (!validator.isEmailValid(emailId)) {
					response.setResponseCode(201);
					response.setResponseDesc("Invalid email Id!");
					return response;
				}

				
				boolean otpFlag = repo.checkOTPMailIntervel(emailId, imeiNo);
				if (otpFlag) {
					response.setResponseCode(201);
					response.setResponseDesc("Too many OTP requests.");
					return response;
				}
				
				Random random = new Random();
				StringBuilder oneTimePassword = new StringBuilder();
				for (int i = 0; i < 4; i++) {
					int randomNumber = random.nextInt(10);
					oneTimePassword.append(randomNumber);
				}
				String oneTimePasswrd = oneTimePassword.toString().trim();
				// System.out.println("OTP mail : " + oneTimePasswrd);
				CandidateOtp newotpinst = repo.getEmailCandidateInst(emailId, imeiNo);
				if (newotpinst == null) {
					CandidateOtp otpinst = new CandidateOtp();
					otpinst.setEmailId(emailId);
					otpinst.setMobileNo("");
					otpinst.setImeiNo(imeiNo);
					otpinst.setOtp(oneTimePasswrd);
					// otpinst.setOtp("0000");
					otpinst.setVerified(false);
					otpinst.setOtpAttempt((short) 0);
					otpinst.setCreatedOn(currentTimestamp);
					otpinst.setCreatedBy("NIC");
					otpinst.setUpdatedBy("NIC");
					otpinst.setUpdatedOn(currentTimestamp);
					repo.save(otpinst);
				} else {
					
					if (newotpinst.getOtpAttempt() > 0) {
						Date updatedDate = newotpinst.getUpdatedOn();
						Instant updatedInstant = updatedDate.toInstant();
						Instant currentInstant = currentTimestamp.toInstant();
						long resultTime = ChronoUnit.MINUTES.between(updatedInstant, currentInstant);

						if ((5 - resultTime) <= 0) {
							newotpinst.setOtpAttempt(0);
							;
							repo.save(newotpinst);
						} else {
							response.setResponseDesc(
									"You have exceeded the maximum number of OTP attempts. Please try again after "
											+ (5 - resultTime) + " minutes.");
							response.setResponseCode(201);
							newotpinst.setOtpAttempt((short) (newotpinst.getOtpAttempt() + 1));
							repo.save(newotpinst);
							return response;
						}

					}
					
					newotpinst.setEmailId(emailId);
					newotpinst.setMobileNo("");
					newotpinst.setImeiNo(imeiNo);
					newotpinst.setOtp(oneTimePasswrd);
					// newotpinst.setOtp("0000");
					newotpinst.setVerified(false);
					newotpinst.setOtpAttempt((short) 0);
					newotpinst.setCreatedOn(currentTimestamp);
					newotpinst.setCreatedBy("NIC");
					newotpinst.setUpdatedBy("NIC");
					newotpinst.setUpdatedOn(currentTimestamp);
					repo.save(newotpinst);
				}
				// System.out.println("Email Otp: " + oneTimePassword);

				msg = "<html xmlns=\"http://www.w3.org/1999/xhtml\" xmlns=\"http://www.w3.org/1999/xhtml\"><head><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" /> \r\n"
						+ "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" /><title>Registration: Email Verification through OTP</title></head> \r\n"
						+ "<body style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box;width: 100% !important;height: 100%;margin: 0;line-height: 1.4;background-color: #F2F4F6;color: #74787E;-webkit-text-size-adjust: none\"> \r\n"
						+ "<span class=\"preheader\" style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box;display: none !important;visibility: hidden;mso-hide: all;font-size: 1px;line-height: 1px;max-height: 0;max-width: 0;opacity: 0;overflow: hidden\">OTP for Email ID verification on Kaushal Panjee mobile app</span> <table class=\"email-wrapper\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box;width: 100%;margin: 0;padding: 0;-premailer-width: 100%;-premailer-cellpadding: 0;-premailer-cellspacing: 0;background-color: #F2F4F6\"> \r\n"
						+ "<tr style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box\"> \r\n"
						+ "<td align=\"center\" style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box;word-break: break-word\"> \r\n"
						+ "<table class=\"email-content\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box;width: 100%;margin: 0;padding: 0;-premailer-width: 100%;-premailer-cellpadding: 0;-premailer-cellspacing: 0\"> \r\n"
						+ "<tr style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box\"> \r\n"
						+ "<td class=\"email-masthead\" style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box;word-break: break-word;padding: 5px 0\"><a href=\"https://kaushal.rural.gov.in\" class=\"email-masthead_name\" style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box;color: #5e769c;font-size: 18px;line-height: 3;margin-left: 22px !important;font-weight: 500;text-decoration: none;text-shadow: 0 1px 0 white\"> Kaushal Panjee App </a> </td> </tr> <!-- Email Body --> \r\n"
						+ "<tr style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box\"> <td class=\"email-body\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box;word-break: break-word;width: 100%;margin: 0;padding: 0;-premailer-width: 100%;-premailer-cellpadding: 0;-premailer-cellspacing: 0;border-top: 1px solid #EDEFF2;border-bottom: 1px solid #EDEFF2;background-color: #FFF\"> \r\n"
						+ "<table class=\"email-body_inner\" align=\"center\" width=\"570\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box;width: 570px;margin: 0 auto;padding: 0;-premailer-width: 570px;-premailer-cellpadding: 0;-premailer-cellspacing: 0;background-color: #FFF\"> <!-- Body content --> <tr style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box\"> <td class=\"content-cell\" style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box;word-break: break-word;padding: 35px\"> \r\n"
						+ "<h1 style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box;margin-top: 0;color: #2F3133;font-size: 19px;font-weight: bold;text-align: left\">Dear User,</h1> <p style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box;line-height: 1.5em;text-align: left;margin-top: 0;color: #74787E;font-size: 16px\">"
						+ oneTimePasswrd
						+ " is your OTP for Email ID verification on Kaushal Panjee mobile app <strong style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box\"></strong></p> <!-- Action --> \r\n"
						+ "<p style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box;line-height: 1.5em;text-align: left;margin-top: 0;color: #74787E;font-size: 16px\">For your security, do not share this OTP with anyone. If you did not initiate this registration request, please ignore this email and report to <b style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box\">grameen.kaushal@nic.in</b></p> \r\n"
						+ "<p style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box;line-height: 1.5em;text-align: left;margin-top: 0;color: #74787E;font-size: 16px\">Thanks, <br style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box\" />Grameen Kaushal Team</p> <!-- Sub copy --> </td> </tr> </table> </td> </tr> <tr style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box\"> <td style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box;word-break: break-word\"> \r\n"
						+ "<table class=\"email-footer\" align=\"center\" width=\"570\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box;width: 570px;margin: 0 auto;padding: 0;-premailer-width: 570px;-premailer-cellpadding: 0;-premailer-cellspacing: 0;text-align: center\"> <tr style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box\"> <td class=\"content-cell\" align=\"center\" style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box;word-break: break-word;padding: 35px\"> \r\n"
						+ "<p class=\"sub align-center\" style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box;line-height: 1.5em;text-align: left;color: #AEAEAE;margin-top: 0;font-size: 12px\"></p> <p class=\"sub align-center\" style=\"font-family: Arial, &quot;Helvetica Neue&quot;, Helvetica, sans-serif;box-sizing: border-box;line-height: 1.5em;text-align: left;color: #AEAEAE;margin-top: 0;font-size: 12px\"> Grameen Kaushalya Yojana (GKY)</p> </td> </tr> </table> </td> </tr> </table> </td> </tr> </table> </body></html>\";";

				SendMail sendmail = new SendMail();
				String emailmailid = otp.getEmail();
				if (emailmailid != null && !emailmailid.equalsIgnoreCase("")) {

					sendmail.sendMail(emailmailid, "OTP for verify email id of ", msg);
				}

				response.setResponseCode(HttpStatus.OK.value());
				response.setResponseDesc(HttpStatus.OK.name());
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
		bnkService.setSecurityHeaders(res);
		return response;
	}

	/*
	 * To verify OTP for mobile number verification
	 */
	@Override
	@Transactional
	public OtpResponse<Map<String, Object>> validateOtp(OtpBean otp, HttpServletRequest req, HttpServletResponse res) {
		OtpResponse<Map<String, Object>> response = new OtpResponse<>();
		List<Map<String, Object>> wrappedList = new ArrayList<>();
		List<Object[]> candidateUserList = null;
		Timestamp currentTimestamp = Timestamp.from(Instant.now());
		String mobileNo = otp.getMobileNo();
		String emailId = otp.getEmail();
		String imeiNo = otp.getImeiNo();
		String userOtp = otp.getOtp();
		String appVersion = otp.getAppVersion();
		String appStatus = repo.appVersionStatus(appVersion);
		boolean status = false;
		CandidateOtp otpinst = null;
		try {
			if (appStatus != null && appStatus.equals("Active") && !appStatus.equals("null")) {

				if (mobileNo != null && emailId == null) {
					otpinst = repo.getMobileCandidateInst(mobileNo, imeiNo);
					status = repo.getStatus(mobileNo, userOtp, imeiNo);
				} else if (mobileNo == null && emailId != null) {
					otpinst = repo.getEmailCandidateInst(emailId, imeiNo);
					status = repo.getEmailStatus(emailId, userOtp, imeiNo);
				} else {
					otpinst = repo.getCandidateInst(emailId, mobileNo, imeiNo);
					status = repo.getForgetIdPassStatus(mobileNo, emailId, userOtp, imeiNo);
				}

				if (otpinst == null) {
					response.setResponseDesc("Invalid OTP.");
					return response;
				}

				if (otpinst.getOtpAttempt() > 4) {
					Date updatedDate = otpinst.getUpdatedOn();
					Instant updatedInstant = updatedDate.toInstant();
					Instant currentInstant = currentTimestamp.toInstant();
					long resultTime = ChronoUnit.MINUTES.between(updatedInstant, currentInstant);

					if ((15 - resultTime) <= 0) {
						otpinst.setOtpAttempt((short) 0);
						repo.save(otpinst);
					} else {

						response.setResponseCode(210);
						response.setResponseDesc(
								"You have exceeded the maximum number of otp attempts. Please try again after "
										+ (15 - resultTime) + " minutes.");
						otpinst.setOtpAttempt((short) (otpinst.getOtpAttempt() + 1));
						repo.save(otpinst);
						return response;
					}
				}

				if (status) {

					if (emailId != null && !emailId.isEmpty() && (mobileNo == null || mobileNo.isEmpty())) {
						// Case: Email provided, Mobile not provided
						repo.delete(otpinst);
						response.setResponseCode(HttpStatus.OK.value());
						response.setResponseDesc(HttpStatus.OK.name());
						response.setResponseFlag("E"); // Flag for Email only

					} else if (mobileNo != null && !mobileNo.isEmpty() && (emailId == null || emailId.isEmpty())) {
						// Case: Mobile provided, Email not provided
						repo.delete(otpinst);
						response.setResponseCode(HttpStatus.OK.value());
						response.setResponseDesc(HttpStatus.OK.name());
						response.setResponseFlag("M"); // Flag for Mobile only

					} else {

						// Case: Both Email and Mobile provided or both are null/empty
						repo.delete(otpinst);

						candidateUserList = repo.getCandidateUserLoginIdList(emailId, mobileNo);
						if (candidateUserList != null && candidateUserList.size() != 0) {
							wrappedList = candidateUserList.stream().map(p -> {
								Map<String, Object> map = new HashMap<String, Object>();
								try {
									map.put("loginId", p[0] + "(" + p[1] + ")");
								} catch (Exception e) {
									e.printStackTrace();
								}
								return map;
							}).collect(Collectors.toList());
						}
						response.setWrappedList(wrappedList);
						response.setResponseCode(HttpStatus.OK.value());
						response.setResponseDesc(HttpStatus.OK.name());
						response.setResponseFlag("ME"); // Flag for both or neither
					}

				} else {

					if (otpinst != null) {
						otpinst.setVerified(status);
						repo.save(otpinst);

						if (otpinst.getOtpAttempt() > 3) {

							otpinst.setOtpAttempt((short) (otpinst.getOtpAttempt() + 1));
							otpinst.setUpdatedOn(currentTimestamp);
							repo.save(otpinst);

							response.setResponseCode(210);
							response.setResponseDesc(
									"You have exceeded the maximum number of login attempts. Please try again in 15 minutes.");
							return response;

						} else {

							otpinst.setOtpAttempt((short) (otpinst.getOtpAttempt() + 1));
							otpinst.setUpdatedOn(currentTimestamp);
							repo.save(otpinst);

							response.setResponseCode(210);
							response.setResponseDesc(
									"Invalid! You have " + (5 - otpinst.getOtpAttempt()) + " attempts left.");
							return response;

						}

					}

				}
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
		bnkService.setSecurityHeaders(res);
		return response;
	}

	public OtpResponse<Map<String, Object>> changePassword(UserPassword pass, HttpServletRequest req,
			HttpServletResponse res) {
		OtpResponse<Map<String, Object>> response = new OtpResponse<>();
		String loginId = pass.getLoginId();
		String password = pass.getOldPassword();
		String newPassword = pass.getNewPassword();
		String decNewPassword = "";
		String hasNewPass = "";
		try {
			decNewPassword = enc.decrypt(newPassword);
			hasNewPass = enc.hash(decNewPassword);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Timestamp currentTimestamp = Timestamp.from(Instant.now());
		AuthenticationEntity authinstance = authrepo.getAuthInstance(loginId);
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
			authrepo.delete(authinstance);
			response.setResponseCode(HttpStatus.UNAUTHORIZED.value());
			response.setResponseDesc(HttpStatus.UNAUTHORIZED.name());
			return response;
		}
		String authHeader = req.getHeader("Authorization");
		String authToken = null;

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			authToken = authHeader.substring(7); // Remove "Bearer " prefix
		}

		boolean authFlag = authrepo.getTokenValidation(loginId, authToken);
		try {
			if (authFlag) {
				String appVersion = pass.getAppVersion();
				String status = repo.appVersionStatus(appVersion);
				// System.out.println("Old Password: " + password);
				// System.out.println("New Password: " + newPassword);
				if (status != null && status.equals("Active") && !status.equals("null")) {
					if (password.equals(hasNewPass)) {
						response.setResponseCode(208);
						response.setResponseDesc("New password should not match with old password.");
						return response;
					}

					// System.out.println("Password valid Flag: " + flag);

					UserDetails detailsInstance = userRepo.getByLoginId(loginId);

					boolean userFlag = userRepo.userIdExistOrNot(loginId, password);
					if (userFlag) {
						boolean flag = isPasswordValid(decNewPassword);

						if (!flag) {
							response.setResponseCode(206);
							response.setResponseDesc(
									"Your password should be minimum 10 character and maximum 12 character and should also contain atleast 1 uppercase, 1 lowercase, 1 special character and 1 numeric value.");
							return response;
						}

						detailsInstance.setPassword(hasNewPass);
						detailsInstance.setPasswordChanged("Y");
						// detailsInstance.setPassword(newPassword);
						detailsInstance.setUpdatedOn(currentTimestamp);
						detailsInstance.setUpdatedBy(detailsInstance.getUserName());
						userRepo.save(detailsInstance);
						response.setResponseCode(HttpStatus.OK.value());
						response.setResponseDesc("Your password has been changed successfully.");
					} else {
						response.setResponseCode(207);
						response.setResponseDesc("Your password is wrong.");
					}

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
		bnkService.setSecurityHeaders(res);
		return response;
	}

	@SuppressWarnings("unused")
	public OtpResponse<Map<String, Object>> forgetUserIdPassword(UserPassword pass, HttpServletRequest req,
			HttpServletResponse res) {
		Timestamp currentTimestamp = Timestamp.from(Instant.now());
		OtpResponse<Map<String, Object>> response = new OtpResponse<>();
		try {

			String appVersion = pass.getAppVersion();
			String status = repo.appVersionStatus(appVersion);

			if (status != null && status.equals("Active") && !status.equals("null")) {

				Validator validator = new Validator();
				SMSBean smsBean = new SMSBean();
				SendSMSTest sendSMS;
				sendSMS = new SendSMSTest();
				String mobileNo = pass.getMobileNo();
				String emailId = pass.getEmail();
				String imeiNo = pass.getImeiNo();

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
				Random random = new Random();
				StringBuilder oneTimePassword = new StringBuilder();
				for (int i = 0; i < 4; i++) {
					int randomNumber = random.nextInt(10);
					oneTimePassword.append(randomNumber);
				}
				String oneTimePass = oneTimePassword.toString().trim();

				// System.out.println("OTP ForgetId Password : " + oneTimePass);
				boolean userFlag = userPrRepo.userIdPasswordExistOrNot(mobileNo, emailId);
				if (userFlag) {

					CandidateOtp newOtpinst = repo.getCandidateInst(emailId, mobileNo, imeiNo);
					if (newOtpinst == null) {
						CandidateOtp otpinst = new CandidateOtp();
						otpinst.setEmailId(emailId);
						otpinst.setMobileNo(mobileNo);
						otpinst.setImeiNo(imeiNo);
						otpinst.setOtp(oneTimePass);
						otpinst.setVerified(false);
						otpinst.setOtpAttempt((short) 0);
						otpinst.setCreatedOn(currentTimestamp);
						otpinst.setCreatedBy("NIC");
						otpinst.setUpdatedBy("NIC");
						otpinst.setUpdatedOn(currentTimestamp);
						repo.save(otpinst);
					} else {
						newOtpinst.setEmailId(emailId);
						newOtpinst.setMobileNo(mobileNo);
						newOtpinst.setImeiNo(imeiNo);
						newOtpinst.setOtp(oneTimePass);
						newOtpinst.setVerified(false);
						newOtpinst.setOtpAttempt((short) 0);
						newOtpinst.setCreatedOn(currentTimestamp);
						newOtpinst.setCreatedBy("NIC");
						newOtpinst.setUpdatedBy("NIC");
						newOtpinst.setUpdatedOn(currentTimestamp);
						repo.save(newOtpinst);
					}

					String sms = oneTimePass
							+ " is your One Time Password for online candidate registration under DDUGKY-KAUSHAL PANJEE. Don't share this with anyone.";
					smsBean.setMobile(mobileNo);
					// smsBean.setMsg(encodedMsg);
					smsBean.setUnicodeMsg(true);
					smsBean.setSms(sms);
					smsBean.setTemplateId("1007753511310303775");

					if (!smsBean.getMobile().equalsIgnoreCase("")) {

						String result = sendSMS.sendSMS(smsBean);
						// System.out.println(" is sms send " + result);
					}

					response.setResponseCode(HttpStatus.OK.value());
					response.setResponseDesc(HttpStatus.OK.name());
				} else {
					response.setResponseCode(207);
					response.setResponseDesc("Register mobile number and email Id mismatch.");
				}

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
		bnkService.setSecurityHeaders(res);
		return response;
	}

	public AuthResponse<Map<String, Object>> generateAuthToken(AuthTokenBean token, HttpServletRequest req,
			HttpServletResponse res) {
		AuthResponse<Map<String, Object>> response = new AuthResponse<>();

		try {
			String imeiNo = token.getImeiNo();
			String appVersion = token.getAppVersion();
			String status = repo.appVersionStatus(appVersion);
			if (status != null && status.equals("Active") && !status.equals("null")) {

				String pssString = generateSecureRandomString(6);
				String authToken = generateSecureRandomString(8);
				// System.out.println("pssString : " + pssString);
				// System.out.println("authToken : " + authToken);
				AuthenticationEntity authinst = authrepo.getAuthenticationInstance(imeiNo);
				if (authinst == null) {
					AuthenticationEntity authinstance = new AuthenticationEntity();
					authinstance.setImeiNo(imeiNo);
					authinstance.setAuthToken(enc.encrypt(authToken));
					authinstance.setPassCode(enc.encrypt(pssString));
					Date date = new Date();
					authinstance.setCreatedOn(date);
					authinstance.setCreatedBy("NIC");
					authinstance.setUpdatedOn(date);
					authinstance.setUpdatedBy("NIC");
					authrepo.save(authinstance);
				} else {

					authinst.setAuthToken(enc.encrypt(authToken));
					authinst.setPassCode(enc.encrypt(pssString));
					Date date = new Date();
					authinst.setCreatedOn(date);
					authinst.setUpdatedOn(date);
					authrepo.save(authinst);
				}

				response.setAuthToken(enc.encrypt(authToken));
				response.setPassString(enc.encrypt(pssString));
				response.setResponseCode(HttpStatus.OK.value());
				response.setResponseDesc(HttpStatus.OK.name());

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
		bnkService.setSecurityHeaders(res);
		return response;
	}

	public OtpResponse<Map<String, Object>> updateUserIdPassword(UserPassword pass, HttpServletRequest req,
			HttpServletResponse res) {
		OtpResponse<Map<String, Object>> response = new OtpResponse<>();
		String appVersion = pass.getAppVersion();
		String loginId = pass.getLoginId();
		String password = pass.getNewPassword();

		String status = repo.appVersionStatus(appVersion);
		try {
			if (status != null && status.equals("Active") && !status.equals("null")) {

				String decNewPassword = enc.decrypt(password);

				UserDetails obj = userRepo.getByLoginId(loginId);
				if (obj != null) {
					boolean flag = isPasswordValid(decNewPassword);

					if (!flag) {
						response.setResponseCode(206);
						response.setResponseDesc(
								"Your password should be minimum 10 character and maximum 12 character and should also contain atleast 1 uppercase, 1 lowercase, 1 special character and 1 numeric value.");
						return response;
					}

					obj.setPassword(enc.hash(decNewPassword));
					userRepo.save(obj);

					response.setResponseCode(HttpStatus.OK.value());
					response.setResponseDesc(HttpStatus.OK.name());

				} else {

					response.setResponseCode(HttpStatus.NOT_FOUND.value());
					response.setResponseDesc("Something went wrong.");
				}

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
		bnkService.setSecurityHeaders(res);
		return response;

	}

	@SuppressWarnings("unused")
	public OtpResponse<Map<String, Object>> forgetUserIdPasswordNew(UserPassword pass, HttpServletRequest req,
			HttpServletResponse res) {
		Timestamp currentTimestamp = Timestamp.from(Instant.now());
		OtpResponse<Map<String, Object>> response = new OtpResponse<>();
		try {

			String appVersion = pass.getAppVersion();
			String status = repo.appVersionStatus(appVersion);

			if (status != null && status.equals("Active") && !status.equals("null")) {

				Validator validator = new Validator();
				SMSBean smsBean = new SMSBean();
				SendSMSTest sendSMS;
				sendSMS = new SendSMSTest();
				String aadharNo = pass.getAadharNo();
				String decAadharNo = enc.decrypt(aadharNo);
				String encAadharNo = enc.encryptNew(decAadharNo);
				boolean aadhaarFlag = repo.checkAadharExist(encAadharNo);

				if (aadhaarFlag) {
					String candidateId = repo.getCandidateUserLoginIdList(encAadharNo);
					response.setCandidateId(candidateId);
					response.setResponseCode(HttpStatus.OK.value());
					response.setResponseDesc(HttpStatus.OK.name());
				} else {
					response.setResponseCode(HttpStatus.NOT_FOUND.value());
					response.setResponseDesc("Aadhar number does not exists.");
				}

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
		bnkService.setSecurityHeaders(res);
		return response;
	}

}
