package com.proliferate.Proliferate.Domain.Entities;

import com.proliferate.Proliferate.Domain.DTO.Month;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "attendance")
public class AttendanceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "month", nullable = false)
    private Month month;
	
    @Column(name = "scheduled_sessions", nullable = false)
    private int scheduledSessions;

    @Column(name = "attended_sessions", nullable = false)
    private int attendedSessions;

    @Column(name = "student_absences", nullable = false)
    private int studentAbsences;

    @Column(name = "tutor_absences", nullable = false)
    private int tutorAbsences;

}
