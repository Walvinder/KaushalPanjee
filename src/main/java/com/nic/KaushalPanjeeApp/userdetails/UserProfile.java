package com.nic.KaushalPanjeeApp.userdetails;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "candidate_user_profile")
public class UserProfile {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_profile_id")
	private long userProfileId;

	@Column(name = "login_id", unique = true)
	private String loginId;

	@Column(name = "cast_category")
	private String castCategory;

	@Column(name = "marital_status")
	private String maritalStatus;

	@Column(name = "voter_id")
	private String voterId;

	@Column(name = "dl_no")
	private String dlNo;

	@Column(name = "is_minority")
	private String isMinority;

	@Column(name = "is_disability")
	private String isDisability;

	@Column(name = "is_nrega")
	private String isNrega;

	@Column(name = "nrega_job_card")
	private String nregaJobCard;

	@Column(name = "is_shg")
	private String isShg;

	@Column(name = "shg_no")
	private String shgNo;

	@Column(name = "antyodaya")
	private String antyodaya;

	@Column(name = "is_rsby")
	private String isRsby;

	@Column(name = "is_pip")
	private String isPip;

	@Column(name = "is_pmayg")
	private String isPmayg;

	@Column(name = "poverty_status")
	private String povertyStatus;

	@Column(name = "annual_family_income")
	private int annualFamilyIncome;

	@Column(name = "highest_education")
	private String highestEducation;

	@Column(name = "month_year_of_passing")
	private String monthYearOfPassing;

	@Column(name = "is_tech_educate")
	private String isTechEducate;

	@Column(name = "tech_qualification")
	private int techQualification;

	@Column(name = "month_year_of_passing_tech_edu")
	private String monthYearOfPassingTechEdu;

	@Column(name = "tech_education_domain")
	private int techEducationDomain;

	@Column(name = "language_known")
	private String languageKnown;

	// TODO: added new column for section 5 -- Abhishek
	@Column(name = "highest_class")
	private String highestClass;

	@Column(name = "is_employeed")
	private String isEmployeed;

	@Column(name = "employment_mode")
	private String employmentMode;

	@Column(name = "monthly_earning")
	private int monthlyEarning;

	@Column(name = "intrested_in")
	private String intrestedIn;

	@Column(name = "employment_preference")
	private String employmentPreference;

	@Column(name = "intrested_sector")
	private String intrestedSector;

	@Column(name = "intrested_trade")
	private String intrestedTrade;

	@Column(name = "trade_code")
	private String tradeCode;

//	TODO: added new column for section 5  -- Abhishek 

	@Column(name = "scheme_type")
	private String schemeType;

	@Column(name = "job_location_preference")
	private String jobLocationPreference;

	@Column(name = "expected_monthly_salary")
	private int expectedMonthlySalary;

	@Column(name = "is_pre_training")
	private String isPreTraining;

	@Column(name = "pre_completed_training")
	private String preCompletedTraining;

	@Column(name = "other_pre_completed_training")
	private String otherPreCompletedTraining;

	@Column(name = "comp_training_duration")
	private String compTrainingDuration;

	@Column(name = "digilocker")
	private String digilocker;

	@Column(name = "heared_about_scheme")
	private String hearedAboutScheme;

	@Column(name = "heared_from")
	private String hearedFrom;

	@Column(name = "created_on")
	private Date createdOn;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "updated_on")
	private Date updatedOn;

	@Column(name = "updated_by")
	private String updatedBy;

	public long getUserProfileId() {
		return userProfileId;
	}

	public void setUserProfileId(long userProfileId) {
		this.userProfileId = userProfileId;
	}

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public String getCastCategory() {
		return castCategory;
	}

	public void setCastCategory(String castCategory) {
		this.castCategory = castCategory;
	}

	public String getMaritalStatus() {
		return maritalStatus;
	}

	public void setMaritalStatus(String maritalStatus) {
		this.maritalStatus = maritalStatus;
	}

	public String getVoterId() {
		return voterId;
	}

	public void setVoterId(String voterId) {
		this.voterId = voterId;
	}

	public String getDlNo() {
		return dlNo;
	}

	public void setDlNo(String dlNo) {
		this.dlNo = dlNo;
	}

	public String getIsMinority() {
		return isMinority;
	}

	public void setIsMinority(String isMinority) {
		this.isMinority = isMinority;
	}

	public String getIsDisability() {
		return isDisability;
	}

	public void setIsDisability(String isDisability) {
		this.isDisability = isDisability;
	}

	public String getIsNrega() {
		return isNrega;
	}

	public void setIsNrega(String isNrega) {
		this.isNrega = isNrega;
	}

	public String getNregaJobCard() {
		return nregaJobCard;
	}

	public void setNregaJobCard(String nregaJobCard) {
		this.nregaJobCard = nregaJobCard;
	}

	public String getIsShg() {
		return isShg;
	}

	public void setIsShg(String isShg) {
		this.isShg = isShg;
	}

	public String getShgNo() {
		return shgNo;
	}

	public void setShgNo(String shgNo) {
		this.shgNo = shgNo;
	}

	public String getAntyodaya() {
		return antyodaya;
	}

	public void setAntyodaya(String antyodaya) {
		this.antyodaya = antyodaya;
	}

	public String getIsRsby() {
		return isRsby;
	}

	public void setIsRsby(String isRsby) {
		this.isRsby = isRsby;
	}

	public String getIsPip() {
		return isPip;
	}

	public void setIsPip(String isPip) {
		this.isPip = isPip;
	}

	public String getIsPmayg() {
		return isPmayg;
	}

	public void setIsPmayg(String isPmayg) {
		this.isPmayg = isPmayg;
	}

	public String getPovertyStatus() {
		return povertyStatus;
	}

	public void setPovertyStatus(String povertyStatus) {
		this.povertyStatus = povertyStatus;
	}

	public int getAnnualFamilyIncome() {
		return annualFamilyIncome;
	}

	public void setAnnualFamilyIncome(int annualFamilyIncome) {
		this.annualFamilyIncome = annualFamilyIncome;
	}

	public String getHighestEducation() {
		return highestEducation;
	}

	public void setHighestEducation(String highestEducation) {
		this.highestEducation = highestEducation;
	}

	public String getMonthYearOfPassing() {
		return monthYearOfPassing;
	}

	public void setMonthYearOfPassing(String monthYearOfPassing) {
		this.monthYearOfPassing = monthYearOfPassing;
	}

	public String getIsTechEducate() {
		return isTechEducate;
	}

	public void setIsTechEducate(String isTechEducate) {
		this.isTechEducate = isTechEducate;
	}

	public int getTechQualification() {
		return techQualification;
	}

	public void setTechQualification(int techQualification) {
		this.techQualification = techQualification;
	}

	public String getMonthYearOfPassingTechEdu() {
		return monthYearOfPassingTechEdu;
	}

	public void setMonthYearOfPassingTechEdu(String monthYearOfPassingTechEdu) {
		this.monthYearOfPassingTechEdu = monthYearOfPassingTechEdu;
	}

	public int getTechEducationDomain() {
		return techEducationDomain;
	}

	public void setTechEducationDomain(int techEducationDomain) {
		this.techEducationDomain = techEducationDomain;
	}

	public String getLanguageKnown() {
		return languageKnown;
	}

	public void setLanguageKnown(String languageKnown) {
		this.languageKnown = languageKnown;
	}

	public String getHighestClass() {
		return highestClass;
	}

	public void setHighestClass(String highestClass) {
		this.highestClass = highestClass;
	}

	public String getIsEmployeed() {
		return isEmployeed;
	}

	public void setIsEmployeed(String isEmployeed) {
		this.isEmployeed = isEmployeed;
	}

	public String getEmploymentMode() {
		return employmentMode;
	}

	public void setEmploymentMode(String employmentMode) {
		this.employmentMode = employmentMode;
	}

	public int getMonthlyEarning() {
		return monthlyEarning;
	}

	public void setMonthlyEarning(int monthlyEarning) {
		this.monthlyEarning = monthlyEarning;
	}

	public String getIntrestedIn() {
		return intrestedIn;
	}

	public void setIntrestedIn(String intrestedIn) {
		this.intrestedIn = intrestedIn;
	}

	public String getEmploymentPreference() {
		return employmentPreference;
	}

	public void setEmploymentPreference(String employmentPreference) {
		this.employmentPreference = employmentPreference;
	}

	public String getIntrestedSector() {
		return intrestedSector;
	}

	public void setIntrestedSector(String intrestedSector) {
		this.intrestedSector = intrestedSector;
	}

	public String getIntrestedTrade() {
		return intrestedTrade;
	}

	public void setIntrestedTrade(String intrestedTrade) {
		this.intrestedTrade = intrestedTrade;
	}

	public String getTradeCode() {
		return tradeCode;
	}

	public void setTradeCode(String tradeCode) {
		this.tradeCode = tradeCode;
	}

	public String getSchemeType() {
		return schemeType;
	}

	public void setSchemeType(String schemeType) {
		this.schemeType = schemeType;
	}

	public String getJobLocationPreference() {
		return jobLocationPreference;
	}

	public void setJobLocationPreference(String jobLocationPreference) {
		this.jobLocationPreference = jobLocationPreference;
	}

	public int getExpectedMonthlySalary() {
		return expectedMonthlySalary;
	}

	public void setExpectedMonthlySalary(int expectedMonthlySalary) {
		this.expectedMonthlySalary = expectedMonthlySalary;
	}

	public String getIsPreTraining() {
		return isPreTraining;
	}

	public void setIsPreTraining(String isPreTraining) {
		this.isPreTraining = isPreTraining;
	}

	public String getPreCompletedTraining() {
		return preCompletedTraining;
	}

	public void setPreCompletedTraining(String preCompletedTraining) {
		this.preCompletedTraining = preCompletedTraining;
	}

	public String getOtherPreCompletedTraining() {
		return otherPreCompletedTraining;
	}

	public void setOtherPreCompletedTraining(String otherPreCompletedTraining) {
		this.otherPreCompletedTraining = otherPreCompletedTraining;
	}

	public String getCompTrainingDuration() {
		return compTrainingDuration;
	}

	public void setCompTrainingDuration(String compTrainingDuration) {
		this.compTrainingDuration = compTrainingDuration;
	}

	public String getDigilocker() {
		return digilocker;
	}

	public void setDigilocker(String digilocker) {
		this.digilocker = digilocker;
	}

	public String getHearedAboutScheme() {
		return hearedAboutScheme;
	}

	public void setHearedAboutScheme(String hearedAboutScheme) {
		this.hearedAboutScheme = hearedAboutScheme;
	}

	public String getHearedFrom() {
		return hearedFrom;
	}

	public void setHearedFrom(String hearedFrom) {
		this.hearedFrom = hearedFrom;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

}
