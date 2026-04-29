package com.nic.KaushalPanjeeApp.helper;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlockResponse<T> {
	private List<T> blockList = new ArrayList<>();
	private Integer responseCode;
	private String responseDesc;
	private String responseMsg;

}
