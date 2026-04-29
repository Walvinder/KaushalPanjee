package com.nic.KaushalPanjeeApp.userdetails;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CandidateAttachmentRepository extends JpaRepository<CandidateAttachment, Long> {

	@Query(value = "select * from candidate_attachment where login_id =?1 order by created_on desc limit 1", nativeQuery = true)
	public CandidateAttachment getAttachmentByLoginId(String loginId);

}
