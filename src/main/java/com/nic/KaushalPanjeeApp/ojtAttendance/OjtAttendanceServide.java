package com.nic.KaushalPanjeeApp.ojtAttendance;

import java.util.Map;

import com.nic.KaushalPanjeeApp.helper.Response;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface OjtAttendanceServide {

	public Response<Map<String, Object>> insertOjtCandidateAttendance(OjtAttendanceBean user, HttpServletRequest req,
			HttpServletResponse res);

}
