package com.nic.KaushalPanjeeApp.secc;

import java.util.Map;

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
public class SECCController {

	private final SECCServiceImpl service;

	public SECCController(SECCServiceImpl service) {
		this.service = service;
	}

	@PostMapping("/seccDetails")
	public ResponseEntity<?> getSeccDetails(@RequestBody LocationBean location, HttpServletRequest req,
			HttpServletResponse res) {
		Response<Map<String, Object>> response = service.getSeccDetails(location, req, res);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
