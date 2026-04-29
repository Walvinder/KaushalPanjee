package com.nic.KaushalPanjeeApp.userdetails;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.LockModeType;

@Repository
public interface YearSequenceRepository extends JpaRepository<YearSequence, Integer> {
	@Transactional
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	YearSequence findByYear(int year);

	@Transactional
	@Modifying
	@Query(value = "update year_sequence set last_sequence = (select  nextval('candidate_kp_id_seq'))", nativeQuery = true)
	void getLatestSequence();

	@Transactional
	@Modifying
	@Query(value = "alter SEQUENCE candidate_kp_id_seq restart with 1", nativeQuery = true)
	void resetYearSequence();

}
