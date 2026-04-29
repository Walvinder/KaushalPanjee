
package com.nic.KaushalPanjeeApp.aadhaarLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankDetailsResponse<T> {

    private List<T> data = new ArrayList<>();
    private Integer responseCode;
    private String responseDesc;
    private String responseMsg;

    public BankDetailsResponse(List<T> data) {
        this.data = data;
    }

    public BankDetailsResponse(List<T> data, Map<String, String> errorsMap) {
        this.data = data;
    }

    public BankDetailsResponse(List<T> data, Integer responseCode, String responseDesc) {
        this.data = data;
        this.responseCode = responseCode;
        this.responseDesc = responseDesc;
    }
}