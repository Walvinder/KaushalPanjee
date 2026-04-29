package com.nic.KaushalPanjeeApp.otp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AuthenticationRepository extends JpaRepository<AuthenticationEntity, Long> {

	@Query(value = "select pass_code from mst_candidate_auth where imei_no = ?1 limit 1", nativeQuery = true)
	String getPassString(String imeiNo);

	@Query(value = "select auth_token from mst_candidate_auth where imei_no = ?1 limit 1", nativeQuery = true)
	String getAuthenticationToken(String imeiNo);

	@Query(value = "select * from mst_candidate_auth where imei_no = ?1 limit 1", nativeQuery = true)
	AuthenticationEntity getAuthenticationInstance(String imeiNo);

	@Query(value = "select * from mst_candidate_auth where login_id = ?1 limit 1", nativeQuery = true)
	AuthenticationEntity getAuthInstance(String loginId);

	@Query(value = "select exists( select 1 from mst_candidate_auth where login_id = ?1 and auth_token =?2)", nativeQuery = true)
	boolean getTokenValidation(String loginId, String authToken);

	@Query(value = "select exists( select 1 from mst_candidate_auth where login_id = ?1 and auth_token =?2 and imei_no = ?3 )", nativeQuery = true)
	boolean getTokenValidationSection(String loginId, String authToken, String imeiNo);

	@Query(value = "select exists( select 1 from mst_candidate_auth where login_id = ?1)", nativeQuery = true)
	boolean checkAuthInstanceExists(String loginId);

}
