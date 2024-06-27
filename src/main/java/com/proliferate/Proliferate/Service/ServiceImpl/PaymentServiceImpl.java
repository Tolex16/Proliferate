package com.proliferate.Proliferate.Service.ServiceImpl;

import com.proliferate.Proliferate.Domain.DTO.Chat.ChatMessage;
import com.proliferate.Proliferate.Domain.DTO.Payment.PaymentRequest;
import com.proliferate.Proliferate.Domain.Entities.*;
import com.proliferate.Proliferate.Repository.*;
import com.proliferate.Proliferate.Service.EmailService;
import com.proliferate.Proliferate.Service.MessageService;
import com.proliferate.Proliferate.Service.PaymentService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Token;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
	
    public PaymentIntent createPaymentIntent(PaymentRequest paymentRequest) throws StripeException {
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

            return paymentIntent;
        } catch (StripeException e) {
            logger.error("Error creating payment intent", e);
            throw e;
        }
    }
	
//	private Token createStripeToken(PaymentRequest paymentRequest) throws StripeException {
//        Map<String, Object> cardParams = new HashMap<>();
//        cardParams.put("number", paymentRequest.getCardNumber());
//        cardParams.put("exp_month", Integer.parseInt(paymentRequest.getExpiration().split("/")[0]));
//        cardParams.put("exp_year", Integer.parseInt(paymentRequest.getExpiration().split("/")[1]));
//        cardParams.put("cvc", paymentRequest.getCvv());
//
//        Map<String, Object> tokenParams = new HashMap<>();
//        tokenParams.put("card", cardParams);
//
//        return Token.create(tokenParams);
//    }
	
    private PaymentIntent createCreditCardPaymentIntent(PaymentRequest paymentRequest) throws StripeException {
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
            .setAmount((long) (paymentRequest.getAmount() * 100))
            .setCurrency(String.valueOf(paymentRequest.getCurrency()))
            .setPaymentMethod(paymentRequest.getToken())
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
        Subject subject = subjectRepository.findById(paymentRequest.getSubjectId()).orElseThrow();
        StudentEntity student = studentRepository.findById(paymentRequest.getStudentId()).orElseThrow();

        // Save Enrollment
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setSubject(subject);
        enrollment.setStatus(Status.PENDING);
        enrollment.setPaymentIntent(paymentIntent.getId());
        enrollmentRepository.save(enrollment);

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
    }
	
	
	    @Transactional
    public void fulfillOrder(String paymentIntentId) {
        try {
        Enrollment enrollment = enrollmentRepository.findByPaymentIntent(paymentIntentId);
        Payment payment = paymentRepository.findByTransactionId(paymentIntentId);

        if (enrollment != null && payment != null) {
            enrollment.setStatus(Status.ENROLLED);
            enrollmentRepository.save(enrollment);

            payment.setStatus(Status.COMPLETED);
            payment.setDate(LocalDate.now());
            paymentRepository.save(payment);

            // Send email notifications
            Subject subject = enrollment.getSubject();
            StudentEntity student = enrollment.getStudent();
            TutorEntity tutor = subject.getTutor();
			
            if (student != null && tutor != null) {
                emailService.sendEnrollmentConfirmation(student.getEmail(), subject.getTitle());
                emailService.notifyTutor(tutor.getEmail(), student.getEmail(), subject.getTitle());
            }
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
        dto.setDescription(payment.getDescription());
        return dto;
    }
}
