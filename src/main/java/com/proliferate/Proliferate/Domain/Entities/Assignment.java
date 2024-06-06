package com.proliferate.Proliferate.Domain.Entities;

import com.proliferate.Proliferate.Domain.DTO.Chat.MessageType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "assignment")
public class Assignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
    @Column(name = "assigned_student")
    private String assignedStudent;
	
	@Column(name = "due_date")
    private String dueDate;
	
	@Column(name = "title")
    private String title;
	
	@Column(name = "subject")
    private String subject;
	
	@Column(name = "grade_level")
    private String gradeLevel;
	
	@Column(name = "student_image")
    @Lob
    private byte[] assignmentFile;
}
