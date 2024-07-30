package com.proliferate.Proliferate.Repository;

import com.proliferate.Proliferate.Domain.Entities.Role;
import com.proliferate.Proliferate.Domain.Entities.StudentEntity;
import com.proliferate.Proliferate.Domain.Entities.TutorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface TutorRepository extends JpaRepository<TutorEntity, Long>{
    Optional<TutorEntity> findByEmail(String email);
    Optional<TutorEntity> findByFirstName(String firstName);
    TutorEntity findByRole(Role role);
    Optional<TutorEntity> findByEmailAndEmailVerifiedIsTrue(String email);
    Optional<TutorEntity> findByVerificationToken(String token);
    Boolean existsByEmail(String email);
    void deleteByEmail(String email);
    @Query("SELECT DISTINCT p.tutor FROM Payment p WHERE p.student.id = :studentId")
    List<TutorEntity> findTutorsByStudentPayments(@Param("studentId") Long studentId);

    //@Query("SELECT t FROM TutorEntity t WHERE :subjectTitle IN elements(t.preferredSubjects)")
    //List<TutorEntity> findTutorsByPreferredSubject(@Param("subjectTitle") String subjectTitle);

    @Query("SELECT t FROM TutorEntity t JOIN t.preferredSubjects s WHERE s = :subjectTitle")
    List<TutorEntity> findTutorsByPreferredSubjectContainingIgnoreCase(@Param("subjectTitle") String subjectTitle);
}

