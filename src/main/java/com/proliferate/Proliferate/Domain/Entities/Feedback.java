package com.proliferate.Proliferate.Domain.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "student_review")
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    @JsonBackReference
    private StudentEntity student;

    @ManyToOne
    @JoinColumn(name = "tutor_id", nullable = false)
    @JsonBackReference
    private TutorEntity tutor;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    @JsonBackReference
    private Subject subject;

    private String sessionDate;

    private int rating;

    private String comments;
}
