package com.nic.KaushalPanjeeApp.otp;

import java.util.Map;

import com.nic.KaushalPanjeeApp.helper.AuthResponse;
import com.nic.KaushalPanjeeApp.helper.AuthTokenBean;
import com.nic.KaushalPanjeeApp.helper.OtpBean;
import com.nic.KaushalPanjeeApp.helper.OtpResponse;
import com.nic.KaushalPanjeeApp.helper.UserPassword;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface OtpService {

	public OtpResponse<Map<String, Object>> otpGenerationForMobile(OtpBean otp, HttpServletRequest req,
			HttpServletResponse res);

	public OtpResponse<Map<String, Object>> otpGenerationForMail(OtpBean otp, HttpServletRequest req,
			HttpServletResponse res);

//	public OtpResponse<Map<String, Object>> validateMailOtp(String mobileNo, String otp);

	public OtpResponse<Map<String, Object>> changePassword(UserPassword pass, HttpServletRequest req,
			HttpServletResponse res);

	// public OtpResponse<Map<String, Object>> forgetUserIdPassword(UserPassword
	// pass, HttpServletRequest req, HttpServletResponse res);

	public OtpResponse<Map<String, Object>> forgetUserIdPasswordNew(UserPassword pass, HttpServletRequest req,
			HttpServletResponse res);

	public AuthResponse<Map<String, Object>> generateAuthToken(AuthTokenBean token, HttpServletRequest req,
			HttpServletResponse res);

	public OtpResponse<Map<String, Object>> validateOtp(OtpBean otp, HttpServletRequest req, HttpServletResponse res);

	public OtpResponse<Map<String, Object>> updateUserIdPassword(UserPassword pass, HttpServletRequest req,
			HttpServletResponse res);

}
