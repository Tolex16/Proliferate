package com.proliferate.Proliferate.Service.ServiceImpl;

import com.proliferate.Proliferate.Domain.DTO.Payment.PaymentRequest;
import com.proliferate.Proliferate.Domain.Entities.*;
import com.proliferate.Proliferate.Repository.*;
import com.proliferate.Proliferate.Response.StripeResponse;
import com.proliferate.Proliferate.Service.EmailService;
import com.proliferate.Proliferate.Service.PaymentService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentMethodCreateParams;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    @Value("${stripe.api.key}")
    private String stripeSecretKey;
	
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    private final EnrollmentRepository enrollmentRepository;

    private final SubjectRepository subjectRepository;

    private final StudentRepository studentRepository;
	
	private final PaymentRepository paymentRepository;

    @Autowired
    private EmailService emailService;
	
    public StripeResponse createPaymentIntent(PaymentRequest paymentRequest) throws StripeException {
        Stripe.apiKey = stripeSecretKey;

        try {
			PaymentIntent paymentIntent;
            if ("Credit Card".equalsIgnoreCase(paymentRequest.getPaymentMethod())) {
				// Tokenize the card details
               // Token token = createStripeToken(paymentRequest);
				// Create a payment intent
                paymentIntent = createCreditCardPaymentIntent(paymentRequest);
            } else if ("Bank Transfer".equalsIgnoreCase(paymentRequest.getPaymentMethod())) {
                paymentIntent = createBankTransferPaymentIntent(paymentRequest);
            } else {
                throw new IllegalArgumentException("Unsupported payment method: " + paymentRequest.getPaymentMethod());
            }
            
            
            // Save enrollment and payment details
            saveEnrollmentAndPayment(paymentIntent, paymentRequest);
			
        // Convert to DTO
        StripeResponse response = new StripeResponse();
        response.setId(paymentIntent.getId());
        response.setAmount(paymentIntent.getAmount());
        response.setCurrency(paymentIntent.getCurrency());
        response.setStatus(paymentIntent.getStatus());
		

        return response;
    } catch (StripeException e) {
        logger.error("Error creating payment intent with Stripe", e);
        throw e;
    } catch (Exception e) {
        logger.error("Error creating payment intent", e);
        throw e;
    }
 }
	
    private PaymentIntent createCreditCardPaymentIntent(PaymentRequest paymentRequest) throws StripeException {

        // Create a PaymentMethod using the token
        PaymentMethodCreateParams paymentMethodCreateParams = PaymentMethodCreateParams.builder()
            .setType(PaymentMethodCreateParams.Type.CARD)
            .setCard(PaymentMethodCreateParams.Token.builder().setToken(paymentRequest.getToken()).build())
            .build();		

		PaymentMethod paymentMethod = PaymentMethod.create(paymentMethodCreateParams);

	   PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
            .setAmount((long) (paymentRequest.getAmount() * 100))
            .setCurrency(paymentRequest.getCurrency().toString())
            .setPaymentMethod(paymentMethod.getId())
            .setConfirm(true)
            .build();

        return PaymentIntent.create(params);
    }
	
	private PaymentIntent createBankTransferPaymentIntent(PaymentRequest paymentRequest) throws StripeException {
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
            .setAmount((long) (paymentRequest.getAmount() * 100))
            .setCurrency(String.valueOf(paymentRequest.getCurrency()))
            .addPaymentMethodType("ach_credit_transfer")
            .setPaymentMethodOptions(
                PaymentIntentCreateParams.PaymentMethodOptions.builder()
                    .setAcssDebit(
                        PaymentIntentCreateParams.PaymentMethodOptions.AcssDebit.builder()
                            .setMandateOptions(
                                PaymentIntentCreateParams.PaymentMethodOptions.AcssDebit.MandateOptions.builder()
                                        .setTransactionType(
                                                PaymentIntentCreateParams.PaymentMethodOptions.AcssDebit.MandateOptions.TransactionType.PERSONAL
                                        )
                                        .build()
                            )
                                .setVerificationMethod(
                                        PaymentIntentCreateParams.PaymentMethodOptions.AcssDebit.VerificationMethod.MICRODEPOSITS
                                )
                                .build()
                            )
                            .build()
                    )
            .build();

        return PaymentIntent.create(params);
    }
	   
    private void saveEnrollmentAndPayment(PaymentIntent paymentIntent, PaymentRequest paymentRequest) {
        try {
		logger.info("Fetching subject with ID: {}", paymentRequest.getSubjectId());
        Subject subject = subjectRepository.findById(paymentRequest.getSubjectId())
            .orElseThrow(() -> new NoSuchElementException("Subject not found with ID: " + paymentRequest.getSubjectId()));
        
        logger.info("Fetching student with ID: {}", paymentRequest.getStudentId());
        StudentEntity student = studentRepository.findById(paymentRequest.getStudentId())
            .orElseThrow(() -> new NoSuchElementException("Student not found with ID: " + paymentRequest.getStudentId()));
		
        // Logging for Payment Intent
        logger.info("Payment Intent created with ID: {}", paymentIntent.getId());
		
        // Save Enrollment
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setSubject(subject);
        enrollment.setStatus(Status.PENDING);
        enrollment.setPaymentIntent(paymentIntent.getId());
        enrollmentRepository.save(enrollment);
        logger.info("Enrollment saved with ID: {}", enrollment.getEnrollmentId());
		
        // Save Payment
        Payment payment = new Payment();
        payment.setStudent(student);
        payment.setAmount(paymentRequest.getAmount());
        payment.setCurrency(paymentRequest.getCurrency());
        payment.setPaymentMethod(paymentRequest.getPaymentMethod());
        payment.setStatus(Status.PENDING);
        payment.setTransactionId(paymentIntent.getId());
		payment.setDescription(paymentRequest.getDescription());
        paymentRepository.save(payment);

    } catch (NoSuchElementException e) {
        logger.error("Entity not found", e);
        throw e;
    }
}
	
	    @Transactional
    public void fulfillOrder(String paymentIntentId) {
        try {
        Enrollment enrollment = enrollmentRepository.findByPaymentIntent(paymentIntentId);
        Payment payment = paymentRepository.findByTransactionId(paymentIntentId);
            // Send email notifications
            Subject subject = enrollment.getSubject();
            StudentEntity student = enrollment.getStudent();
            TutorEntity tutor = subject.getTutor();

            if (enrollment != null && payment != null) {
            enrollment.setStatus(Status.ENROLLED);
            enrollmentRepository.save(enrollment);

            payment.setTutor(tutor);
            payment.setStatus(Status.COMPLETED);
            payment.setDate(LocalDate.now());
            paymentRepository.save(payment);


//            if (student != null && tutor != null) {
//                emailService.sendEnrollmentConfirmation(student.getEmail(), subject.getTitle());
//                emailService.notifyTutor(tutor.getEmail(), student.getEmail(), subject.getTitle());
//            }
        } else {
            throw new RuntimeException("Order fulfillment failed: Enrollment or Payment not found");
        }
		
        } catch (Exception e) {
            logger.error("Error fulfilling order", e);
            throw new RuntimeException("Order fulfillment failed");
        }
    }
	
   public void handleFailedPayment(String paymentIntentId) {
        Enrollment enrollment = enrollmentRepository.findByPaymentIntent(paymentIntentId);
        Payment payment = paymentRepository.findByTransactionId(paymentIntentId);

        if (enrollment != null && payment != null) {
            enrollment.setStatus(Status.PENDING);
            enrollmentRepository.save(enrollment);

            payment.setStatus(Status.FAILED);
            paymentRepository.save(payment);
        } else {
            throw new RuntimeException("Failed payment handling failed: Enrollment or Payment not found");
        }
    }
	public List<PaymentRequest> getPaymentsByStudentId(Long studentId) {
        StudentEntity student = studentRepository.findById(studentId).orElseThrow();
        List<Payment> payments = paymentRepository.findByStudent(student);
        return payments.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public List<PaymentRequest> getPaymentsByStudentIdAndDateRange(Long studentId, LocalDate startDate, LocalDate endDate) {
        List<Payment> payments = paymentRepository.findByStudent_StudentIdAndDateBetween(studentId, startDate, endDate);
        return payments.stream().map(this::convertToDto).collect(Collectors.toList());
    }
	
	private PaymentRequest convertToDto(Payment payment) {
        PaymentRequest dto = new PaymentRequest();
        dto.setStudentId(payment.getStudent().getStudentId());
        dto.setAmount(payment.getAmount());
        dto.setCurrency(payment.getCurrency());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setStatus(payment.getStatus());
        dto.setDate(payment.getDate());
		dto.setTransactionId(payment.getTransactionId());
        dto.setDescription(payment.getDescription());
        return dto;
    }
}
