package com.proliferate.Proliferate.Domain.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.proliferate.Proliferate.Domain.DTO.Chat.MessageType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "assignment")
public class Assignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long assignmentId;
	
    @ManyToOne
    @JoinColumn(name = "student_id")
    private StudentEntity assignedStudent;
	
	@Column(name = "due_date")
    private LocalDate dueDate;
	
	@Column(name = "title")
    private String title;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    @JsonBackReference
    private Subject subject;
	
	@Column(name = "grade_level")
    private String gradeLevel;

    @Column(name = "description")
    private String description;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "assignment_file")
    private byte[] assignmentFile;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "assignment_solution")
    private byte[] assignmentSolution;
}
