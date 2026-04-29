package com.nic.KaushalPanjeeApp.sectortrade;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nic.KaushalPanjeeApp.userdetails.UserDetails;

public interface SectorTradeRepository extends JpaRepository<UserDetails, Long> {

	@Query(value = "select sector_code, sector_name from mst_sector", nativeQuery = true)
	List<Object[]> getSectorList();

	@Query(value = "select trade_code, trade_name from course_registration where sector_code IN (:sectorId) "
			+ " union all "
			+ " select trade_code, trade_name from rseti_course_registration where sector_code IN (:sectorId)", nativeQuery = true)
	List<Object[]> getTradeList(@Param("sectorId") Integer[] sectorId);

	@Query(value = "select cr.sector_code, pt.sector_name,  id, cr.trade_name from course_registration cr join mst_sector pt on pt.sector_code = cr.sector_code   where scheme_code = '1' and valid_status = 'Active' and trade_name ilike :tradeName", nativeQuery = true)
	public List<Object[]> getDdugkyTradeBySearchTradeName(@Param("tradeName") String tradeName);
	
	@Query(value = "select cr.sub_sector_code, pt.program_type_name,  id, cr.trade_name from course_registration cr join program_type pt on pt.sno::varchar = cr.sub_sector_code   where scheme_code = '2' and valid_status = 'Active' and trade_name ilike :tradeName", nativeQuery = true)
	public List<Object[]> getRsetiTradeBySearchTradeName(@Param("tradeName") String tradeName);

}
