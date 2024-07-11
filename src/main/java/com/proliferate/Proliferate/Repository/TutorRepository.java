package com.proliferate.Proliferate.Repository;

import com.proliferate.Proliferate.Domain.Entities.Role;
import com.proliferate.Proliferate.Domain.Entities.StudentEntity;
import com.proliferate.Proliferate.Domain.Entities.TutorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
}
