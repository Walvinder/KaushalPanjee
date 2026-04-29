package com.nic.KaushalPanjeeApp.tech;

import java.util.Map;

import com.nic.KaushalPanjeeApp.helper.BannerResponse;
import com.nic.KaushalPanjeeApp.helper.LanguageResponse;
import com.nic.KaushalPanjeeApp.helper.OtpBean;
import com.nic.KaushalPanjeeApp.helper.Response;
import com.nic.KaushalPanjeeApp.helper.SurveyResponse;
import com.nic.KaushalPanjeeApp.helper.TechDomainResponse;
import com.nic.KaushalPanjeeApp.helper.TechQualificationResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface TechnicalDetailsService {

	public TechQualificationResponse<Map<String, Object>> getTechQualificationList(OtpBean qual, HttpServletRequest req,
			HttpServletResponse res);

	public TechDomainResponse<Map<String, Object>> getQualificationDomainList(OtpBean qual, HttpServletRequest req,
			HttpServletResponse res);

	public SurveyResponse<Map<String, Object>> getSurveyList(OtpBean appversion, HttpServletRequest req,
			HttpServletResponse res);

	public LanguageResponse<Map<String, Object>> getLanguageList(OtpBean appversion, HttpServletRequest req,
			HttpServletResponse res);

	public LanguageResponse<Map<String, Object>> getTrainingCenterList(OtpBean taining, HttpServletRequest req,
			HttpServletResponse res);

	public LanguageResponse<Map<String, Object>> searchTrainingCenterList(OtpBean taining, HttpServletRequest req,
			HttpServletResponse res);

	public LanguageResponse<Map<String, Object>> getTrainingCenter(OtpBean taining, HttpServletRequest req,
			HttpServletResponse res);

	public BannerResponse<Map<String, Object>> getBanner(OtpBean taining, HttpServletRequest req,
			HttpServletResponse res);

	public Response<Map<String, Object>> getSchemeList(OtpBean taining, HttpServletRequest req,
			HttpServletResponse res);

}
