package com.nic.KaushalPanjeeApp.unnati;

import java.util.Map;

import com.nic.KaushalPanjeeApp.helper.Response;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface NregaUnnatiService {

	public Response<Map<String, Object>> getNregaUnnatiApplicants(NregaUnnatiApplicantBean user, HttpServletRequest req,
			HttpServletResponse res);

	public Response<Map<String, Object>> checkCandidateExistInUnnathi(CandidateExistsUnnathi user,
			HttpServletRequest req, HttpServletResponse res);

	public Response<Map<String, Object>> sendKpIdPassword(HttpServletRequest req, HttpServletResponse res);

}
