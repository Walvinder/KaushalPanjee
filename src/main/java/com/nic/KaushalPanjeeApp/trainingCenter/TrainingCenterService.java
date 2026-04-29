package com.nic.KaushalPanjeeApp.trainingCenter;

import java.util.Map;

import com.nic.KaushalPanjeeApp.helper.LocationBean;
import com.nic.KaushalPanjeeApp.helper.Response;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface TrainingCenterService {

	public Response<Map<String, Object>> getKpPiaDetails(LocationBean location, HttpServletRequest req,
			HttpServletResponse res);

	public Response<Map<String, Object>> getKpTrainingCenters(LocationBean location, HttpServletRequest req,
			HttpServletResponse res);

	public Response<Map<String, Object>> getOrgKpTrainingCenters(LocationBean location, HttpServletRequest req,
			HttpServletResponse res);

	public Response<Map<String, Object>> getTrainingCenterCourses(LocationBean location, HttpServletRequest req,
			HttpServletResponse res);

	public Response<Map<String, Object>> getInstituteCourses(LocationBean location, HttpServletRequest req,
			HttpServletResponse res);

	public Response<Map<String, Object>> insertCandidateTrainingCenter(LocationBean location, HttpServletRequest req,
			HttpServletResponse res);

}
