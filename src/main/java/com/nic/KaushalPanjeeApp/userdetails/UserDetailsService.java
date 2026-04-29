package com.nic.KaushalPanjeeApp.userdetails;

import java.io.IOException;
import java.util.Map;

import com.nic.KaushalPanjeeApp.aadhaarLog.AadhaarTxnRequest;
import com.nic.KaushalPanjeeApp.aadhaarLog.BankDetailsResponse;
import com.nic.KaushalPanjeeApp.aadhaarLog.BankRequest;
import com.nic.KaushalPanjeeApp.helper.MasterResponse;
import com.nic.KaushalPanjeeApp.helper.OtpResponse;
import com.nic.KaushalPanjeeApp.helper.Response;
import com.nic.KaushalPanjeeApp.helper.UserCreationBean;
import com.nic.KaushalPanjeeApp.helper.UserDetailsBean;
import com.nic.KaushalPanjeeApp.helper.UserDeviceBean;
import com.nic.KaushalPanjeeApp.helper.UserPassword;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface UserDetailsService {

	public Response<Map<String, Object>> createUser(UserCreationBean user, HttpServletRequest req,
			HttpServletResponse res) throws IOException;

	public Response<Map<String, Object>> loginUser(UserDeviceBean login, HttpServletRequest req,
			HttpServletResponse res);

	public Response<Map<String, Object>> getUserDetails(UserDeviceBean user, HttpServletRequest req,
			HttpServletResponse res);

	public Response<Map<String, Object>> insertCandidateDetails(UserDetailsBean user, HttpServletRequest req,
			HttpServletResponse res);

	public Response<Map<String, Object>> sectionStatus(UserDetailsBean user, HttpServletRequest req,
			HttpServletResponse res);

	public MasterResponse<Map<String, Object>> getCandidateMasterList(UserDetailsBean user, HttpServletRequest req,
			HttpServletResponse res);

	public Response<Map<String, Object>> changeProfileImage(UserDetailsBean user, HttpServletRequest req,
			HttpServletResponse res);

	public OtpResponse<Map<String, Object>> getUserLoginIdPassword(UserPassword pass, HttpServletRequest req,
			HttpServletResponse res);

	public Response<Map<String, Object>> checkUserExistance(UserPassword pass, HttpServletRequest req,
			HttpServletResponse res);

	public Response<Map<String, Object>> updateUserAadhaarSHA();

	public Response<Map<String, Object>> updateFaceRegistred(UserPassword request, HttpServletRequest req,
			HttpServletResponse res);

	public Response<Map<String, Object>> updateUserAadhaarDetails(UserCreationBean user, HttpServletRequest req,
			HttpServletResponse res);

	public Response<Map<String, Object>> updateCandidateEmailId(UserCreationBean user, HttpServletRequest req,
			HttpServletResponse res);

	public Response<Map<String, Object>> userLogOut(UserDeviceBean login, HttpServletRequest req,
			HttpServletResponse res);

	public BankDetailsResponse<Map<String, Object>> getBankList(BankRequest request, HttpServletRequest req,
			HttpServletResponse res);

	public Response<Map<String, Object>> saveAadhaarTxn(AadhaarTxnRequest request, HttpServletResponse res);

}
