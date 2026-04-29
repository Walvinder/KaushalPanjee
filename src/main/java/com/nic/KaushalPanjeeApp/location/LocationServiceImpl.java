package com.nic.KaushalPanjeeApp.location;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.nic.KaushalPanjeeApp.config.SecurityHeader;
import com.nic.KaushalPanjeeApp.helper.BlockResponse;
import com.nic.KaushalPanjeeApp.helper.DistrictResponse;
import com.nic.KaushalPanjeeApp.helper.GrampanchayatResponse;
import com.nic.KaushalPanjeeApp.helper.HandledException;
import com.nic.KaushalPanjeeApp.helper.LocationBean;
import com.nic.KaushalPanjeeApp.helper.Response;
import com.nic.KaushalPanjeeApp.helper.StateResponse;
import com.nic.KaushalPanjeeApp.helper.VillageResponse;
import com.nic.KaushalPanjeeApp.otp.AuthenticationEntity;
import com.nic.KaushalPanjeeApp.otp.AuthenticationRepository;
import com.nic.KaushalPanjeeApp.otp.OtpRepository;
import com.nic.KaushalPanjeeApp.userdetails.UserDetailsRepository;
import com.nic.KaushalPanjeeApp.userdetails.UserDeviceInfoRepository;
import com.nic.KaushalPanjeeApp.userdetails.UserProfileRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class LocationServiceImpl implements LocationService {

	@Autowired
	LocationRepository repo;

	@Autowired
	OtpRepository otpRepo;

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

	@Override
	public StateResponse<Map<String, Object>> getStateList(LocationBean location, HttpServletRequest req,
			HttpServletResponse res) {
		StateResponse<Map<String, Object>> response = new StateResponse<>();
		List<Map<String, Object>> stateList = new ArrayList<>();
		List<Object[]> state = null;
		try {
			String appVersion = location.getAppVersion();
			String status = otpRepo.appVersionStatus(appVersion);

			if (status != null && status.equals("Active") && !status.equals("null")) {

				state = repo.getStateList();

				if (state != null && state.size() != 0) {

					stateList = state.stream().map(p -> {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("stateCode", p[0]);
						map.put("stateName", p[1]);
						if (p[2] == null) {
							map.put("lgdStateCode", "");
						} else {
							map.put("lgdStateCode", p[2]);
						}
						return map;
					})

							.collect(Collectors.toList());

					response.setStateList(stateList);
					response.setResponseCode(HttpStatus.OK.value());
					response.setResponseDesc(HttpStatus.OK.name());
					response.setResponseMsg("");
				} else {
					response.setStateList(null);
					response.setResponseCode(303);
					response.setResponseDesc("Something went wrong.");
					response.setResponseMsg("State not available.");

				}

			} else {
				response.setResponseCode(301);
				response.setResponseDesc("Please upgread your app first.");
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

	public DistrictResponse<Map<String, Object>> getDistrictList(LocationBean location, HttpServletRequest req,
			HttpServletResponse res) {
		DistrictResponse<Map<String, Object>> response = new DistrictResponse<>();
		List<Map<String, Object>> wrappedList = new ArrayList<>();
		List<Object[]> districtList = null;
		String loginId = location.getLoginId();

		Timestamp currentTimestamp = Timestamp.from(Instant.now());
		AuthenticationEntity authinstance = authRepo.getAuthInstance(loginId);
		if (authinstance == null) {
			response.setResponseCode(HttpStatus.UNAUTHORIZED.value());
			response.setResponseDesc(HttpStatus.UNAUTHORIZED.name());
			return response;
		}
		Date updatedDate = authinstance.getUpdatedOn();
		Instant updatedInstant = updatedDate.toInstant();
		Instant currentInstant = currentTimestamp.toInstant();
		long resultTime = ChronoUnit.MINUTES.between(updatedInstant, currentInstant);
		if (1440 - resultTime <= 0) {
			authRepo.delete(authinstance);
			response.setResponseCode(HttpStatus.UNAUTHORIZED.value());
			response.setResponseDesc(HttpStatus.UNAUTHORIZED.name());
			return response;
		}
		String authHeader = req.getHeader("Authorization");
		String authToken = null;

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			authToken = authHeader.substring(7); // Remove "Bearer " prefix
		}

		boolean authFlag = authRepo.getTokenValidation(loginId, authToken);
		try {
			if (authFlag) {
				String appVersion = location.getAppVersion();
				String status = otpRepo.appVersionStatus(appVersion);
				if (status != null && status.equals("Active") && !status.equals("null")) {
					districtList = repo.getDistrictList(location.getStateCode());

					if (districtList != null && districtList.size() != 0) {

						wrappedList = districtList.stream().map(p -> {
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("districtCode", p[0]);
							map.put("districtName", p[1]);
							if (p[2] == null) {
								map.put("lgdDistrictCode", "");
							} else {
								map.put("lgdDistrictCode", p[2]);
							}
							return map;
						})

								.collect(Collectors.toList());
						response.setDistrictList(wrappedList);
						response.setResponseCode(HttpStatus.OK.value());
						response.setResponseDesc(HttpStatus.OK.name());

					} else {
						response.setDistrictList(null);
						response.setResponseCode(303);
						response.setResponseDesc("Something went wrong.");
						response.setResponseMsg("District not available.");

					}
				} else {
					response.setResponseCode(301);
					response.setResponseDesc("Please upgread your app first.");
					response.setResponseMsg("Outdated app version.");
				}
			} else {
				response.setResponseCode(HttpStatus.UNAUTHORIZED.value());
				response.setResponseDesc(HttpStatus.UNAUTHORIZED.name());
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

	public BlockResponse<Map<String, Object>> getBlockList(LocationBean location, HttpServletRequest req,
			HttpServletResponse res) {
		BlockResponse<Map<String, Object>> response = new BlockResponse<>();
		List<Map<String, Object>> wrappedList = new ArrayList<>();
		List<Object[]> blockList = null;
		String loginId = location.getLoginId();
		Timestamp currentTimestamp = Timestamp.from(Instant.now());
		AuthenticationEntity authinstance = authRepo.getAuthInstance(loginId);
		if (authinstance == null) {
			response.setResponseCode(HttpStatus.UNAUTHORIZED.value());
			response.setResponseDesc(HttpStatus.UNAUTHORIZED.name());
			return response;
		}
		Date updatedDate = authinstance.getUpdatedOn();
		Instant updatedInstant = updatedDate.toInstant();
		Instant currentInstant = currentTimestamp.toInstant();
		long resultTime = ChronoUnit.MINUTES.between(updatedInstant, currentInstant);
		if (1440 - resultTime <= 0) {
			authRepo.delete(authinstance);
			response.setResponseCode(HttpStatus.UNAUTHORIZED.value());
			response.setResponseDesc(HttpStatus.UNAUTHORIZED.name());
			return response;
		}
		String authHeader = req.getHeader("Authorization");
		String authToken = null;

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			authToken = authHeader.substring(7); // Remove "Bearer " prefix
		}

		boolean authFlag = authRepo.getTokenValidation(loginId, authToken);

		try {
			if (authFlag) {
				String appVersion = location.getAppVersion();
				String status = otpRepo.appVersionStatus(appVersion);
				if (status != null && status.equals("Active") && !status.equals("null")) {

					blockList = repo.getBlockList(location.getDistrictCode());

					if (blockList != null && blockList.size() != 0) {

						wrappedList = blockList.stream().map(p -> {
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("blockCode", p[0]);
							map.put("blockName", p[1]);
							if (p[2] == null) {
								map.put("lgdBlockCode", "");
							} else {
								map.put("lgdBlockCode", p[2]);
							}
							return map;
						})

								.collect(Collectors.toList());

						response.setBlockList(wrappedList);
						response.setResponseCode(HttpStatus.OK.value());
						response.setResponseDesc(HttpStatus.OK.name());
						response.setResponseMsg("");
					} else {
						response.setBlockList(null);
						response.setResponseCode(303);
						response.setResponseDesc("Something went wrong.");
						response.setResponseMsg("Block not available.");

					}

				} else {
					response.setResponseCode(301);
					response.setResponseDesc("Please upgread your app first.");
					response.setResponseMsg("Outdated app version.");
				}
			} else {
				response.setResponseCode(HttpStatus.UNAUTHORIZED.value());
				response.setResponseDesc(HttpStatus.UNAUTHORIZED.name());
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

	public GrampanchayatResponse<Map<String, Object>> getGramPanchayatList(LocationBean location,
			HttpServletRequest req, HttpServletResponse res) {
		GrampanchayatResponse<Map<String, Object>> response = new GrampanchayatResponse<>();
		List<Map<String, Object>> wrappedList = new ArrayList<>();
		List<Object[]> gpList = null;
		String loginId = location.getLoginId();

		Timestamp currentTimestamp = Timestamp.from(Instant.now());
		AuthenticationEntity authinstance = authRepo.getAuthInstance(loginId);
		if (authinstance == null) {
			response.setResponseCode(HttpStatus.UNAUTHORIZED.value());
			response.setResponseDesc(HttpStatus.UNAUTHORIZED.name());
			return response;
		}
		Date updatedDate = authinstance.getUpdatedOn();
		Instant updatedInstant = updatedDate.toInstant();
		Instant currentInstant = currentTimestamp.toInstant();
		long resultTime = ChronoUnit.MINUTES.between(updatedInstant, currentInstant);
		if (1440 - resultTime <= 0) {
			authRepo.delete(authinstance);
			response.setResponseCode(HttpStatus.UNAUTHORIZED.value());
			response.setResponseDesc(HttpStatus.UNAUTHORIZED.name());
			return response;
		}
		String authHeader = req.getHeader("Authorization");
		String authToken = null;

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			authToken = authHeader.substring(7); // Remove "Bearer " prefix
		}

		boolean authFlag = authRepo.getTokenValidation(loginId, authToken);
		try {
			if (authFlag) {
				String appVersion = location.getAppVersion();
				String status = otpRepo.appVersionStatus(appVersion);
				if (status != null && status.equals("Active") && !status.equals("null")) {
					gpList = repo.getGramPanchayatList(location.getBlockCode());

					if (gpList != null && gpList.size() != 0) {

						wrappedList = gpList.stream().map(p -> {
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("gpCode", p[0]);
							map.put("gpName", p[1]);
							if (p[2] == null) {
								map.put("lgdGpCode", "");
							} else {
								map.put("lgdGpCode", p[2]);
							}

							return map;
						})

								.collect(Collectors.toList());
						response.setGrampanchayatList(wrappedList);
						response.setResponseCode(HttpStatus.OK.value());
						response.setResponseDesc(HttpStatus.OK.name());
						response.setResponseMsg("");
					} else {
						response.setGrampanchayatList(wrappedList);
						response.setResponseCode(303);
						response.setResponseDesc("Something went wrong.");
						response.setResponseMsg("Grampanchayat not available.");

					}

				} else {
					response.setResponseCode(301);
					response.setResponseDesc("Please upgread your app first.");
					response.setResponseMsg("Outdated app version.");
				}
			} else {
				response.setResponseCode(HttpStatus.UNAUTHORIZED.value());
				response.setResponseDesc(HttpStatus.UNAUTHORIZED.name());
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

	public VillageResponse<Map<String, Object>> getVillageList(LocationBean location, HttpServletRequest req,
			HttpServletResponse res) {
		VillageResponse<Map<String, Object>> response = new VillageResponse<>();
		List<Map<String, Object>> wrappedList = new ArrayList<>();
		List<Object[]> villageList = null;
		String loginId = location.getLoginId();

		Timestamp currentTimestamp = Timestamp.from(Instant.now());
		AuthenticationEntity authinstance = authRepo.getAuthInstance(loginId);
		if (authinstance == null) {
			response.setResponseCode(HttpStatus.UNAUTHORIZED.value());
			response.setResponseDesc(HttpStatus.UNAUTHORIZED.name());
			return response;
		}
		Date updatedDate = authinstance.getUpdatedOn();
		Instant updatedInstant = updatedDate.toInstant();
		Instant currentInstant = currentTimestamp.toInstant();
		long resultTime = ChronoUnit.MINUTES.between(updatedInstant, currentInstant);
		if (1440 - resultTime <= 0) {
			authRepo.delete(authinstance);
			response.setResponseCode(HttpStatus.UNAUTHORIZED.value());
			response.setResponseDesc(HttpStatus.UNAUTHORIZED.name());
			return response;
		}
		String authHeader = req.getHeader("Authorization");
		String authToken = null;

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			authToken = authHeader.substring(7); // Remove "Bearer " prefix
		}

		boolean authFlag = authRepo.getTokenValidation(loginId, authToken);
		try {
			if (authFlag) {
				String appVersion = location.getAppVersion();
				String status = otpRepo.appVersionStatus(appVersion);
				if (status != null && status.equals("Active") && !status.equals("null")) {
					villageList = repo.getVillageList(location.getGpCode());

					if (villageList != null && villageList.size() != 0) {

						wrappedList = villageList.stream().map(p -> {
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("villageCode", p[0]);
							map.put("villageName", p[1]);
							if (p[2] == null) {
								map.put("lgdVillageCode", "");
							} else {
								map.put("lgdVillageCode", p[2]);
							}

							return map;
						})

								.collect(Collectors.toList());
						response.setVillageList(wrappedList);
						response.setResponseCode(HttpStatus.OK.value());
						response.setResponseDesc(HttpStatus.OK.name());
						response.setResponseMsg("");
					} else {
						response.setVillageList(wrappedList);
						response.setResponseCode(303);
						response.setResponseDesc("Something went wrong.");
						response.setResponseMsg("Village not available.");

					}

				} else {
					response.setResponseCode(301);
					response.setResponseDesc("Please upgread your app first.");
					response.setResponseMsg("Outdated app version.");
				}
			} else {
				response.setResponseCode(HttpStatus.UNAUTHORIZED.value());
				response.setResponseDesc(HttpStatus.UNAUTHORIZED.name());
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

	public Response<Map<String, Object>> getUlbList(LocationBean location, HttpServletRequest req,
			HttpServletResponse res) {
		Response<Map<String, Object>> response = new Response<>();
		List<Map<String, Object>> wrappedList = new ArrayList<>();
		List<Object[]> ulbList = null;
		String loginId = location.getLoginId();
		Timestamp currentTimestamp = Timestamp.from(Instant.now());
		AuthenticationEntity authinstance = authRepo.getAuthInstance(loginId);
		if (authinstance == null) {
			response.setResponseCode(HttpStatus.UNAUTHORIZED.value());
			response.setResponseDesc(HttpStatus.UNAUTHORIZED.name());
			return response;
		}
		Date updatedDate = authinstance.getUpdatedOn();
		Instant updatedInstant = updatedDate.toInstant();
		Instant currentInstant = currentTimestamp.toInstant();
		long resultTime = ChronoUnit.MINUTES.between(updatedInstant, currentInstant);
		if (1440 - resultTime <= 0) {
			authRepo.delete(authinstance);
			response.setResponseCode(HttpStatus.UNAUTHORIZED.value());
			response.setResponseDesc(HttpStatus.UNAUTHORIZED.name());
			return response;
		}
		String authHeader = req.getHeader("Authorization");
		String authToken = null;

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			authToken = authHeader.substring(7); // Remove "Bearer " prefix
		}

		boolean authFlag = authRepo.getTokenValidation(loginId, authToken);

		try {
			if (authFlag) {
				String appVersion = location.getAppVersion();
				String status = otpRepo.appVersionStatus(appVersion);
				if (status != null && status.equals("Active") && !status.equals("null")) {

					ulbList = repo.getUlbList(location.getLgdDistrictCode());

					if (ulbList != null && ulbList.size() != 0) {

						wrappedList = ulbList.stream().map(p -> {
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("ulbCode", p[0]);
							map.put("ulbName", p[1]);
							return map;
						})

								.collect(Collectors.toList());

						response.setWrappedList(wrappedList);
						response.setResponseCode(HttpStatus.OK.value());
						response.setResponseDesc(HttpStatus.OK.name());
						response.setResponseMsg("");
					} else {
						response.setWrappedList(null);
						response.setResponseCode(303);
						response.setResponseDesc("Something went wrong.");
						response.setResponseMsg("Urben local body not available.");

					}

				} else {
					response.setResponseCode(301);
					response.setResponseDesc("Please upgread your app first.");
					response.setResponseMsg("Outdated app version.");
				}
			} else {
				response.setResponseCode(HttpStatus.UNAUTHORIZED.value());
				response.setResponseDesc(HttpStatus.UNAUTHORIZED.name());
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

	public Response<Map<String, Object>> getUlbWardList(LocationBean location, HttpServletRequest req,
			HttpServletResponse res) {
		Response<Map<String, Object>> response = new Response<>();
		List<Map<String, Object>> wrappedList = new ArrayList<>();
		List<Object[]> ulbWardList = null;
		String loginId = location.getLoginId();
		Timestamp currentTimestamp = Timestamp.from(Instant.now());
		AuthenticationEntity authinstance = authRepo.getAuthInstance(loginId);
		if (authinstance == null) {
			response.setResponseCode(HttpStatus.UNAUTHORIZED.value());
			response.setResponseDesc(HttpStatus.UNAUTHORIZED.name());
			return response;
		}
		Date updatedDate = authinstance.getUpdatedOn();
		Instant updatedInstant = updatedDate.toInstant();
		Instant currentInstant = currentTimestamp.toInstant();
		long resultTime = ChronoUnit.MINUTES.between(updatedInstant, currentInstant);
		if (1440 - resultTime <= 0) {
			authRepo.delete(authinstance);
			response.setResponseCode(HttpStatus.UNAUTHORIZED.value());
			response.setResponseDesc(HttpStatus.UNAUTHORIZED.name());
			return response;
		}
		String authHeader = req.getHeader("Authorization");
		String authToken = null;

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			authToken = authHeader.substring(7); // Remove "Bearer " prefix
		}

		boolean authFlag = authRepo.getTokenValidation(loginId, authToken);

		try {
			if (authFlag) {
				String appVersion = location.getAppVersion();
				String status = otpRepo.appVersionStatus(appVersion);
				if (status != null && status.equals("Active") && !status.equals("null")) {

					ulbWardList = repo.getUlbWardList(location.getUlbCode());

					if (ulbWardList != null && ulbWardList.size() != 0) {

						wrappedList = ulbWardList.stream().map(p -> {
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("wardCode", p[0]);
							map.put("wardName", p[1]);
							return map;
						})

								.collect(Collectors.toList());

						response.setWrappedList(wrappedList);
						response.setResponseCode(HttpStatus.OK.value());
						response.setResponseDesc(HttpStatus.OK.name());
						response.setResponseMsg("");
					} else {
						response.setWrappedList(null);
						response.setResponseCode(303);
						response.setResponseDesc("Something went wrong.");
						response.setResponseMsg("Ward not available.");

					}

				} else {
					response.setResponseCode(301);
					response.setResponseDesc("Please upgread your app first.");
					response.setResponseMsg("Outdated app version.");
				}
			} else {
				response.setResponseCode(HttpStatus.UNAUTHORIZED.value());
				response.setResponseDesc(HttpStatus.UNAUTHORIZED.name());
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
