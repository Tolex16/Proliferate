package com.proliferate.Proliferate.ForgotPasswordRequest;

import com.proliferate.Proliferate.Domain.Entities.StudentEntity;
import com.proliferate.Proliferate.Domain.Entities.TutorEntity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserEntity {
    @ManyToOne(targetEntity = StudentEntity.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id")
    private StudentEntity student;

    @ManyToOne(targetEntity = TutorEntity.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "tutor_id")
    private TutorEntity tutor;
}
