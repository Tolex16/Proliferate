package com.proliferate.Proliferate.Domain.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.Basic;
import lombok.Data;
import lombok.NoArgsConstructor;
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

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "profile_image")
    private byte[] profileImage;

    @Column(name = "type", length = 255)
	private String type;
	
	@Column(name = "message", length = 1024)  // Increase the length of the message column
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
