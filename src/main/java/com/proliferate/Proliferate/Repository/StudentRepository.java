package com.proliferate.Proliferate.Repository;

import com.proliferate.Proliferate.Domain.Entities.Role;
import com.proliferate.Proliferate.Domain.Entities.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface StudentRepository extends JpaRepository<StudentEntity, Long>{

    Optional<StudentEntity> findByUserName(String userName);

    Optional<StudentEntity> findByFirstName(String firstName);

    Optional<StudentEntity> findByEmail(String email);

    Boolean existsByUserName(String userName);

    StudentEntity findByRole(Role role);

    Boolean existsByEmail(String email);

}
