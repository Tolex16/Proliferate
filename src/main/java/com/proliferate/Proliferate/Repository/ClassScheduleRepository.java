package com.proliferate.Proliferate.Repository;

import com.proliferate.Proliferate.Domain.Entities.ClassSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassScheduleRepository extends JpaRepository<ClassSchedule, Long> {
    List<ClassSchedule> findByStudentName(String studentName);
}
