package com.proliferate.Proliferate.Repository;

import com.proliferate.Proliferate.Domain.Entities.Assignment;
import com.proliferate.Proliferate.Domain.Entities.Role;
import com.proliferate.Proliferate.Domain.Entities.TutorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long>{

    Optional<Assigment> findByAssignedStudent(String assignedStudent);

}
