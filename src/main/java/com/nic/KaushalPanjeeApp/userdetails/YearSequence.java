package com.nic.KaushalPanjeeApp.userdetails;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "year_sequence")
public class YearSequence {

	@Id
	private int year;
	private int lastSequence;

}
