package com.nic.KaushalPanjeeApp.userdetails;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserBankDetailsRepository extends JpaRepository<UserBankDetails, Long> {

	@Query(value = "select * from mst_candidate_bank where login_id =?1 order by created_on desc limit 1", nativeQuery = true)
	public UserBankDetails getBankInstanceByLogin(String loginid);

	@Query(value = "SELECT cb.bank_account_no, cb.ifsc_code, cb.pan_no, cb.bank_code, b.bank_name "
			+ "FROM mst_candidate_bank cb " + "LEFT JOIN mst_bank b ON cb.bank_code = b.bank_code "
			+ "WHERE cb.login_id = ?1", nativeQuery = true)
	List<Object[]> getBankListByLoginId(String loginId);

}
