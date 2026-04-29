package com.nic.KaushalPanjeeApp.helper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse<T> {
	private String authToken;
	private String passString;
	private Integer responseCode;
	private String responseDesc;

}
