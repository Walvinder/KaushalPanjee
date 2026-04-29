package com.nic.KaushalPanjeeApp.userdetails;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserAddressDetailsRepository extends JpaRepository<UserAddressDetails, Long> {

	@Query(value = "select * from mst_candidate_address_details where login_id =?1 order by created_on desc limit 1", nativeQuery = true)
	public UserAddressDetails getInstanceByLogin(String loginid);

}
