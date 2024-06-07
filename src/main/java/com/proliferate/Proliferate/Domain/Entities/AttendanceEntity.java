package com.proliferate.Proliferate.Domain.Entities;

import com.proliferate.Proliferate.Domain.DTO.Month;
import jakarta.persistence.*;
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
