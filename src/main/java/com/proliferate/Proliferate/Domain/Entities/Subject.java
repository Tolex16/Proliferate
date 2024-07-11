package com.proliferate.Proliferate.Domain.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@Entity
@Table(name = "subjects")
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long subjectId;
	
    @Column(name = "title")
    @NotNull(message = "Title cannot be null")
    @NotBlank(message = "Title cannot be blank")
    private String title;

    @ManyToOne
    @JoinColumn(name = "tutor_id", nullable = false)
    private TutorEntity tutor;
	
	@OneToMany(mappedBy = "subject")
    private Set<ClassSchedule> classSchedules;
}
