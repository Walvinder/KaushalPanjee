package com.nic.KaushalPanjeeApp.aadhaarLog;

import lombok.Data;

@Data
public class AadhaarTxnRequest {

	  private String txnAadhaar;
	    private String txnApp;
	    private String ret;
	    private String aadhaarCode;
	    private String maskedAadhaar;
}
