package com.proliferate.Proliferate.Repository;

import com.proliferate.Proliferate.Domain.Entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    Enrollment findByPaymentIntent(String paymentIntent);
}
