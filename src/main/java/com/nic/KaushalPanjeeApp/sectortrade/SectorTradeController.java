package com.nic.KaushalPanjeeApp.sectortrade;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nic.KaushalPanjeeApp.helper.OtpBean;
import com.nic.KaushalPanjeeApp.helper.Response;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/kaushalpanjee/panjeeapi")
public class SectorTradeController {
	@Autowired
	private SectorTradeService service;

	@PostMapping("/sectorList")
	public ResponseEntity<?> getSectorList(@RequestBody OtpBean user, HttpServletRequest req, HttpServletResponse res)
			throws IOException {
		Response<Map<String, Object>> response = service.getSectorDetails(user, req, res);
		return new ResponseEntity<Response<Map<String, Object>>>(response, HttpStatus.OK);
	}

	@PostMapping("/tradeList")
	public ResponseEntity<?> getTradeList(@RequestBody OtpBean user, HttpServletRequest req, HttpServletResponse res)
			throws IOException {
		Response<Map<String, Object>> response = service.getTradeDetails(user, req, res);
		return new ResponseEntity<Response<Map<String, Object>>>(response, HttpStatus.OK);
	}
	
	@PostMapping("/searchTrade")
	public ResponseEntity<?> getTradeBySearch(@RequestBody OtpBean user, HttpServletRequest req, HttpServletResponse res)
			throws IOException {
		Response<Map<String, Object>> response = service.getTradeBySearch(user, req, res);
		return new ResponseEntity<Response<Map<String, Object>>>(response, HttpStatus.OK);
	}
}
