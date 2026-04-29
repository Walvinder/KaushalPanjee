package com.nic.KaushalPanjeeApp.location;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nic.KaushalPanjeeApp.helper.BlockResponse;
import com.nic.KaushalPanjeeApp.helper.DistrictResponse;
import com.nic.KaushalPanjeeApp.helper.GrampanchayatResponse;
import com.nic.KaushalPanjeeApp.helper.LocationBean;
import com.nic.KaushalPanjeeApp.helper.Response;
import com.nic.KaushalPanjeeApp.helper.StateResponse;
import com.nic.KaushalPanjeeApp.helper.VillageResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/kaushalpanjee/panjeeapi")
//@RequestMapping("/panjeeapi")
public class LocationController {

	@Autowired
	LocationService service;

	@PostMapping("/stateList")
	public ResponseEntity<?> lgdStateList(@RequestBody LocationBean location, HttpServletRequest req,
			HttpServletResponse res) {
		StateResponse<Map<String, Object>> response = service.getStateList(location, req, res);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/districtList")
	public ResponseEntity<?> lgdDistrictList(@RequestBody LocationBean location, HttpServletRequest req,
			HttpServletResponse res) {
		DistrictResponse<Map<String, Object>> response = service.getDistrictList(location, req, res);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/blockList")
	public ResponseEntity<?> lgdBlockList(@RequestBody LocationBean location, HttpServletRequest req,
			HttpServletResponse res) {
		BlockResponse<Map<String, Object>> response = service.getBlockList(location, req, res);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/gramPanchayatList")
	public ResponseEntity<?> lgdGramPanchayatList(@RequestBody LocationBean location, HttpServletRequest req,
			HttpServletResponse res) {
		GrampanchayatResponse<Map<String, Object>> response = service.getGramPanchayatList(location, req, res);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/villageList")
	public ResponseEntity<?> lgdVillageList(@RequestBody LocationBean location, HttpServletRequest req,
			HttpServletResponse res) {
		VillageResponse<Map<String, Object>> response = service.getVillageList(location, req, res);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/ulbList")
	public ResponseEntity<?> getUlbList(@RequestBody LocationBean location, HttpServletRequest req,
			HttpServletResponse res) {
		Response<Map<String, Object>> response = service.getUlbList(location, req, res);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/wardList")
	public ResponseEntity<?> getWardList(@RequestBody LocationBean location, HttpServletRequest req,
			HttpServletResponse res) {
		Response<Map<String, Object>> response = service.getUlbWardList(location, req, res);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
