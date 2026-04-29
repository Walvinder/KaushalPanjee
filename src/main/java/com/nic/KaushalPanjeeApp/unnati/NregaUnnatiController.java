package com.nic.KaushalPanjeeApp.unnati;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nic.KaushalPanjeeApp.helper.Response;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/kaushalpanjee/panjeeapi")
public class NregaUnnatiController {

	@Autowired
	NregaUnnatiService service;

	@PostMapping("/getNregaUnnatiApplicants")
	public ResponseEntity<?> getNregaUnnatiApplicants(@RequestBody NregaUnnatiApplicantBean user,
			HttpServletRequest req, HttpServletResponse res) throws IOException {
		Response<Map<String, Object>> response = service.getNregaUnnatiApplicants(user, req, res);
		return new ResponseEntity<Response<Map<String, Object>>>(response, HttpStatus.OK);
	}

	@PostMapping("/checkCandidateUnnati")
	public ResponseEntity<?> checkCandidateExistInUnnathi(@RequestBody CandidateExistsUnnathi user,
			HttpServletRequest req, HttpServletResponse res) throws IOException {
		Response<Map<String, Object>> response = service.checkCandidateExistInUnnathi(user, req, res);
		return new ResponseEntity<Response<Map<String, Object>>>(response, HttpStatus.OK);
	}

	@PostMapping("/sendKpIdPassword")
	public ResponseEntity<?> sendKpIdPassword(HttpServletRequest req, HttpServletResponse res) throws IOException {
		Response<Map<String, Object>> response = service.sendKpIdPassword(req, res);
		return new ResponseEntity<Response<Map<String, Object>>>(response, HttpStatus.OK);
	}

}
