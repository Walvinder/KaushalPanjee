package com.nic.KaushalPanjeeApp.userdetails;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserDeviceInfoRepository extends JpaRepository<UserDeviceInfo, Long> {

	@Query(value = "select  imei_no from candidate_device_details where imei_no =?1", nativeQuery = true)
	public String getImeiNumber(String imeiNo);

	@Query(value = "select  * from candidate_device_details where login_id = ?1 and imei_no =?2 order by created_on desc Limit 1", nativeQuery = true)
	public UserDeviceInfo getDeviceObj(String loginId, String imeiNo);

	@Query(value = "select exists( select 1 from candidate_device_details where login_id = ?1 and status = 'Active')", nativeQuery = true)
	public boolean deviceDataExistOrNot(String loginId);

	@Query(value = "select * from candidate_device_details where login_id=?1 order by created_on desc Limit 1", nativeQuery = true)
	public UserDeviceInfo getDeviceinfoInstance(String loginId);

	@Query(value = "select imei_no from candidate_device_details where login_id=?1 order by created_on desc Limit 1", nativeQuery = true)
	public String getExistingImeiNo(String loginId);

}
