package com.nic.KaushalPanjeeApp.ojtAttendance;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OjtAttendanceRepository extends JpaRepository<OjtAttendance, Long> {

	@Query(value = "select wdr.latitude::varchar, wdr.longitude::varchar from ojt_plan_confirmation_ddugky pcd left join workplace_detail_register wdr on pcd.workplace_id = wdr.id where pcd.batch_id = ?1 limit 1", nativeQuery = true)
	public List<Object[]> getUserLocation(int batchId);

	@Query(value = "select * from mst_ojt_attendance where batch_id = ?1 and candidate_id = ?2 and attendance_date = ?3", nativeQuery = true)
	public OjtAttendance getAttendanceInstance(int batchId, String candidateId, LocalDate localDateTime);

}
