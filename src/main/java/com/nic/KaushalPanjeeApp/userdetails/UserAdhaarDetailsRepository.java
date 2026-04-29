package com.nic.KaushalPanjeeApp.userdetails;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserAdhaarDetailsRepository extends JpaRepository<UserAdhaarDetails, Long> {

	@Query(value = "select * from mst_candidate_adhaar_details where login_id =?1", nativeQuery = true)
	public UserAdhaarDetails getAdhaarInstanceByLogin(String loginid);

	@Query(value = "select exists( select 1 from mst_candidate_adhaar_details where aadhar_no = ?1)", nativeQuery = true)
	public boolean isAadhaarExistOrNot(String aadharNo);

	@Query(value = "select login_id, aadhar_no from mst_candidate_adhaar_details where hash_aadhaar_no is null", nativeQuery = true)
	public List<Object[]> getUserAddharList();

	@Query(value = "select count(mobile_no) from mst_candidate_adhaar_details  where mobile_no = ?1", nativeQuery = true)
	public long getMobileNoCount(String decMobile);

}
