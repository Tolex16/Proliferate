package com.proliferate.Proliferate.Repository;

import com.proliferate.Proliferate.Domain.Entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notifications, Long> {
    List<Notifications> findByStudentStudentId(Long studentId);
    List<Notifications> findByTutorTutorId(Long tutorId);
    List<Notifications> findByAdminAdminId(Long adminId);
}
