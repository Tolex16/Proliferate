package com.proliferate.Proliferate.Domain.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "payments")
@NoArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private StudentEntity student;

    @ManyToOne
    @JoinColumn(name = "tutor_id")
    private TutorEntity tutor;

    @Column(name = "amount")
    private double amount;
	
	@Column(name = "currency")
    @Enumerated(EnumType.STRING)
	private Currency currency;

    @Column(name = "method")
    private String paymentMethod;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "description")
    private String description;
}
