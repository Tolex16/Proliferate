package com.proliferate.Proliferate.Repository;

import com.proliferate.Proliferate.Domain.Entities.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long>{

    Optional<Assignment> findByAssignedStudent(String assignedStudent);

}
