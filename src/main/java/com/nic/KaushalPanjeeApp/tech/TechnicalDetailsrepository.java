package com.nic.KaushalPanjeeApp.tech;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nic.KaushalPanjeeApp.userdetails.UserProfile;

public interface TechnicalDetailsrepository extends JpaRepository<UserProfile, Long> {

	@Query(value = "SELECT qual_code, qual_name FROM mst_technical_qual where qual_type = ?1  order by qual_name asc", nativeQuery = true)
	public List<Object[]> getTechQualificationList(String qualificationCategory);

	@Query(value = "SELECT dm.domain_code, dm.domain_name FROM mst_technical_qual ql "
			+ "JOIN mst_technical_qual_domain dm ON dm.domain_code = ANY(string_to_array(ql.qual_desc, ',')) WHERE ql.qual_code = ?1", nativeQuery = true)
	public List<Object[]> getTechDomainList(String qualCode);

	@Query(value = "select survey_code, survey_medium from mst_survey order by survey_code asc", nativeQuery = true)
	public List<Object[]> getSurveyList();

	@Query(value = "select id, language_name from mst_language order by language_name asc", nativeQuery = true)
	public List<Object[]> getLanguageList();

	@Query(value = "select institute_name,  COALESCE(landline_no, '') || ' ' || COALESCE(contact_no, '') contact_details, address_line1, address_line2, "
			+ "address_line3, state_name, district_name, block_name, pin_code, iii.file1_name, iii.file1_extention, iii.file2_name, iii.file2_extention,"
			+ "iii.file3_name, iii.file3_extention, iii.file4_name, iii.file4_extention, iii.file5_name, iii.file5_extention, ii.institute_id, "
			+ "iii.lattitude, iii.longitude, iii.radius, ii.district_code from institute_info ii inner join institute_infra_info iii using(institute_id)"
			+ "left join mst_state using (state_code) left join mst_district using(district_code)"
			+ "left join mst_block using(block_code) where ii.district_code = ?1", nativeQuery = true)
	public List<Object[]> getTrainingCenterList(String districtCode, String sectorI);

	@Query(value = "select institute_name,  COALESCE(landline_no, '') || ' ' || COALESCE(contact_no, '') contact_details, address_line1, address_line2, "
			+ "address_line3, state_name, district_name, block_name, pin_code, iii.file1_name, iii.file1_extention, iii.file2_name, iii.file2_extention,"
			+ "iii.file3_name, iii.file3_extention, iii.file4_name, iii.file4_extention, iii.file5_name, iii.file5_extention, ii.institute_id, "
			+ "iii.lattitude, iii.longitude, iii.radius, ii.district_code from institute_info ii inner join institute_infra_info iii using(institute_id)"
			+ "left join mst_state using (state_code) left join mst_district using(district_code)"
			+ "left join mst_block using(block_code) where ii.institute_name ilike ?1", nativeQuery = true)
	public List<Object[]> searchTrainingCenterList(String centerName);

	@Query(value = "select institute_name,  COALESCE(landline_no, '') || ' ' || COALESCE(contact_no, '') contact_details, address_line1, address_line2, "
			+ "address_line3, state_name, district_name, block_name, pin_code, iii.file1_name, iii.file1_extention, iii.file2_name, iii.file2_extention,"
			+ "iii.file3_name, iii.file3_extention, iii.file4_name, iii.file4_extention, iii.file5_name, iii.file5_extention, ii.institute_id, "
			+ "iii.lattitude, iii.longitude, iii.radius, ii.district_code from institute_info ii inner join institute_infra_info iii using(institute_id)"
			+ "left join mst_state using (state_code) left join mst_district using(district_code)"
			+ "left join mst_block using(block_code) where ii.institute_id = ?1", nativeQuery = true)
	public List<Object[]> getTrainingCenter(int centercode);

	@Query(value = "select banner_code, banner_image from mst_candidate_banner", nativeQuery = true)
	public List<Object[]> getBannerList();

}
