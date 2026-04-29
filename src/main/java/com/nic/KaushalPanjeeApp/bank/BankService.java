package com.nic.KaushalPanjeeApp.bank;

import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface BankService {

	public BankResponse<Map<String, Object>> getBankDetails(BankBean bank, HttpServletRequest req,
			HttpServletResponse res);

}
