package com.nic.KaushalPanjeeApp.ojtAttendance;

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
public class OjtAttendanceController {
	
	@Autowired
	OjtAttendanceServide service;
	
	@PostMapping("/insertOjtCandidateAttendance")
	public ResponseEntity<?> insertOjtCandidateAttendance(@RequestBody OjtAttendanceBean user, HttpServletRequest req,
			HttpServletResponse res) {
		Response<Map<String, Object>> response = service.insertOjtCandidateAttendance(user, req, res);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
