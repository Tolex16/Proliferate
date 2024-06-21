package com.proliferate.Proliferate.Domain.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "enrollment")
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long enrollmentId;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private StudentEntity student;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;
	
	@Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status; // e.g., "PENDING", "ENROLLED"
	
	@Column(name = "payment_intent")
    private String paymentIntent;

}
