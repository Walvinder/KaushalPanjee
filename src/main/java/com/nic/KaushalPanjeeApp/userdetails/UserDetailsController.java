package com.nic.KaushalPanjeeApp.userdetails;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nic.KaushalPanjeeApp.aadhaarLog.AadhaarTxnRequest;
import com.nic.KaushalPanjeeApp.aadhaarLog.BankDetailsResponse;
import com.nic.KaushalPanjeeApp.aadhaarLog.BankRequest;
import com.nic.KaushalPanjeeApp.config.EncryptionUtil;
import com.nic.KaushalPanjeeApp.helper.MasterResponse;
import com.nic.KaushalPanjeeApp.helper.OtpResponse;
import com.nic.KaushalPanjeeApp.helper.Response;
import com.nic.KaushalPanjeeApp.helper.UserCreationBean;
import com.nic.KaushalPanjeeApp.helper.UserDetailsBean;
import com.nic.KaushalPanjeeApp.helper.UserDeviceBean;
import com.nic.KaushalPanjeeApp.helper.UserPassword;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/kaushalpanjee/panjeeapi")
//@RequestMapping("/panjeeapi")
public class UserDetailsController {

	@Autowired
	UserDetailsService service;

	@Autowired
	CandidateLogRepository logrepo;

	@Autowired
	private EncryptionUtil enc;

	@PostMapping("/createUser")
	public ResponseEntity<?> saveUserDetails(@RequestBody UserCreationBean user, HttpServletRequest req,
			HttpServletResponse res) throws IOException {
		Response<Map<String, Object>> response = service.createUser(user, req, res);
		return new ResponseEntity<Response<Map<String, Object>>>(response, HttpStatus.OK);
	}

	@PostMapping("/login")
	public ResponseEntity<?> userLogin(@RequestBody UserDeviceBean login, HttpServletRequest req,
			HttpServletResponse res) throws Exception {
		Response<Map<String, Object>> response = service.loginUser(login, req, res);

		CandidateLogEntity logInstance = new CandidateLogEntity();
		logInstance.setUsesId(enc.encryptNew(login.getLoginId()));
		logInstance.setImeiNo(enc.encryptNew(login.getImeiNo()));
		logInstance.setDateAndTime(Timestamp.from(Instant.now()));
		if (response.getResponseCode() == 200) {
			logInstance.setActionPerformed(enc.encryptNew("User login sucessfully"));
			logInstance.setStatus(enc.encryptNew("Success"));
		} else {
			logInstance.setActionPerformed(enc.encryptNew("User login failed"));
			logInstance.setStatus(enc.encryptNew("Failed"));
		}

		logrepo.save(logInstance);

		return new ResponseEntity<Response<Map<String, Object>>>(response, HttpStatus.OK);
	}

	@PostMapping("/userDetails")
	public ResponseEntity<?> userDetails(@RequestBody UserDeviceBean user, HttpServletRequest req,
			HttpServletResponse res) throws IOException {
		Response<Map<String, Object>> response = service.getUserDetails(user, req, res);
		return new ResponseEntity<Response<Map<String, Object>>>(response, HttpStatus.OK);
	}

	@PostMapping("/insertDetails")
	public ResponseEntity<?> insertUserDetails(@RequestBody UserDetailsBean user, HttpServletRequest req,
			HttpServletResponse res) throws Exception {
		Response<Map<String, Object>> response = service.insertCandidateDetails(user, req, res);

		CandidateLogEntity logInstance = new CandidateLogEntity();
		logInstance.setUsesId(enc.encryptNew(user.getLoginId()));
		logInstance.setImeiNo(enc.encryptNew(user.getImeiNo()));
		logInstance.setDateAndTime(Timestamp.from(Instant.now()));
		if (response.getResponseCode() == 200) {
			logInstance.setActionPerformed(enc.encryptNew("Candidate profile details saved successfully"));
			logInstance.setStatus(enc.encryptNew("Success"));
		} else {
			logInstance.setActionPerformed(enc.encryptNew("Candidate profile details save failed"));
			logInstance.setStatus(enc.encryptNew("Failed"));
		}
		logrepo.save(logInstance);

		return new ResponseEntity<Response<Map<String, Object>>>(response, HttpStatus.OK);
	}

	@PostMapping("/sectionStatus")
	public ResponseEntity<?> getSectionStatus(@RequestBody UserDetailsBean user, HttpServletRequest req,
			HttpServletResponse res) throws IOException {
		Response<Map<String, Object>> response = service.sectionStatus(user, req, res);
		return new ResponseEntity<Response<Map<String, Object>>>(response, HttpStatus.OK);
	}

	@PostMapping("/candidateDetails")
	public ResponseEntity<?> getCandidateMasterData(@RequestBody UserDetailsBean user, HttpServletRequest req,
			HttpServletResponse res) throws IOException {
		MasterResponse<Map<String, Object>> response = service.getCandidateMasterList(user, req, res);
		return new ResponseEntity<MasterResponse<Map<String, Object>>>(response, HttpStatus.OK);
	}

	@PostMapping("/changeProfilePhoto")
	public ResponseEntity<?> forChangeProfileImage(@RequestBody UserDetailsBean user, HttpServletRequest req,
			HttpServletResponse res) throws Exception {
		Response<Map<String, Object>> response = service.changeProfileImage(user, req, res);

		CandidateLogEntity logInstance = new CandidateLogEntity();
		logInstance.setUsesId(enc.encryptNew(user.getLoginId()));
		logInstance.setImeiNo(enc.encryptNew(user.getImeiNo()));
		logInstance.setDateAndTime(Timestamp.from(Instant.now()));
		if (response.getResponseCode() == 200) {
			logInstance.setActionPerformed(enc.encryptNew("Candidate profile picture changed successfully"));
			logInstance.setStatus(enc.encryptNew("Success"));
		} else {
			logInstance.setActionPerformed(enc.encryptNew("Candidate profile picture change failed"));
			logInstance.setStatus(enc.encryptNew("Failed"));
		}
		logrepo.save(logInstance);

		return new ResponseEntity<Response<Map<String, Object>>>(response, HttpStatus.OK);
	}

	@PostMapping("/getLoginIdPassword")
	public ResponseEntity<?> getLoginIdPassword(@RequestBody UserPassword pass, HttpServletRequest req,
			HttpServletResponse res) {
		OtpResponse<Map<String, Object>> response = service.getUserLoginIdPassword(pass, req, res);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/checkUserExistance")
	public ResponseEntity<?> checkUserExistance(@RequestBody UserPassword pass, HttpServletRequest req,
			HttpServletResponse res) {
		Response<Map<String, Object>> response = service.checkUserExistance(pass, req, res);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/updateAdharSHA")
	public ResponseEntity<?> updateAdhaarSHAId() {
		Response<Map<String, Object>> response = service.updateUserAadhaarSHA();
		return new ResponseEntity<Response<Map<String, Object>>>(response, HttpStatus.OK);
	}

	@PostMapping("/updateFaceRegistred")
	public ResponseEntity<?> updateFaceRegistred(@RequestBody UserPassword request, HttpServletRequest req,
			HttpServletResponse res) {
		Response<Map<String, Object>> response = service.updateFaceRegistred(request, req, res);
		return new ResponseEntity<Response<Map<String, Object>>>(response, HttpStatus.OK);
	}

	@PostMapping("/updateAdhaarDetails")
	public ResponseEntity<?> updateUserAadhaarDetails(@RequestBody UserCreationBean user, HttpServletRequest req,
			HttpServletResponse res) throws Exception {
		Response<Map<String, Object>> response = service.updateUserAadhaarDetails(user, req, res);

		CandidateLogEntity logInstance = new CandidateLogEntity();
		logInstance.setUsesId(enc.encryptNew(user.getLoginId()));
		logInstance.setImeiNo(enc.encryptNew(user.getImeiNo()));
		logInstance.setDateAndTime(Timestamp.from(Instant.now()));
		if (response.getResponseCode() == 200) {
			logInstance.setActionPerformed(enc.encryptNew("Candidate aadhaar details updated successfully"));
			logInstance.setStatus(enc.encryptNew("Success"));
		} else {
			logInstance.setActionPerformed(enc.encryptNew("Candidate aadhaar details update failed"));
			logInstance.setStatus(enc.encryptNew("Failed"));
		}
		logrepo.save(logInstance);

		return new ResponseEntity<Response<Map<String, Object>>>(response, HttpStatus.OK);
	}

	@PostMapping("/updateCandidateEmailId")
	public ResponseEntity<?> updateCandidateEmailId(@RequestBody UserCreationBean user, HttpServletRequest req,
			HttpServletResponse res) throws IOException, Exception {
		Response<Map<String, Object>> response = service.updateCandidateEmailId(user, req, res);

		CandidateLogEntity logInstance = new CandidateLogEntity();
		logInstance.setUsesId(enc.encryptNew(user.getLoginId()));
		//logInstance.setImeiNo(enc.encryptNew(user.getImeiNo()));
		logInstance.setDateAndTime(Timestamp.from(Instant.now()));
		if (response.getResponseCode() == 200) {
			logInstance.setActionPerformed(enc.encryptNew("Candidate email Id has been updated sucessfully"));
			logInstance.setStatus(enc.encryptNew("Success"));
		} else {
			logInstance.setActionPerformed(enc.encryptNew("Candidate email Id has been update failed"));
			logInstance.setStatus(enc.encryptNew("Failed"));
		}
		logrepo.save(logInstance);

		return new ResponseEntity<Response<Map<String, Object>>>(response, HttpStatus.OK);
	}

	@PostMapping("/logout")
	public ResponseEntity<?> userLogOut(@RequestBody UserDeviceBean login, HttpServletRequest req,
			HttpServletResponse res) throws IOException {
		Response<Map<String, Object>> response = service.userLogOut(login, req, res);
		return new ResponseEntity<Response<Map<String, Object>>>(response, HttpStatus.OK);
	}

	@PostMapping("/bankList")
	public ResponseEntity<?> bankList(@RequestBody BankRequest request, HttpServletRequest req,
			HttpServletResponse res) {

		BankDetailsResponse<Map<String, Object>> response = service.getBankList(request, req, res);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/saveAadhaarTxn")
	public ResponseEntity<?> saveAadhaarTxn(@RequestBody AadhaarTxnRequest request, HttpServletResponse res) {

		Response<Map<String, Object>> response = service.saveAadhaarTxn(request, res);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
