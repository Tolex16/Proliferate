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
    Optional<TutorEntity> findByEmailIgnoreCase(String email);
    Optional<TutorEntity> findByFirstName(String firstName);
    TutorEntity findByRole(Role role);
    Optional<TutorEntity> findByEmailIgnoreCaseAndEmailVerifiedIsTrue(String email);
    Optional<TutorEntity> findByVerificationToken(String token);
    Boolean existsByEmailIgnoreCase(String email);
    void deleteByEmailIgnoreCase(String email);
    @Query("SELECT DISTINCT p.tutor FROM Payment p WHERE p.student.id = :studentId")
    List<TutorEntity> findTutorsByStudentPayments(@Param("studentId") Long studentId);
	
	@Query("SELECT t FROM TutorEntity t WHERE LOWER(:subjectTitle) IN (SELECT LOWER(subject) FROM TutorEntity tut JOIN tut.preferredSubjects subject WHERE tut = t) AND t.teachingGrade = :gradeLevel")
    List<TutorEntity> findTutorsBySubjectAndGrade(@Param("subjectTitle") String subjectTitle, @Param("gradeLevel") String gradeLevel);
}

