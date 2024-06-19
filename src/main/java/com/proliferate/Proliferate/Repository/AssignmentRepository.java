package com.proliferate.Proliferate.Repository;

import com.proliferate.Proliferate.Domain.Entities.Assignment;
import com.proliferate.Proliferate.Domain.Entities.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long>{

    List<Assignment> findByAssignedStudent(StudentEntity studentName);

}
