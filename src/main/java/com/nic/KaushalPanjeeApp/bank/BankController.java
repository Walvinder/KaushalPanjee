package com.nic.KaushalPanjeeApp.bank;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/kaushalpanjee/panjeeapi")
//@RequestMapping("/panjeeapi")
public class BankController {

	@Autowired
	BankService service;

	@PostMapping("/bankDetails")
	public ResponseEntity<?> getBankDetails(@RequestBody BankBean bank, HttpServletRequest req,
			HttpServletResponse res) {
		BankResponse<Map<String, Object>> response = service.getBankDetails(bank, req, res);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
