package com.nic.KaushalPanjeeApp.bank;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nic.KaushalPanjeeApp.userdetails.UserDetails;

public interface BankRepository extends JpaRepository<UserDetails, Long> {

	@Query(value = "select aa.bank_code, aa.bank_name, aa.bank_branch_code, aa.bank_branch_name, ifsc_code, bal.alength "
			+ "from(select bnk.bank_code, bnk.bank_name, brn.bank_branch_code, brn.bank_branch_name, brn.ifsc_code from mst_bank bnk "
			+ "left join mst_bank_branch brn on bnk.bank_code = brn.bank_code)aa left join mst_bank_account_length bal on aa.bank_code = bal.bank_code "
			+ "where aa.ifsc_code =?1 group by aa.bank_code, aa.bank_name, aa.bank_branch_code, aa.bank_branch_name, aa.ifsc_code, bal.alength", nativeQuery = true)
	List<Object[]> getBankAccDetailsList(String ifscCode);

}
