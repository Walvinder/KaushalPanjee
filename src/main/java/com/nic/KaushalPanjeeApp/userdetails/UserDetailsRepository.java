package com.nic.KaushalPanjeeApp.userdetails;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserDetailsRepository extends JpaRepository<UserDetails, Long> {

	@Query(value = "select exists( select 1 from candidate_user where login_id=?1 and password=?2 )", nativeQuery = true)
	public boolean userIdExistOrNot(String userID, String password);

	/*
	 * @Query(value = "select count(*) from candidate_user", nativeQuery = true)
	 * public long getCount();
	 */

	@Query(value = "select  * from candidate_user where login_id =?1", nativeQuery = true)
	public UserDetails getByLoginId(String loginId);

	/*@Query(value = "select aadhar_no, aadhar_image, aadhar_block_name, date_of_birth, aadhar_district_name, email_id, gender,  care_of, login_id, "
			+ "mobile_no, candidate_name, aadhar_post_office, aadhar_state_name, aadhar_location, aadhar_village_name, aadhar_pin_code, bb.state_code, "
			+ "state_name, attachment_id from (select aadhar_no, aadhar_image, aadhar_block_name, date_of_birth, aadhar_district_name, email_id, gender, "
			+ "care_of, usr.login_id, mobile_no, candidate_name, aadhar_post_office, aadhar_state_name, aadhar_location, aadhar_village_name, aadhar_pin_code, "
			+ "state_code, attachment_id from mst_candidate_adhaar_details usr left join (WITH RankedAttachments AS (SELECT aadhar_image, login_id, updated_on, attachment_id,   "
			+ "ROW_NUMBER() OVER (PARTITION BY login_id  ORDER BY updated_on DESC) as rn FROM candidate_attachment WHERE aadhar_image IS NOT NULL)  "
			+ "SELECT login_id, aadhar_image, attachment_id FROM RankedAttachments WHERE rn = 1 and login_id = ?1 ORDER BY login_id)aa on usr.login_id = aa.login_id)bb "
			+ "left join mst_state cls on bb.state_code = cls.state_code where bb.login_id = ?1", nativeQuery = true)*/
	
	@Query(value = "select aadhar_no, aadhar_image, aadhar_block_name, date_of_birth, aadhar_district_name, bb.email_id, gender, "
			+ "care_of, bb.login_id, mobile_no, candidate_name, aadhar_post_office, aadhar_state_name, aadhar_location,  "
			+ "aadhar_village_name, aadhar_pin_code, bb.state_code, state_name, attachment_id, oa.check_in, oa.check_out, "
			+ "oa.total_hours,oa.attendance_flag, wrd.id workplaceId, wrd.workplace_name, ed.employer_id, ed.organization_name,  "
			+ "wrd.latitude, wrd.longitude, wrd.radius, pcd.batch_id, pd.ojt_start_date, pd.ojt_end_date from (select aadhar_no, aadhar_image, aadhar_block_name,  "
			+ "date_of_birth, aadhar_district_name, email_id, gender, care_of, usr.login_id, mobile_no, candidate_name, aadhar_post_office,  "
			+ "aadhar_state_name, aadhar_location, aadhar_village_name, aadhar_pin_code, state_code, attachment_id from mst_candidate_adhaar_details usr  "
			+ "left join (WITH RankedAttachments AS (SELECT aadhar_image, login_id, updated_on, attachment_id, ROW_NUMBER() OVER (PARTITION BY login_id  "
			+ "ORDER BY updated_on DESC) as rn FROM candidate_attachment WHERE aadhar_image IS NOT NULL) SELECT login_id, aadhar_image, attachment_id  "
			+ "FROM RankedAttachments WHERE rn = 1 and login_id = ?1 ORDER BY login_id)aa on usr.login_id = aa.login_id where usr.login_id = ?1)bb  "
			+ "left join mst_state cls on bb.state_code = cls.state_code left join mst_ojt_attendance oa on bb.login_id = oa.candidate_id  "
			+ "left join ojt_plan_confirmation_ddugky pcd on bb.login_id = pcd.candidate_id left join workplace_detail_register wrd on pcd.workplace_id = wrd.id  "
			+ "left join employer_details ed on pcd.employer_id = ed.employer_id left join ojt_plan_ddugky pd on pd.ojt_plan_id = pcd.ojt_plan_id ", nativeQuery = true)
	public List<Object[]> getUserProfileList(String loginid);

	@Query(value = "select exists( select 1 from candidate_user where login_id=?1 and login_status = 'logged')", nativeQuery = true)
	public boolean userLoggedInOrNot(String loginid);

	@Query(value = "select personal_status, address_status, secc_status, educational_status, employment_status, training_status,  "
			+ "banking_status,aadhar_image,attachment_id, aadhar_state_name, is_face_registered, candidate_name, aadhar_no, ls.state_code, password_changed, "
			+ "COALESCE(scheme_type, 'NA') schemetype, aa.state_code, case when (CURRENT_DATE between pd.ojt_start_date and pd.ojt_end_date) then 'Y' else 'N' end as ojtFlag "
			+ "from (select personal_status, address_status, secc_status, educational_status, employment_status, training_status, banking_status, "
			+ "aadhar_image,attachment_id, aadhar_state_name, is_face_registered, candidate_name, aadhar_no, state_code, aa.login_id "
			+ "from (select personal_status, address_status, secc_status, educational_status, employment_status, training_status, banking_status, login_id "
			+ "from mst_candidate_profile_status where login_id = ?1)aa left join (WITH RankedAttachments AS (SELECT aadhar_no, aadhar_image, attachment_id, ca.login_id, "
			+ "ca.updated_on, aadhar_state_name,is_face_registered, candidate_name, mcad.state_code, "
			+ "ROW_NUMBER() OVER (PARTITION BY ca.login_id  ORDER BY ca.updated_on DESC) as rn FROM candidate_attachment ca join mst_candidate_adhaar_details mcad "
			+ "on ca.login_id = mcad.login_id WHERE aadhar_image IS NOT NULL)  "
			+ "SELECT login_id, aadhar_image, attachment_id, aadhar_no, updated_on, aadhar_state_name,is_face_registered, candidate_name, state_code "
			+ "FROM RankedAttachments WHERE rn = 1 ORDER BY login_id)bb on aa.login_id = bb.login_id)aa left join lgd_state ls on aa.state_code = ls.nrlm_state_code left join candidate_user cu "
			+ "on cu.login_id = aa.login_id left join candidate_user_profile cup on cup.login_id = aa.login_id "
			+ "left join ojt_plan_confirmation_ddugky cd on cup.login_id = cd.candidate_id "
			+ "left join ojt_plan_ddugky pd on pd.ojt_plan_id = cd.ojt_plan_id", nativeQuery = true)
	public List<Object[]> getSectionStatus(String loginid);

	@Query(value = "select guardian_name, mother_name, guardian_mobile_no, annual_family_income, voter_id, dl_no, cast_category, marital_status,"
			+ "is_minority, is_disability, is_nrega, nrega_job_card, is_shg, shg_no, antyodaya, is_rsby, is_pip, is_pmayg from candidate_user_profile cup "
			+ "left join mst_candidate_adhaar_details cad on cup.login_id =cad.login_id "
			+ "where cup.login_id=?1", nativeQuery = true)
	public List<Object[]> getCandidatePersonalDetails(String loginid);

	@Query(value = "select voter_id_card, attachment_id, cad.aadhar_state_name FROM candidate_attachment ca left join mst_candidate_adhaar_details cad on ca.login_id =cad.login_id "
			+ "WHERE ca.login_id = ?1 and voter_id_card is not null ORDER BY ca.updated_on DESC LIMIT 1", nativeQuery = true)
	public List<Object[]> getCandidateVoterCard(String loginid);

	@Query(value = "select dl_card, attachment_id, cad.aadhar_state_name FROM candidate_attachment ca left join mst_candidate_adhaar_details cad on ca.login_id =cad.login_id "
			+ "WHERE ca.login_id = ?1 and dl_card is not null ORDER BY ca.updated_on DESC LIMIT 1", nativeQuery = true)
	public List<Object[]> getCandidateDrivingLicenseCard(String loginid);

	@Query(value = "select category_cert, attachment_id, cad.aadhar_state_name FROM candidate_attachment ca left join mst_candidate_adhaar_details cad on ca.login_id =cad.login_id "
			+ "WHERE ca.login_id = ?1 and category_cert is not null ORDER BY ca.updated_on DESC LIMIT 1", nativeQuery = true)
	public List<Object[]> getCandidateCategoryCertificate(String loginid);

	@Query(value = "select minority_cert, attachment_id, cad.aadhar_state_name FROM candidate_attachment ca left join mst_candidate_adhaar_details cad on ca.login_id =cad.login_id "
			+ "WHERE ca.login_id = ?1 and minority_cert is not null ORDER BY ca.updated_on DESC LIMIT 1", nativeQuery = true)
	public List<Object[]> getCandidateMinorityCertificate(String loginid);

	@Query(value = "select disablity_cert, attachment_id, cad.aadhar_state_name FROM candidate_attachment ca left join mst_candidate_adhaar_details cad on ca.login_id =cad.login_id "
			+ "WHERE ca.login_id = ?1 and disablity_cert is not null ORDER BY ca.updated_on DESC LIMIT 1", nativeQuery = true)
	public List<Object[]> getCandidateDisabilityCertificate(String loginid);

	@Query(value = "select narega_card, attachment_id, cad.aadhar_state_name FROM candidate_attachment ca left join mst_candidate_adhaar_details cad on ca.login_id =cad.login_id "
			+ "WHERE ca.login_id = ?1 and narega_card is not null ORDER BY ca.updated_on DESC LIMIT 1", nativeQuery = true)
	public List<Object[]> getCandidateNaregaCard(String loginid);

	@Query(value = "select ration_card, attachment_id, cad.aadhar_state_name FROM candidate_attachment ca left join mst_candidate_adhaar_details cad on ca.login_id =cad.login_id "
			+ "WHERE ca.login_id = ?1 and ration_card is not null ORDER BY ca.updated_on DESC LIMIT 1", nativeQuery = true)
	public List<Object[]> getCandidateRationCard(String loginid);

	@Query(value = "select rsby_card, attachment_id, cad.aadhar_state_name FROM candidate_attachment ca left join mst_candidate_adhaar_details cad on ca.login_id =cad.login_id "
			+ "WHERE ca.login_id = ?1 and rsby_card is not null ORDER BY ca.updated_on DESC LIMIT 1", nativeQuery = true)
	public List<Object[]> getCandidateRsbyCard(String loginid);

	@Query(value = "select residence_cert, attachment_id, cad.aadhar_state_name FROM candidate_attachment ca left join mst_candidate_adhaar_details cad on ca.login_id =cad.login_id "
			+ "WHERE ca.login_id = ?1 and residence_cert is not null ORDER BY ca.updated_on DESC LIMIT 1", nativeQuery = true)
	public List<Object[]> getCandidateResidenceCert(String loginid);

	/*
	 * @Query(value =
	 * "select pr_state_code, prstateName, pr_district_code, prdistrictname, pr_block_code, prblockname, pr_gp_code, "
	 * +
	 * "prgpname, pr_village_code, prvillagename, pr_street1,pr_street2, pr_pin_code, "
	 * +
	 * "is_temp_address_same, temp_state_code, tempstatename, temp_district_code, tempdistrictname, temp_block_code, tempblockname, temp_gp_code, tempgpname,"
	 * +
	 * "temp_village_code, village_name as tempvillagename, temp_street1,temp_street2, temp_pin_code, login_id "
	 * +
	 * "from(select pr_state_code, prstateName, pr_district_code, prdistrictname, pr_block_code, prblockname, pr_gp_code, "
	 * +
	 * "prgpname, pr_village_code, prvillagename, pr_street1,pr_street2, pr_pin_code, "
	 * +
	 * "is_temp_address_same, temp_state_code, tempstatename, temp_district_code, tempdistrictname, temp_block_code, tempblockname, temp_gp_code, grampanchayat_name as tempgpname, "
	 * + "temp_village_code,temp_street1,temp_street2, temp_pin_code, login_id " +
	 * "from(select pr_state_code, prstateName, pr_district_code, prdistrictname, pr_block_code, prblockname, pr_gp_code, "
	 * +
	 * "prgpname, pr_village_code, prvillagename, pr_street1,pr_street2, pr_pin_code, "
	 * +
	 * "is_temp_address_same, temp_state_code, tempstatename, temp_district_code, tempdistrictname, temp_block_code, block_name as tempblockname, temp_gp_code, "
	 * + "temp_village_code,temp_street1,temp_street2, temp_pin_code, login_id " +
	 * "from(select pr_state_code, prstateName, pr_district_code, prdistrictname, pr_block_code, prblockname, pr_gp_code, "
	 * +
	 * "prgpname, pr_village_code, prvillagename, pr_street1,pr_street2, pr_pin_code, "
	 * +
	 * "is_temp_address_same, temp_state_code, tempstatename, temp_district_code, district_name as tempdistrictname, temp_block_code, temp_gp_code, "
	 * + "temp_village_code,temp_street1,temp_street2, temp_pin_code, login_id " +
	 * "from(select  pr_state_code, prstateName, pr_district_code, prdistrictname, pr_block_code, prblockname, pr_gp_code, "
	 * +
	 * "prgpname, pr_village_code, prvillagename, pr_street1,pr_street2, pr_pin_code, "
	 * +
	 * "is_temp_address_same, temp_state_code, cst.state_name as tempstatename, temp_district_code, temp_block_code, temp_gp_code, temp_village_code,temp_street1,temp_street2, temp_pin_code, login_id "
	 * +
	 * "from(select pr_state_code, prstateName, pr_district_code, district_name as prdistrictname, pr_block_code, block_name as prblockname, pr_gp_code, "
	 * +
	 * "grampanchayat_name as prgpname, pr_village_code, village_name as prvillagename, pr_street1,pr_street2, pr_pin_code, "
	 * +
	 * "is_temp_address_same, temp_state_code, temp_district_code, temp_block_code, temp_gp_code, temp_village_code,temp_street1,temp_street2, temp_pin_code, login_id "
	 * +
	 * "from(select pr_state_code, prstateName, pr_district_code, district_name, pr_block_code, block_name, pr_gp_code, grampanchayat_name, pr_village_code,pr_street1,pr_street2, pr_pin_code, "
	 * +
	 * "is_temp_address_same, temp_state_code, temp_district_code, temp_block_code, temp_gp_code, temp_village_code,temp_street1,temp_street2, temp_pin_code, login_id "
	 * +
	 * "from (select pr_state_code, prstateName, pr_district_code, district_name, pr_block_code, block_name, pr_gp_code, pr_village_code,pr_street1,pr_street2, pr_pin_code, "
	 * +
	 * "is_temp_address_same, temp_state_code, temp_district_code, temp_block_code, temp_gp_code, temp_village_code,temp_street1,temp_street2, temp_pin_code, login_id "
	 * +
	 * "from (select pr_state_code, prstateName, pr_district_code, district_name, pr_block_code, pr_gp_code, pr_village_code,pr_street1,pr_street2, pr_pin_code, "
	 * +
	 * "is_temp_address_same, temp_state_code, temp_district_code, temp_block_code, temp_gp_code, temp_village_code,temp_street1,temp_street2, temp_pin_code, login_id "
	 * +
	 * "from (select pr_state_code, st.state_name as prstateName, pr_district_code, pr_block_code, pr_gp_code, pr_village_code,pr_street1,pr_street2, pr_pin_code, is_temp_address_same,"
	 * +
	 * "temp_state_code, temp_district_code, temp_block_code, temp_gp_code, temp_village_code,temp_street1,temp_street2, temp_pin_code, login_id "
	 * +
	 * "from mst_candidate_address_details pr left join mst_state st on pr.pr_state_code = st.state_code)aa "
	 * +
	 * "left join mst_district cd on aa.pr_district_code = cd.district_code)bb left join mst_block cb on bb.pr_block_code = cb.block_code)cc "
	 * +
	 * "left join mst_grampanchayat cg on cc.pr_gp_code = cg.grampanchayat_code)dd left join mst_village cv on dd.pr_village_code = cv.village_code)ee "
	 * +
	 * "left join mst_state cst on ee.temp_state_code = cst.state_code)ff left join mst_district cd on ff.temp_district_code = cd.district_code)gg "
	 * +
	 * "left join mst_block cb on gg.temp_block_code = cb.block_code)hh left join mst_grampanchayat cg on hh.temp_gp_code = cg.grampanchayat_code)ii "
	 * +
	 * "left join mst_village cv on ii.temp_village_code = cv.village_code where login_id = ?1"
	 * , nativeQuery = true) public List<Object[]> getCandidateAddressDetails(String
	 * loginid);
	 */

	@Query(value = "select pr_state_code, st.state_name as prstatename, pr_district_code, cd.district_name as prdistrictname, pr_block_code, cb.block_name prblockname, pr_gp_code,  "
			+ "cg.grampanchayat_name as prgpname, pr_village_code, cv.village_name as prvillagename, pr_street1,pr_street2, pr_pin_code,is_temp_address_same, temp_state_code,  "
			+ "cst.state_name, temp_district_code, tcd.district_name, temp_block_code, tcb.block_name, temp_gp_code, tcg.grampanchayat_name, temp_village_code, tcv.village_name as tempvillagename, "
			+ "temp_street1,temp_street2, temp_pin_code, login_id, pr_locality, pr_ulb_code, prusd.ulbname, pr_ward_code, ulbs.wardname_english as prwardname, temp_ulb_code,  "
			+ "tusd.ulbname as tempulbname, temp_ward_code, tulbs.wardname_english tempwardname, temp_locality, pld.district_code, tld.district_code  "
			+ "from mst_candidate_address_details pr  " + "left join mst_state st on pr.pr_state_code = st.state_code  "
			+ "left join mst_district cd on pr.pr_district_code = cd.district_code  "
			+ "left join mst_block cb on pr.pr_block_code = cb.block_code  "
			+ "left join mst_grampanchayat cg on pr.pr_gp_code = cg.grampanchayat_code  "
			+ "left join mst_village cv on pr.pr_village_code = cv.village_code  "
			+ "left join mst_state cst on pr.temp_state_code = cst.state_code  "
			+ "left join mst_district tcd on pr.temp_district_code = tcd.district_code  "
			+ "left join mst_block tcb on pr.temp_block_code = tcb.block_code  "
			+ "left join mst_grampanchayat tcg on pr.temp_gp_code = tcg.grampanchayat_code  "
			+ "left join mst_village tcv on pr.temp_village_code = tcv.village_code  "
			+ "left join public.ulb_state_data prusd on prusd.ulbcode = pr.pr_ulb_code  "
			+ "left join public.ulb_state_data tusd on tusd.ulbcode = pr.temp_ulb_code  "
			+ "left join public.urban_local_bodies_coverage_details ulbs on ulbs.wardcode = pr.pr_ward_code  "
			+ "left join public.urban_local_bodies_coverage_details tulbs on tulbs.wardcode = pr.temp_ward_code  "
			+ "left join lgd_district pld on pld.nrlm_district_code = pr.pr_district_code "
			+ "left join lgd_district tld on tld.nrlm_district_code = pr.temp_district_code "
			+ "where login_id = ?1", nativeQuery = true)
	public List<Object[]> getCandidateAddressDetails(String loginid);

	@Query(value = "select secc_state_code, state_name, secc_district_code, district_name, secc_block_code, block_name, secc_gp_code, grampanchayat_name, "
			+ "secc_village_code, village_name, secc_candidate_name, secc_ahl_tin, login_id, lgd_village_code "
			+ "from (select secc_state_code, state_name, secc_district_code, district_name, secc_block_code, block_name, secc_gp_code, grampanchayat_name, "
			+ "secc_village_code, village_name, secc_candidate_name, secc_ahl_tin, login_id "
			+ "from(select secc_state_code, state_name, secc_district_code, district_name, secc_block_code, block_name, secc_gp_code, grampanchayat_name, secc_village_code, secc_candidate_name, secc_ahl_tin, login_id "
			+ "from(select secc_state_code, state_name, secc_district_code, district_name, secc_block_code, block_name, secc_gp_code, secc_village_code, secc_candidate_name, secc_ahl_tin, login_id "
			+ "from(select secc_state_code, state_name, secc_district_code, district_name, secc_block_code, secc_gp_code, secc_village_code, secc_candidate_name, secc_ahl_tin, login_id "
			+ "from(select secc_state_code, state_name, secc_district_code, secc_block_code, secc_gp_code, secc_village_code, secc_candidate_name, secc_ahl_tin, login_id "
			+ "from mst_candidate_address_details cup left join mst_state cs on cup.secc_state_code = cs.state_code)aa left join mst_district cd on aa.secc_district_code = cd.district_code)bb "
			+ "left join mst_block cb on bb.secc_block_code = cb.block_code)cc left join mst_grampanchayat cg on cc.secc_gp_code = cg.grampanchayat_code)dd "
			+ "left join mst_village cv on dd.secc_village_code = cv.village_code)abc left join lgd_vill_details vill "
			+ "on vill.nrlm_village_code = abc.secc_village_code where login_id = ?1", nativeQuery = true)
	public List<Object[]> getCandidateSeccDetails(String loginid);

	// @Query(value = "select highest_education, month_year_of_passing,
	// language_known, is_tech_educate, tech_qualification,
	// month_year_of_passing_tech_edu,"
	// + "tech_education_domain, login_id, qual_name, domain_name, highest_class
	// from (select highest_education, month_year_of_passing, language_known,
	// is_tech_educate, "
	// + "tech_qualification, month_year_of_passing_tech_edu, tech_education_domain,
	// login_id, qual_name, highest_class from candidate_user_profile cup "
	// + "left join mst_technical_qual mtq on cup.tech_qualification =
	// cast(qual_code as integer))aa left join mst_technical_qual_domain tqd "
	// + "on aa.tech_education_domain = cast(domain_code as integer) where login_id
	// = ?1", nativeQuery = true)
	// public List<Object[]> getCandidateEducationDetails(String loginid);

	@Query(value = "SELECT " + "    CASE " + "        WHEN highest_class IS NOT NULL AND TRIM(highest_class) <> '' "
			+ "            THEN 'Schooling' " + "        WHEN qual_type IS NOT NULL " + "            THEN qual_type "
			+ "        ELSE qual_name " + "    END AS highest_education, " + "    month_year_of_passing, "
			+ "    language_known, " + "    is_tech_educate, " + "    tech_qualification, "
			+ "    month_year_of_passing_tech_edu, " + "    tech_education_domain, " + "    login_id, "
			+ "    qual_name, " + "    domain_name, " + "    highest_class " + "FROM ( " + "    SELECT "
			+ "        cup.highest_education, " + "        cup.month_year_of_passing, " + "        cup.language_known, "
			+ "        cup.is_tech_educate, " + "        cup.tech_qualification, "
			+ "        cup.month_year_of_passing_tech_edu, " + "        cup.tech_education_domain, "
			+ "        cup.login_id, " + "        mtq.qual_name, " + "        mtq.qual_type, "
			+ "        cup.highest_class " + "    FROM candidate_user_profile cup "
			+ "    LEFT JOIN mst_technical_qual mtq "
			+ "        ON cup.tech_qualification = CAST(mtq.qual_code AS INTEGER) " + ") aa "
			+ "LEFT JOIN mst_technical_qual_domain tqd "
			+ "    ON aa.tech_education_domain = CAST(tqd.domain_code AS INTEGER) "
			+ "WHERE login_id = ?1", nativeQuery = true)
	List<Object[]> getCandidateEducationDetails(String loginId);

	@Query(value = "select is_employeed, employment_mode, intrested_in, employment_preference, job_location_preference, monthly_earning, expected_monthly_salary "
			+ "FROM candidate_user_profile where login_id = ?1", nativeQuery = true)
	public List<Object[]> getCandidateEmployeeDetails(String loginid);

	// @Query(value = "select cup.is_pre_training, cup.pre_completed_training,
	// cup.comp_training_duration, cup.heared_about_scheme, cup.heared_from,
	// cup.intrested_sector, "
	// + "STRING_AGG(DISTINCT cst.sector_name, ',') AS sectorsName,
	// cup.intrested_trade, cup.scheme_type "
	// + "from candidate_user_profile cup left join mst_sector cst on
	// cst.sector_code = ANY (SELECT
	// CAST(unnest(string_to_array(cup.intrested_sector, ',')) AS integer))"
	// + "where login_id = ?1 group by cup.is_pre_training,
	// cup.pre_completed_training, cup.comp_training_duration,
	// cup.heared_about_scheme, cup.heared_from, cup.intrested_sector, "
	// + "cup.intrested_trade", nativeQuery = true)
	// public List<Object[]> getCandidateTrainingDetails(String loginid);
	//
	@Query(value = """
			SELECT
			    cup.is_pre_training,
			    cup.pre_completed_training,
			    cup.comp_training_duration,
			    cup.heared_about_scheme,
			    cup.heared_from,
			    cup.intrested_sector,
			    STRING_AGG(DISTINCT cst.sector_name, ',') AS sectorsName,
			    cup.intrested_trade,
			    cup.scheme_type
			FROM candidate_user_profile cup
			LEFT JOIN mst_sector cst
			    ON cst.sector_code = ANY(string_to_array(cup.intrested_sector, ',')::int[])
			WHERE cup.login_id = ?1
			GROUP BY
			    cup.is_pre_training,
			    cup.pre_completed_training,
			    cup.comp_training_duration,
			    cup.heared_about_scheme,
			    cup.heared_from,
			    cup.intrested_sector,
			    cup.intrested_trade,
			    cup.scheme_type
			""", nativeQuery = true)
	List<Object[]> getCandidateTrainingDetails(String loginId);

	@Query(value = "select aa.bank_code, aa.bank_name, aa.bank_branch_code, cbb.bank_branch_name, bank_account_no, aa.ifsc_code, pan_no, login_id "
			+ "from(select cup.bank_code, bank_branch_code, bank_account_no, ifsc_code, pan_no, cb.bank_name, login_id from mst_candidate_bank cup "
			+ "left join mst_bank cb on cup.bank_code = cb.bank_code)aa left join mst_bank_branch cbb on aa.bank_branch_code = cbb.bank_branch_code "
			+ "where login_id = ?1", nativeQuery = true)
	public List<Object[]> getCandidateBankingDetails(String loginid);

	@Query(value = "select password from candidate_user where login_id = ?1", nativeQuery = true)
	public String getExistingPassword(String loginId);

	@Query(value = "select pip_cert, attachment_id, cad.aadhar_state_name FROM candidate_attachment ca left join mst_candidate_adhaar_details cad on ca.login_id=cad.login_id WHERE ca.login_id = ?1  and pip_cert is not null ORDER BY ca.updated_on DESC LIMIT 1", nativeQuery = true)
	public List<Object[]> getCandidatePipCert(String loginid);

	@Query(value = "select pmayg_attachment, attachment_id, cad.aadhar_state_name FROM candidate_attachment ca left join mst_candidate_adhaar_details cad on ca.login_id=cad.login_id WHERE ca.login_id = ?1  and pmayg_attachment is not null ORDER BY ca.updated_on DESC LIMIT 1", nativeQuery = true)
	public List<Object[]> getCandidatePmaygCert(String loginid);

	@Query(value = "select shg_image, attachment_id, cad.aadhar_state_name FROM candidate_attachment ca left join mst_candidate_adhaar_details cad on ca.login_id=cad.login_id WHERE ca.login_id = ?1  and shg_image is not null ORDER BY ca.updated_on DESC LIMIT 1", nativeQuery = true)
	public List<Object[]> getCandidateSHGImage(String loginid);

}
