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
public interface StudentRepository extends JpaRepository<StudentEntity, Long>{

    Optional<StudentEntity> findByUserNameIgnoreCase(String userName);
    Optional<StudentEntity> findByUserNameIgnoreCaseAndEmailVerifiedIsTrue(String userName);
    Optional<StudentEntity> findByFirstName(String firstName);
    Optional<StudentEntity> findByVerificationToken(String token);
    Optional<StudentEntity> findByEmailIgnoreCase(String email);

    Optional<StudentEntity> findByEmailIgnoreCaseAndEmailVerifiedIsTrue(String email);

    Optional<StudentEntity> findByFriendEmailIgnoreCase(String email);

    Boolean existsByUserNameIgnoreCase(String userName);

    StudentEntity findByRole(Role role);

    Boolean existsByEmailIgnoreCase(String email);

    void deleteByUserNameIgnoreCase(String userName);
    @Query("SELECT DISTINCT p.student FROM Payment p WHERE p.tutor.id = :tutorId")
    List<StudentEntity> findStudentsByTutorPayments(@Param("tutorId") Long tutorId);
}
