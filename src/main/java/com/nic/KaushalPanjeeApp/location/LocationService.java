package com.nic.KaushalPanjeeApp.location;

import java.util.Map;

import com.nic.KaushalPanjeeApp.helper.BlockResponse;
import com.nic.KaushalPanjeeApp.helper.DistrictResponse;
import com.nic.KaushalPanjeeApp.helper.GrampanchayatResponse;
import com.nic.KaushalPanjeeApp.helper.LocationBean;
import com.nic.KaushalPanjeeApp.helper.Response;
import com.nic.KaushalPanjeeApp.helper.StateResponse;
import com.nic.KaushalPanjeeApp.helper.VillageResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface LocationService {

	public StateResponse<Map<String, Object>> getStateList(LocationBean location, HttpServletRequest req,
			HttpServletResponse res);

	public DistrictResponse<Map<String, Object>> getDistrictList(LocationBean location, HttpServletRequest req,
			HttpServletResponse res);

	public BlockResponse<Map<String, Object>> getBlockList(LocationBean location, HttpServletRequest req,
			HttpServletResponse res);

	public GrampanchayatResponse<Map<String, Object>> getGramPanchayatList(LocationBean location,
			HttpServletRequest req, HttpServletResponse res);

	public VillageResponse<Map<String, Object>> getVillageList(LocationBean location, HttpServletRequest req,
			HttpServletResponse res);

	public Response<Map<String, Object>> getUlbList(LocationBean location, HttpServletRequest req,
			HttpServletResponse res);

	public Response<Map<String, Object>> getUlbWardList(LocationBean location, HttpServletRequest req,
			HttpServletResponse res);

}
