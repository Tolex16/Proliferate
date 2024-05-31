package com.proliferate.Proliferate.Repository;

import com.proliferate.Proliferate.Domain.Entities.Role;
import com.proliferate.Proliferate.Domain.Entities.TutorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface TutorRepository extends JpaRepository<TutorEntity, Long>{

    Optional<TutorEntity> findByUserName(String userName);

    Optional<TutorEntity> findByEmail(String email);

    Boolean existsByUserName(String userName);

    TutorEntity findByRole(Role role);

    Boolean existsByEmail(String email);

}
