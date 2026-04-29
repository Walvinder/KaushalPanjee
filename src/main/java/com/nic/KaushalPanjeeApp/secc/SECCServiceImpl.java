package com.nic.KaushalPanjeeApp.secc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.nic.KaushalPanjeeApp.config.SecurityHeader;
import com.nic.KaushalPanjeeApp.helper.HandledException;
import com.nic.KaushalPanjeeApp.helper.LocationBean;
import com.nic.KaushalPanjeeApp.helper.Response;
import com.nic.KaushalPanjeeApp.location.LocationRepository;
import com.nic.KaushalPanjeeApp.otp.AuthenticationRepository;
import com.nic.KaushalPanjeeApp.otp.OtpRepository;
import com.nic.KaushalPanjeeApp.userdetails.UserDetailsRepository;
import com.nic.KaushalPanjeeApp.userdetails.UserDeviceInfoRepository;
import com.nic.KaushalPanjeeApp.userdetails.UserProfileRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class SECCServiceImpl {
	private final JdbcTemplate externalJdbcTemplate;

	@Autowired
	OtpRepository otpRepo;

	@Autowired
	LocationRepository repo;

	@Autowired
	UserDetailsRepository userRepo;

	@Autowired
	UserProfileRepository profileRepo;

	@Autowired
	UserDeviceInfoRepository deviceRepo;

	@Autowired
	AuthenticationRepository authRepo;

	@Autowired
	SecurityHeader bnkService;

	public SECCServiceImpl(@Qualifier("externalJdbcTemplate") JdbcTemplate externalJdbcTemplate) {
		this.externalJdbcTemplate = externalJdbcTemplate;
	}

	public Response<Map<String, Object>> getSeccDetails(LocationBean location, HttpServletRequest req,
			HttpServletResponse res) {
		Response<Map<String, Object>> response = new Response<>();
		List<Map<String, Object>> wrappedList = new ArrayList<>();
		try {

			String appVersion = location.getAppVersion();
			String status = otpRepo.appVersionStatus(appVersion);
			String candidateName = location.getSeccName();
			String conCandidateName = candidateName + "%";
			if ("Active".equals(status)) {
				String stateshortName = repo.getStateShortName(location.getLgdVillCode());
				String tableName = "";

				if (stateshortName != null && !stateshortName.isEmpty()) {
					tableName = stateshortName + "_secc";
				}

				if (tableName == null || tableName.trim().isEmpty()) {
					response.setResponseCode(303);
					response.setResponseDesc("Data Not available.");
					return response;
				}

				String[] villCodes = location.getLgdVillCode().split(",");
				String placeholders = String.join(",", Collections.nCopies(villCodes.length, "?"));

				String sql = "select distinct name as seccname, COALESCE(fathername, 'NA') as fathername, ahl_tin as ahltin from "
						+ tableName + " where village_code_lgd in (" + placeholders + ")" + " and name ilike '"
						+ conCandidateName + "'"
						+ " and excluded_or_included_or_deprivated in ('deprivation', 'inclusion')";

				Object[] params = villCodes;

				wrappedList = externalJdbcTemplate.queryForList(sql, params);

				if (wrappedList != null && wrappedList.size() != 0) {

					response.setWrappedList(wrappedList);
					response.setResponseCode(HttpStatus.OK.value());
					response.setResponseDesc(HttpStatus.OK.name());
					response.setResponseMsg("");
				} else {
					response.setWrappedList(wrappedList);
					response.setResponseCode(303);
					response.setResponseDesc("Something went wrong.");
					response.setResponseMsg("SECC not available.");

				}

			} else {
				response.setResponseCode(301);
				response.setResponseDesc("Please upgrade your app first.");
				response.setResponseMsg("Outdated app version.");
			}

		} catch (HandledException e) {
			e.printStackTrace();
			throw new HandledException(e.getCode(), e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new HandledException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
		}
		bnkService.setSecurityHeaders(res);
		return response;

	}

}
