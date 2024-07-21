package com.proliferate.Proliferate.Domain.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "notifications")
public class Notifications {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    private String profileImage;

	private String type;
	
	private String message;

    @CreationTimestamp
    private LocalDateTime createdAt;
	
    @ManyToOne
    @JoinColumn(name = "student_id")
    @JsonBackReference
    private StudentEntity student;

    @ManyToOne
    @JoinColumn(name = "tutor_id")
    @JsonBackReference
    private TutorEntity tutor;
	
	@ManyToOne
    @JoinColumn(name = "admin_id")
    @JsonBackReference
    private AdminEntity admin;

}
