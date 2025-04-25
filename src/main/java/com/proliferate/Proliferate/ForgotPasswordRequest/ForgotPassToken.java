package com.proliferate.Proliferate.ForgotPasswordRequest;

import com.proliferate.Proliferate.Domain.Entities.StudentEntity;
import com.proliferate.Proliferate.Domain.Entities.TutorEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "forgotpass_token")
public class ForgotPassToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String token;

    @ManyToOne(targetEntity = StudentEntity.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id")
    private StudentEntity student;

    @ManyToOne(targetEntity = TutorEntity.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "tutor_id")
    private TutorEntity tutor;

    @Column(nullable = false)
    private LocalDateTime expireTime;

    @Column(nullable = false)
    private boolean isUsed;

    public UserDetails getUser() {
        return student != null ? student : tutor;
    }

    public void setUser(UserDetails user) {
        if (user instanceof StudentEntity) {
            this.student = (StudentEntity) user;
            this.tutor = null;
        } else if (user instanceof TutorEntity) {
            this.tutor = (TutorEntity) user;
            this.student = null;
        }
    }
}
