package com.proliferate.Proliferate.Repository;

import com.proliferate.Proliferate.Domain.Entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByStudent(StudentEntity student);

	List<Payment> findByTutor(TutorEntity tutor);
	Payment findByTransactionId(String transactionId);
	
	List<Payment> findByStudent_StudentIdAndDateBetween(Long studentId, LocalDate startDate, LocalDate endDate);

	List<Payment> findByTutor_TutorIdAndDateBetween(Long tutorId, LocalDate startDate, LocalDate endDate);

}

