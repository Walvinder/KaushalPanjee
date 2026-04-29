package com.nic.KaushalPanjeeApp.otp;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OtpRepository extends JpaRepository<CandidateOtp, Long> {

	@Query(value = "SELECT EXISTS ( SELECT 1 FROM mst_candidate_otp WHERE mobile_no = ?1 AND otp = ?2 AND imei_no = ?3 AND is_verified = false)", nativeQuery = true)
	Boolean getStatus(String mobileNo, String otp, String imeiNo);

	@Query(value = "select exists(select 1 from mst_candidate_otp  where mobile_no = ?1 and email_id = ?2 and otp = ?3 and imei_no = ?4 and is_verified = false)", nativeQuery = true)
	Boolean getForgetIdPassStatus(String mobileNo, String emailId, String userOtp, String imeiNo);

	@Query(value = "select exists( select 1 from mst_candidate_otp where email_id = ?1 and otp = ?2 and imei_no = ?3 and is_verified = false)", nativeQuery = true)
	Boolean getEmailStatus(String email, String otp, String imeiNo);

	@Modifying
	@Query(value = "update kaushal_otp set is_verified = :status where mobile_number =:mobileNo and otp = :otp", nativeQuery = true)
	public void updateVerifiedStatus(@Param("status") Boolean status, @Param("mobileNo") String mobileNo,
			@Param("otp") String otp);

	@Modifying
	@Query(value = "update kaushal_otp set is_verified = :status where email =:email and otp = :otp", nativeQuery = true)
	public void updateEmailVerifiedStatus(@Param("status") Boolean status, @Param("email") String email,
			@Param("otp") String otp);

	@Query(value = "select app_status from mst_app_version where app_version = ?1 and app_name = 'KAUSHAL-PANJEE-APP'", nativeQuery = true)
	public String appVersionStatus(String appVersion);

	@Query(value = "select * from mst_candidate_otp where mobile_no = ?1 and imei_no = ?2 limit 1", nativeQuery = true)
	CandidateOtp getMobileCandidateInst(String mobileNo, String imeiNo);

	@Query(value = "select * from mst_candidate_otp where email_id = ?1 and imei_no = ?2 limit 1", nativeQuery = true)
	CandidateOtp getEmailCandidateInst(String emailId, String imeiNo);

	@Query(value = "select * from mst_candidate_otp where email_id = ?1 and mobile_no =?2 and imei_no = ?3 limit 1", nativeQuery = true)
	CandidateOtp getCandidateInst(String emailId, String mobileNo, String imeiNo);

	@Query(value = "select login_id, candidate_name from mst_candidate_adhaar_details where email_id = ?1 and mobile_no = ?2", nativeQuery = true)
	List<Object[]> getCandidateUserLoginIdList(String emailId, String mobileNo);

	@Query(value = "select exists( select 1 from mst_candidate_adhaar_details where aadhar_no=?1)", nativeQuery = true)
	boolean checkAadharExist(String encAadharNo);

	@Query(value = "select login_id from mst_candidate_adhaar_details where aadhar_no=?1", nativeQuery = true)
	String getCandidateUserLoginIdList(String encAadharNo);

	@Query(value = "SELECT EXISTS (SELECT 1 FROM mst_candidate_otp  WHERE  mobile_no =?1 and imei_no=?2 and created_on > NOW() - INTERVAL '2 minutes')", nativeQuery = true)
	boolean checkOTPIntervel(String decMobile, String imeiNo);

	@Query(value = "SELECT EXISTS (SELECT 1 FROM mst_candidate_otp  WHERE  email_id =?1 and imei_no=?2 and created_on > NOW() - INTERVAL '2 minutes')", nativeQuery = true)
	boolean checkOTPMailIntervel(String emailId, String imeiNo);

}
