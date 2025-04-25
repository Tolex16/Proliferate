package com.proliferate.Proliferate.Domain.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
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
    @JsonBackReference
    private StudentEntity student;

    @Column(name = "test")
    private String test;

    @Column(name = "marks")
    private int marks;

    @Column(name = "questions_attempted")
	private int questionsAttempted;

    @Column(name = "correct_answers")
	private int correctAnswers;

    @Column(name = "wrong_answers")
	private int wrongAnswers;
	
	@Column(name = "result")
    @Enumerated(EnumType.STRING)
    private Result result;  // "Pass", "Fail", or "Upcoming"
}
