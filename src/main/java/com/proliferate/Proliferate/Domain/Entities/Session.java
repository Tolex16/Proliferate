package com.proliferate.Proliferate.Domain.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@Entity
@Table(name = "sessions")
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sessionId;

    @ManyToOne
    @JoinColumn(name = "subject_id",nullable = false)
    @JsonBackReference
    private Subject subject;

    @ManyToOne
    @JoinColumn(name = "tutor_id", nullable = false)
    @JsonBackReference
    private TutorEntity tutor;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    @JsonBackReference
    private StudentEntity student;

    @Column(name = "frequency", nullable = false)
	private int frequency;

    @Column(name = "duration")
    private String duration;
}
