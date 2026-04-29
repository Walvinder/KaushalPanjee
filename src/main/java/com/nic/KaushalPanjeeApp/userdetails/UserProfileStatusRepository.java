package com.nic.KaushalPanjeeApp.userdetails;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserProfileStatusRepository extends JpaRepository<UserProfileStatus, Long> {

	@Query(value = "select * from mst_candidate_profile_status where login_id =?1 order by created_on desc limit 1", nativeQuery = true)
	public UserProfileStatus getStatusInstanceByLogin(String loginid);

}
