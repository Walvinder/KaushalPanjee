package com.nic.KaushalPanjeeApp.unnati;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import jakarta.transaction.Transactional;

public interface NregaUnnatiApplicantRepository extends JpaRepository<NregaUnnatiApplicantEntity, Long> {

	@Query(value = "select * from nrega_unnati_applicant where job_card_no =?1 and applicant_no = ?2", nativeQuery = true)
	public NregaUnnatiApplicantEntity getNregaUnnatiInstance(String jobCardNo, String applicantNo);

//	public String getAadharByLoginId(String candidateId);

	@Query(value = "select exists (select 1 from nrega_unnati_applicant where adhaar_hash = ?1)", nativeQuery = true)
	public Boolean existsByAAdhar(String aadhaarNumber);

	@Modifying
	@Transactional
	@Query(value = "UPDATE nrega_unnati_applicant "
			+ "SET login_id = ?2, modified_by = 'KaushalPanjee', modified_on = now() "
			+ "WHERE adhaar_hash = ?1", nativeQuery = true)
	int updateLoginIdByAadhaar(String aadhaarNumber, String loginId);

	@Query(value = "select exists (select 1 from mst_candidate_adhaar_details where in_batch = 'true' and login_id= ?1)", nativeQuery = true)
	public boolean checkExistsInBatch(String loginId);

	@Query(value = "select login_id, mobile_no from mst_candidate_adhaar_details where login_id in(select login_id from candidate_user_temp)", nativeQuery = true)
	public List<Object[]> getUserData();

}
