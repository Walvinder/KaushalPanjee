package com.nic.KaushalPanjeeApp.trainingCenter;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nic.KaushalPanjeeApp.helper.LocationBean;
import com.nic.KaushalPanjeeApp.helper.Response;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/kaushalpanjee/panjeeapi")
public class TrainingCenterController {
	@Autowired
	TrainingCenterService service;

	@PostMapping("/kpPiaOrgList")
	public ResponseEntity<?> getPiaDetails(@RequestBody LocationBean location, HttpServletRequest req,
			HttpServletResponse res) {
		Response<Map<String, Object>> response = service.getKpPiaDetails(location, req, res);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/kpPiaTrainingCenters")
	public ResponseEntity<?> getKpTrainingCenters(@RequestBody LocationBean location, HttpServletRequest req,
			HttpServletResponse res) {
		Response<Map<String, Object>> response = service.getKpTrainingCenters(location, req, res);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/kpOrgTrainingCenters")
	public ResponseEntity<?> getOrgKpTrainingCenters(@RequestBody LocationBean location, HttpServletRequest req,
			HttpServletResponse res) {
		Response<Map<String, Object>> response = service.getOrgKpTrainingCenters(location, req, res);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/trainingCenterCourse")
	public ResponseEntity<?> getTrainingCenterCourses(@RequestBody LocationBean location, HttpServletRequest req,
			HttpServletResponse res) {
		Response<Map<String, Object>> response = service.getTrainingCenterCourses(location, req, res);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/instituteCourse")
	public ResponseEntity<?> getInstituteCourses(@RequestBody LocationBean location, HttpServletRequest req,
			HttpServletResponse res) {
		Response<Map<String, Object>> response = service.getInstituteCourses(location, req, res);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/insertCandidateTrainingCenter")
	public ResponseEntity<?> insertCandidateTrainingCenter(@RequestBody LocationBean location, HttpServletRequest req,
			HttpServletResponse res) {
		Response<Map<String, Object>> response = service.insertCandidateTrainingCenter(location, req, res);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
