package com.nic.KaushalPanjeeApp.tech;

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

import com.nic.KaushalPanjeeApp.config.EncryptionUtil;
import com.nic.KaushalPanjeeApp.helper.BannerResponse;
import com.nic.KaushalPanjeeApp.helper.LanguageResponse;
import com.nic.KaushalPanjeeApp.helper.OtpBean;
import com.nic.KaushalPanjeeApp.helper.Response;
import com.nic.KaushalPanjeeApp.helper.SurveyResponse;
import com.nic.KaushalPanjeeApp.helper.TechDomainResponse;
import com.nic.KaushalPanjeeApp.helper.TechQualificationResponse;
import com.nic.KaushalPanjeeApp.userdetails.CandidateLogEntity;
import com.nic.KaushalPanjeeApp.userdetails.CandidateLogRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/kaushalpanjee/panjeeapi")
//@RequestMapping("/panjeeapi")
public class TechnicalDetailsController {
	@Autowired
	TechnicalDetailsService service;

	@Autowired
	CandidateLogRepository logrepo;

	@Autowired
	private EncryptionUtil enc;

	@PostMapping("/technicalQualification")
	public ResponseEntity<?> getTechQualification(@RequestBody OtpBean qual, HttpServletRequest req,
			HttpServletResponse res) {
		TechQualificationResponse<Map<String, Object>> response = service.getTechQualificationList(qual, req, res);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/techDomain")
	public ResponseEntity<?> getTechDomain(@RequestBody OtpBean qual, HttpServletRequest req, HttpServletResponse res) {
		TechDomainResponse<Map<String, Object>> response = service.getQualificationDomainList(qual, req, res);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/surveyList")
	public ResponseEntity<?> getsurvey(@RequestBody OtpBean appversion, HttpServletRequest req,
			HttpServletResponse res) {
		SurveyResponse<Map<String, Object>> response = service.getSurveyList(appversion, req, res);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/languageList")
	public ResponseEntity<?> getLanguage(@RequestBody OtpBean appversion, HttpServletRequest req,
			HttpServletResponse res) {
		LanguageResponse<Map<String, Object>> response = service.getLanguageList(appversion, req, res);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/trainingCenter")
	public ResponseEntity<?> getTrainingCenterList(@RequestBody OtpBean taining, HttpServletRequest req,
			HttpServletResponse res) {
		LanguageResponse<Map<String, Object>> response = service.getTrainingCenterList(taining, req, res);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/searchCenter")
	public ResponseEntity<?> searchTrainingCenter(@RequestBody OtpBean taining, HttpServletRequest req,
			HttpServletResponse res) throws Exception {
		LanguageResponse<Map<String, Object>> response = service.searchTrainingCenterList(taining, req, res);

		CandidateLogEntity logInstance = new CandidateLogEntity();
		logInstance.setUsesId(enc.encryptNew(taining.getLoginId()));
		logInstance.setImeiNo(enc.encryptNew(taining.getImeiNo()));
		logInstance.setDateAndTime(Timestamp.from(Instant.now()));

		if (response.getResponseCode() == 200) {
			logInstance.setActionPerformed(enc.encryptNew("Training center search completed successfully"));
			logInstance.setStatus(enc.encryptNew("Success"));
		} else {
			logInstance.setActionPerformed(enc.encryptNew("Failed to search training center"));
			logInstance.setStatus(enc.encryptNew("Failed"));
		}
		logrepo.save(logInstance);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/getCenter")
	public ResponseEntity<?> getTrainingCenter(@RequestBody OtpBean taining, HttpServletRequest req,
			HttpServletResponse res) {
		LanguageResponse<Map<String, Object>> response = service.getTrainingCenter(taining, req, res);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/getBanner")
	public ResponseEntity<?> getBanner(@RequestBody OtpBean taining, HttpServletRequest req, HttpServletResponse res) {
		BannerResponse<Map<String, Object>> response = service.getBanner(taining, req, res);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/getSchemeList")
	public ResponseEntity<?> getSchemeList(@RequestBody OtpBean taining, HttpServletRequest req,
			HttpServletResponse res) {
		Response<Map<String, Object>> response = service.getSchemeList(taining, req, res);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
