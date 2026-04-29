package com.nic.KaushalPanjeeApp.location;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nic.KaushalPanjeeApp.userdetails.UserProfile;

public interface LocationRepository extends JpaRepository<UserProfile, Long> {

	@Query(value = "SELECT nrlm.state_code, nrlm.state_name, lgd.state_code as lgd_state_code FROM mst_state nrlm "
			+ "left join lgd_state lgd on nrlm.state_code = lgd.nrlm_state_code order by state_name", nativeQuery = true)
	List<Object[]> getStateList();

	@Query(value = "select nrlm.district_code, nrlm.district_name, lgd.district_code as lgd_district_code from mst_district nrlm "
			+ "left join lgd_district lgd on nrlm.district_code = lgd.nrlm_district_code where left(nrlm.district_code, 2) = ?1 order by district_name asc", nativeQuery = true)
	List<Object[]> getDistrictList(String stateCode);

	@Query(value = "select nrlm.block_code, nrlm.block_name, lgd.block_code as lgd_block_code from mst_block nrlm "
			+ "left join lgd_block lgd on nrlm.block_code = lgd.nrlm_block_code where nrlm.district_code = ?1 order by block_name asc", nativeQuery = true)
	List<Object[]> getBlockList(String districtCode);

	@Query(value = "SELECT nrlm.grampanchayat_code, nrlm.grampanchayat_name, lgd.lgd_gp_code FROM mst_grampanchayat nrlm  "
			+ "LEFT JOIN lgd_vill_details lgd ON nrlm.grampanchayat_code = LEFT(lgd.nrlm_village_code, 10) "
			+ "WHERE left(nrlm.grampanchayat_code, 7) = ?1 GROUP BY  nrlm.grampanchayat_code, nrlm.grampanchayat_name, lgd.lgd_gp_code order by grampanchayat_name asc", nativeQuery = true)
	List<Object[]> getGramPanchayatList(String blockCode);

	@Query(value = "SELECT nrlm.village_code, nrlm.village_name, lgd.lgd_village_code FROM mst_village nrlm "
			+ "left join lgd_vill_details lgd on nrlm.village_code = lgd.nrlm_village_code "
			+ "where left(nrlm.village_code, 10) = ?1 order by village_name asc", nativeQuery = true)
	List<Object[]> getVillageList(String gpCode);

	@Query(value = " select distinct name, fathername, ahl_tin from mp_secc "
			+ "					where village_code_lgd in (?2) "
			+ "					and name ilike ?1 and excluded_or_included_or_deprivated in ('deprivation', 'inclusion')", nativeQuery = true)
	List<Object[]> getSeccCandidateDetails(String candidateName, String lgdVillageCode, String tableName);

	@Query(value = "select state_short_name from mst_state where state_code = (select nrlm_state_code from lgd_state where state_code = (select lgd_state_code from lgd_vill_details where lgd_village_code = ?1 limit 1))", nativeQuery = true)
	String getStateShortName(String lgdVillCode);

	@Query(value = "select ulbcode, ulbname from public.ulb_state_data  where districtcode =?1", nativeQuery = true)
	List<Object[]> getUlbList(String lgdDistrictCode);

	@Query(value = "select wardcode, wardname_english from public.urban_local_bodies_coverage_details where localbodycode =?1", nativeQuery = true)
	List<Object[]> getUlbWardList(String ulbCode);

}
