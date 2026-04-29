package com.nic.KaushalPanjeeApp.sectortrade;

import java.util.Map;

import com.nic.KaushalPanjeeApp.helper.OtpBean;
import com.nic.KaushalPanjeeApp.helper.Response;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface SectorTradeService {

	public Response<Map<String, Object>> getSectorDetails(OtpBean user, HttpServletRequest req,
			HttpServletResponse res);

	public Response<Map<String, Object>> getTradeDetails(OtpBean user, HttpServletRequest req, HttpServletResponse res);

	public Response<Map<String, Object>> getTradeBySearch(OtpBean user, HttpServletRequest req,
			HttpServletResponse res);

}
