package com.nic.KaushalPanjeeApp.userdetails;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

	@Query(value = "select * from candidate_user_profile where login_id =?1", nativeQuery = true)
	UserProfile getprofileInstance(String loginid);

	@Query(value = "select exists( select 1 from mst_candidate_adhaar_details where mobile_no=?1 and email_id=?2 )", nativeQuery = true)
	public boolean userIdPasswordExistOrNot(String mobileNo, String emailId);

	@Query(value = "select login_id from mst_candidate_adhaar_details where mobile_no=?1 and email_id=?2", nativeQuery = true)
	String getUserLoginIdPassword(String mobileNo, String emailId);

	@Query(value = "select aadhar_state_name from mst_candidate_adhaar_details where login_id =?1", nativeQuery = true)
	String getUserState(String loginId);

}
