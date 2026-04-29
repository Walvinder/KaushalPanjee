package com.nic.KaushalPanjeeApp.sectortrade;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.nic.KaushalPanjeeApp.config.SecurityHeader;
import com.nic.KaushalPanjeeApp.helper.HandledException;
import com.nic.KaushalPanjeeApp.helper.OtpBean;
import com.nic.KaushalPanjeeApp.helper.Response;
import com.nic.KaushalPanjeeApp.otp.AuthenticationEntity;
import com.nic.KaushalPanjeeApp.otp.AuthenticationRepository;
import com.nic.KaushalPanjeeApp.otp.OtpRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class SectorTradeServiceImpl implements SectorTradeService {
	@Autowired
	private SectorTradeRepository apiDataRepository;

	@Autowired
	OtpRepository otpRepo;

	@Autowired
	AuthenticationRepository authRepo;

	@Autowired
	SecurityHeader bnkService;

	public Response<Map<String, Object>> getSectorDetails(OtpBean user, HttpServletRequest req,
			HttpServletResponse res) {
		Response<Map<String, Object>> response = new Response<>();
		List<Map<String, Object>> wrappedList = new ArrayList<>();
		List<Object[]> sectorList = null;
		String loginId = user.getLoginId();

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
				String appVersion = user.getAppVersion();
				String status = otpRepo.appVersionStatus(appVersion);

				if (status != null && status.equals("Active") && !status.equals("null")) {

					sectorList = apiDataRepository.getSectorList();

					if (sectorList != null && sectorList.size() != 0) {
						wrappedList = sectorList.stream().map(p -> {
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("sectorId", p[0]);
							map.put("sectorName", p[1]);
							return map;
						})

								.collect(Collectors.toList());

						response.setWrappedList(wrappedList);
						response.setResponseCode(HttpStatus.OK.value());
						response.setResponseDesc(HttpStatus.OK.name());
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

	public Response<Map<String, Object>> getTradeDetails(OtpBean user, HttpServletRequest req,
			HttpServletResponse res) {
		Response<Map<String, Object>> response = new Response<>();
		List<Map<String, Object>> wrappedList = new ArrayList<>();
		List<Object[]> tradeList = null;
		String loginId = user.getLoginId();

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
				String appVersion = user.getAppVersion();
				String status = otpRepo.appVersionStatus(appVersion);
				String sectorId = user.getSectorId();
				List<String> sectorIdList;

				if (sectorId.contains(",")) {
					sectorIdList = Arrays.asList(sectorId.split(",")); // Split into list if multiple
				} else {
					sectorIdList = Collections.singletonList(sectorId); // Wrap single ID in list
				}

				// Continue only if app version status is active
				if (status != null && status.equalsIgnoreCase("Active")) {
					// Convert List<String> to String[]
					String[] sectorIdArray = sectorIdList.toArray(new String[0]);

					Integer[] sectorIdIntArray = new Integer[sectorIdArray.length];

					for (int i = 0; i < sectorIdArray.length; i++) {
						try {
							sectorIdIntArray[i] = Integer.parseInt(sectorIdArray[i].trim()); // Trim to avoid any
																								// leading/trailing
																								// spaces
						} catch (NumberFormatException e) {
							System.err.println("Invalid sector ID '" + sectorIdArray[i] + "': " + e.getMessage());
							// Handle the error as needed, e.g., skip or set to a default value
						}
					}
					// Fetch trade list from repository
					tradeList = apiDataRepository.getTradeList(sectorIdIntArray);

					if (tradeList != null && !tradeList.isEmpty()) {

						wrappedList = tradeList.stream().map(p -> {
							Map<String, Object> map = new HashMap<>();
							map.put("tradeCode", p[0]);
							map.put("trade", p[1]);
							return map;
						}).collect(Collectors.toList());

						response.setWrappedList(wrappedList);
						response.setResponseCode(HttpStatus.OK.value());
						response.setResponseDesc(HttpStatus.OK.name());
					} else {
						// Handle case where no trade list is returned
						response.setWrappedList(new ArrayList<>());
						response.setResponseCode(HttpStatus.NO_CONTENT.value());
						response.setResponseDesc("No trades found.");
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

	public Response<Map<String, Object>> getTradeBySearch(OtpBean user, HttpServletRequest req,
			HttpServletResponse res) {
		Response<Map<String, Object>> response = new Response<>();
		List<Map<String, Object>> wrappedList = new ArrayList<>();
		List<Object[]> tradeList = null;
		String loginId = user.getLoginId();

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
				String appVersion = user.getAppVersion();
				String status = otpRepo.appVersionStatus(appVersion);

				if (status != null && status.equalsIgnoreCase("Active")) {

					String tradeName = user.getTradeName();
					String conTradeName = "%" + tradeName + "%";
					System.out.println("Trade Name:  " + conTradeName);
					
					if(user.getSchemeType().trim().equals("DDUGKY")) {
						tradeList = apiDataRepository.getDdugkyTradeBySearchTradeName(conTradeName);
					}else if(user.getSchemeType().trim().equals("RSETI")) {
						tradeList = apiDataRepository.getRsetiTradeBySearchTradeName(conTradeName);
					}else {
						tradeList = null;
					}
					

					if (tradeList != null && !tradeList.isEmpty()) {

						wrappedList = tradeList.stream().map(p -> {
							Map<String, Object> map = new HashMap<>();
							map.put("tradeCode", p[2]);
							map.put("trade", p[3]);
							map.put("sectorId", p[0]);
							map.put("sectorName", p[1]);
							return map;
						}).collect(Collectors.toList());

						response.setWrappedList(wrappedList);
						response.setResponseCode(HttpStatus.OK.value());
						response.setResponseDesc(HttpStatus.OK.name());
					} else {
						// Handle case where no trade list is returned
						response.setWrappedList(new ArrayList<>());
						response.setResponseCode(HttpStatus.NO_CONTENT.value());
						response.setResponseDesc("No trades found.");
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
