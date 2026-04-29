package com.nic.KaushalPanjeeApp.trainingCenter;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TrainingCenterRepository extends JpaRepository<TrainingCenterEntity, Long> {

	@Query(value = "select distinct(so.pia_code), so.pia_name from sanction_order_details so "
			+ "left join pia_detail pd on pd.pia_code = so.pia_code where pia_status = 'V' and district_code = ?1", nativeQuery = true)
	public List<Object[]> getKpPiaDetails(String districtCode);

	@Query(value = "select distinct(aap.org_id), oo.name_of_the_org, office_district_code from annual_action_plan aap "
			+ "left join org_onboarding oo on oo.org_id::varchar = aap.org_id where office_district_code = ?1", nativeQuery = true)
	public List<Object[]> getKpOrganizationDetails(String districtCode);

	@Query(value = "select tc.id, concat(tc.training_center_name, ' (Incharge:- ',mu.user_name, ', Mobile No:- ', tc.mobile_no, ')') as trainingCenter from training_centers tc "
			+ "inner join sanction_order_details so on so.sanction_order_id = tc.sanction_order "
			+ "left join mst_user mu on mu.user_cd::varchar = tc.centre_incharge where so.pia_code= ?1 and mu.active_flag = 'Y'", nativeQuery = true)
	public List<Object[]> getKpPiaTrainingCenterList(String piaId);

	@Query(value = "select institute_id, concat(institute_name , ' (Incharge :- ',user_name, ', Mobile Number:- ' , COALESCE(contact_no, mobile), ' )') as instituteName  "
			+ "from institute_info ii left join institute_infra_info infra using(institute_id) "
			+ "left join (select right(rl.entity_code, -3)::integer as institute_id, user_name, mobile, active_flag from mst_assign_role rl  "
			+ "left join mst_user mu using(login_id) where role_cd = '104')aa using(institute_id) "
			+ "where ii.org_id= ?1 and aa.active_flag = 'Y'", nativeQuery = true)
	public List<Object[]> getKpOrgInstituteList(String orgid);

	@Query(value = "select cr.id, cr.trade_name from course_registration cr "
			+ "inner join training_center_materials tcm on cr.id::varchar = tcm.trade where tcm.training_center_id = ?1", nativeQuery = true)
	public List<Object[]> getKpTrainingCenterCourseList(int trainingCenterId);

	@Query(value = "select distinct(cr.id), cr.trade_name from course_registration cr inner join annual_action_plan aap on cr.id = aap.course_id "
			+ "where right(entity_code, -3) = ?1", nativeQuery = true)
	public List<Object[]> getKpInstituteCourseList(String instituteId);

	@Query(value = "select * from candidate_training_center_details where login_id = ?1", nativeQuery = true)
	public TrainingCenterEntity getTrainingCenterInstance(String loginId);

}
