package com.proliferate.Proliferate.Domain.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "tests")
public class Test {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long testId;
	
	@Column(name = "test_title")
    private String testTitle;
	
	@Column(name = "test_Date")
    private String testDate;
    
	@Column(name = "total_marks")
	private int totalMarks;
}
