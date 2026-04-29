package com.nic.KaushalPanjeeApp.aadhaarLog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankRequest {
    private String loginId;
    private String appVersion;
}
