package com.nic.KaushalPanjeeApp.bank;

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

import com.nic.KaushalPanjeeApp.config.EncryptionUtil;
import com.nic.KaushalPanjeeApp.config.SecurityHeader;
import com.nic.KaushalPanjeeApp.helper.HandledException;
import com.nic.KaushalPanjeeApp.otp.AuthenticationEntity;
import com.nic.KaushalPanjeeApp.otp.AuthenticationRepository;
import com.nic.KaushalPanjeeApp.otp.OtpRepository;
import com.nic.KaushalPanjeeApp.userdetails.UserDetailsRepository;
import com.nic.KaushalPanjeeApp.userdetails.UserDeviceInfoRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class BankServiceImpl implements BankService {

	@Autowired
	OtpRepository otpRepo;

	@Autowired
	BankRepository repo;

	@Autowired
	UserDetailsRepository userRepo;

	@Autowired
	UserDeviceInfoRepository deviceRepo;

	@Autowired
	AuthenticationRepository authRepo;

	@Autowired
	private EncryptionUtil enc;

	@Autowired
	private SecurityHeader sec;

	@Override
	public BankResponse<Map<String, Object>> getBankDetails(BankBean bank, HttpServletRequest req,
			HttpServletResponse res) {
		BankResponse<Map<String, Object>> response = new BankResponse<>();
		List<Map<String, Object>> wrappedList = new ArrayList<>();
		Timestamp currentTimestamp = Timestamp.from(Instant.now());
		String appVersion = bank.getAppVersion();
		String loginId = bank.getLoginId();
		// String ifscCode = bank.getIfscCode();
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

		List<Object[]> bankAccList = null;
		try {
			String authHeader = req.getHeader("Authorization");
			String authToken = null;

			if (authHeader != null && authHeader.startsWith("Bearer ")) {
				authToken = authHeader.substring(7); // Remove "Bearer " prefix
			}

			String ifscCode = enc.decrypt(bank.getIfscCode());

			String status = otpRepo.appVersionStatus(appVersion);

			boolean authFlag = authRepo.getTokenValidation(loginId, authToken);

			if (authFlag) {
				if (status != null && status.equals("Active") && !status.equals("null")) {

					bankAccList = repo.getBankAccDetailsList(ifscCode);

					if (bankAccList != null && bankAccList.size() != 0) {

						wrappedList = bankAccList.stream().map(p -> {
							Map<String, Object> map = new HashMap<String, Object>();
							try {
								Short bankCode = (Short) p[0];
								Integer branchCode = (Integer) p[2];

								String encbankCode = enc.encrypt(bankCode != null ? String.valueOf(bankCode) : "");
								String encbankName = enc.encrypt(p[1] != null ? (String) p[1] : "");
								String encbranchCode = enc
										.encrypt(branchCode != null ? String.valueOf(branchCode) : "");
								String encbranchName = enc.encrypt(p[3] != null ? (String) p[3] : "");
								String encaccLength = enc.encrypt(p[5] != null ? (String) p[5] : "");

								map.put("bankCode", encbankCode);
								map.put("bankName", encbankName);
								map.put("branchCode", encbranchCode);
								map.put("branchName", encbranchName);
								map.put("accLength", encaccLength);
							} catch (Exception e) {
								e.printStackTrace();
							}
							return map;
						})

								.collect(Collectors.toList());

						response.setBankDetailsList(wrappedList);
						response.setResponseCode(HttpStatus.OK.value());
						response.setResponseDesc(HttpStatus.OK.name());
						response.setResponseMsg("Here is bank details against " + ifscCode);
					} else {
						response.setBankDetailsList(wrappedList);
						response.setResponseCode(302);
						response.setResponseDesc(ifscCode + " is not valid ifsc code.");
						response.setResponseMsg("Bank details for " + ifscCode + " is not available.");
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
		sec.setSecurityHeaders(res);
		return response;
	}

}
