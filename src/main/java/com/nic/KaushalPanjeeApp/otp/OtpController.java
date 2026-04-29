package com.nic.KaushalPanjeeApp.otp;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nic.KaushalPanjeeApp.helper.AuthResponse;
import com.nic.KaushalPanjeeApp.helper.AuthTokenBean;
import com.nic.KaushalPanjeeApp.helper.OtpBean;
import com.nic.KaushalPanjeeApp.helper.OtpResponse;
import com.nic.KaushalPanjeeApp.helper.UserPassword;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/kaushalpanjee/panjeeapi")
//@RequestMapping("/panjeeapi")
public class OtpController {
	@Autowired
	OtpService service;

	@PostMapping("/generateMobileOtp")
	public ResponseEntity<?> generateMobileOtp(@RequestBody OtpBean otp, HttpServletRequest req,
			HttpServletResponse res) {

		OtpResponse<Map<String, Object>> response = service.otpGenerationForMobile(otp, req, res);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/validateOtp")
	public ResponseEntity<?> validateMobileOtp(@RequestBody OtpBean otp, HttpServletRequest req,
			HttpServletResponse res) {
		OtpResponse<Map<String, Object>> response = service.validateOtp(otp, req, res);
		return new ResponseEntity<OtpResponse<Map<String, Object>>>(response, HttpStatus.OK);
	}

	@PostMapping("/generateMailOtp")
	public ResponseEntity<?> generateMailOtp(@RequestBody OtpBean otp, HttpServletRequest req,
			HttpServletResponse res) {
		OtpResponse<Map<String, Object>> response = service.otpGenerationForMail(otp, req, res);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/changePassword")
	public ResponseEntity<?> changePassword(@RequestBody UserPassword pass, HttpServletRequest req,
			HttpServletResponse res) {
		OtpResponse<Map<String, Object>> response = service.changePassword(pass, req, res);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/forgetIdPassword")
	public ResponseEntity<?> forgetUserIdPassword(@RequestBody UserPassword pass, HttpServletRequest req,
			HttpServletResponse res) {
		// OtpResponse<Map<String,Object>> response = service.forgetUserIdPassword(pass,
		// req, res);
		OtpResponse<Map<String, Object>> response = service.forgetUserIdPasswordNew(pass, req, res);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/updatePassword")
	public ResponseEntity<?> updateUserIdPassword(@RequestBody UserPassword pass, HttpServletRequest req,
			HttpServletResponse res) {
		OtpResponse<Map<String, Object>> response = service.updateUserIdPassword(pass, req, res);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/generateToken")
	public ResponseEntity<?> generateAuthToken(@RequestBody AuthTokenBean token, HttpServletRequest req,
			HttpServletResponse res) {
		AuthResponse<Map<String, Object>> response = service.generateAuthToken(token, req, res);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
