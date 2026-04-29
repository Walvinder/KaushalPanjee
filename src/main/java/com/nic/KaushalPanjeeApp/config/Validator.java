package com.nic.KaushalPanjeeApp.config;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Validator {
	public boolean isEmailValid(String email) {
		if (email == null || email.isEmpty()) {
			return false;
		}
		return email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
	}

	public boolean isMobileValid(String mobileNumber) {
		if (mobileNumber == null || mobileNumber.isEmpty()) {
			return false;
		}
		return mobileNumber.matches("^[6-9][0-9]{9}$");
	}

	public boolean isVoterCardValid(String voterId) {
		if (voterId == null || voterId.isEmpty()) {
			return false;
		}
		// Example: Voter ID must be 15 alphanumeric characters
		return voterId.matches("^[A-Z0-9]{8,15}$");
	}

	public boolean isDrivingLicenseValid(String licenseNumber) {
		if (licenseNumber == null || licenseNumber.isEmpty()) {
			return false;
		}
		// Regex pattern for format validation
		return licenseNumber.matches("^[A-Z0-9]{8,18}$");
	}

	public static boolean isLessThanYears(String dateOfBirthStr, String dateFormat) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
		LocalDate dateOfBirth = LocalDate.parse(dateOfBirthStr, formatter);
		LocalDate today = LocalDate.now();
		LocalDate eighteenYearsAgo = today.minusYears(14);

		// Check if dateOfBirth is after or equal to the date 14 years ago
		return !dateOfBirth.isBefore(eighteenYearsAgo);
	}

	public static boolean isGreaterThanYears(String dateOfBirthStr, String dateFormat) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
		LocalDate dateOfBirth = LocalDate.parse(dateOfBirthStr, formatter);
		LocalDate today = LocalDate.now();
		LocalDate fiftyYearsAgo = today.minusYears(50);

		// Check if dateOfBirth is after or equal to the date 15 years ago
		return !dateOfBirth.isAfter(fiftyYearsAgo);
	}

}
