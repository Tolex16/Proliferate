package com.proliferate.Proliferate.Repository;

import com.proliferate.Proliferate.Domain.Entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByStudent(StudentEntity student);
	
	Payment findByTransactionId(String transactionId);
}

