package com.proliferate.Proliferate.Domain.Entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "scores")
public class Score {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scoreId;
	
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private StudentEntity student;

    private String test;

    private int marks;
    
	private int questionsAttempted;
    
	private int correctAnswers;
    
	private int wrongAnswers;
	
//	@Column(name = "result")
//    @Enumerated(EnumType.STRING)
    private Result result;  // "Pass", "Fail", or "Upcoming"
}
