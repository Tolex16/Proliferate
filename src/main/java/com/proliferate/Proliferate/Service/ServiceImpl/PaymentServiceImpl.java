package com.proliferate.Proliferate.Service.ServiceImpl;

import com.proliferate.Proliferate.Domain.DTO.Payment.PaymentRequest;
import com.proliferate.Proliferate.Domain.Entities.*;
import com.proliferate.Proliferate.ExeceptionHandler.UserNotFoundException;
import com.proliferate.Proliferate.Repository.*;
import com.proliferate.Proliferate.Response.EarningsHistoryResponse;
import com.proliferate.Proliferate.Response.PaymentHistoryResponse;
import com.proliferate.Proliferate.Response.StripeResponse;
import com.proliferate.Proliferate.Service.EmailService;
import com.proliferate.Proliferate.Service.JwtService;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    @Value("${stripe.api.key}")
    private String stripeSecretKey;
	
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    private final SessionRepository sessionRepository;

    private final StudentRepository studentRepository;

    private final TutorRepository tutorRepository;

    private final AdminRepository adminRepository;
	private final PaymentRepository paymentRepository;

    private final NotificationRepository notificationRepository;
    @Autowired
    private final JwtService jwtService;
	
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
		Long studentId = jwtService.getUserId();
        Session session = sessionRepository.findById(paymentRequest.getSessionId())
            .orElseThrow(() -> new NoSuchElementException("Session not found with ID: " + paymentRequest.getSessionId()));
        
        logger.info("Fetching student with ID: {}", studentId);
        StudentEntity student = studentRepository.findById(studentId)
            .orElseThrow(() -> new NoSuchElementException("Student not found with ID: " + studentId));
		
        // Logging for Payment Intent
        logger.info("Payment Intent created with ID: {}", paymentIntent.getId());

        TutorEntity tutor = session.getTutor();

        // Save Payment
        Payment payment = new Payment();
        payment.setStudent(student);
        payment.setSession(session);
        payment.setAmount(paymentRequest.getAmount());
        payment.setCurrency(paymentRequest.getCurrency());
        payment.setPaymentMethod(paymentRequest.getPaymentMethod());
        payment.setTransactionId(paymentIntent.getId());
        payment.setTutor(tutor);
        payment.setStatus(Status.COMPLETED);
        payment.setDate(LocalDate.now());
		payment.setDescription(paymentRequest.getDescription());
        paymentRepository.save(payment);

        List<AdminEntity> admins = adminRepository.findAll();
        for (AdminEntity admin : admins) {
            Notifications notification = new Notifications();
            notification.setAdmin(admin);
            notification.setType("Payment Confirmation");
            notification.setMessage("Payment received: " + student.getFirstName() + " " + student.getLastName() + " has successfully paid for the tutoring session with " + tutor.getFirstName() + " " + tutor.getLastName() + " on " + payment.getDate() + ".");
            notification.setCreatedAt(LocalDateTime.now());

            notificationRepository.save(notification);
        }

        Notifications notification1 = new Notifications();

        notification1.setStudent(student);

//            if (student.getStudentImage() != null) {
//                notification1.setProfileImage(Base64.getEncoder().encodeToString(student.getStudentImage()));
//            } else {
//                notification1.setProfileImage(null); // or set a default image, if applicable
//            }
        notification1.setType("Payment Confirmation for Session");
        notification1.setMessage("Payment received: Your payment for the tutoring session with  " + tutor.getFirstName() + " " + tutor.getLastName() + " on " + payment.getDate() + " has been successfully processed.");
        notification1.setCreatedAt(LocalDateTime.now());

        notificationRepository.save(notification1);

        } catch (NoSuchElementException e) {
        logger.error("Entity not found", e);
        throw e;
    }
}
	

	
   public void handleFailedPayment(String paymentIntentId) {
        Payment payment = paymentRepository.findByTransactionId(paymentIntentId);

        if (payment != null) {
            payment.setStatus(Status.FAILED);
            paymentRepository.save(payment);

            List<AdminEntity> admins = adminRepository.findAll();
            for (AdminEntity admin : admins) {
                Notifications notification = new Notifications();

                notification.setAdmin(admin);
                notification.setType("Payment Failure Notice");
                notification.setMessage("Payment failure alert: A payment for the tutoring session with " + payment.getTutor().getFirstName() + " " + payment.getTutor().getLastName() + " on " + payment.getDate() + " by " + payment.getStudent().getFirstName() + " " + payment.getStudent().getLastName() +
                        " has failed. Please review the payment details and contact the user to resolve the issue.");
                notification.setCreatedAt(LocalDateTime.now());

                notificationRepository.save(notification);
            }
        } else {
            throw new RuntimeException("Failed payment handling failed: Enrollment or Payment not found");
        }
    }

	public List<PaymentHistoryResponse> getPaymentsByStudentId() {
        Long studentId = jwtService.getUserId();
        StudentEntity student = studentRepository.findById(studentId).orElseThrow();
        List<Payment> payments = paymentRepository.findByStudent(student);
        return payments.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public List<PaymentHistoryResponse> getPaymentsByStudentIdAndDateRange(LocalDate startDate, LocalDate endDate) {
        Long studentId = jwtService.getUserId();
        List<Payment> payments = paymentRepository.findByStudent_StudentIdAndDateBetween(studentId, startDate, endDate);
        return payments.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    public List<EarningsHistoryResponse> getPaymentsByTutorId() {
        Long tutorId = jwtService.getUserId();
        TutorEntity tutor = tutorRepository.findById(tutorId).orElseThrow();
        List<Payment> payments = paymentRepository.findByTutor(tutor);
        return payments.stream().map(this::earningsHistoryResponse).collect(Collectors.toList());
    }

    public List<EarningsHistoryResponse> getPaymentsByTutorIdAndDateRange(LocalDate startDate, LocalDate endDate) {
        Long tutorId = jwtService.getUserId();
        List<Payment> payments = paymentRepository.findByTutor_TutorIdAndDateBetween(tutorId, startDate, endDate);
        return payments.stream().map(this::earningsHistoryResponse).collect(Collectors.toList());
    }
	
	private PaymentHistoryResponse convertToDto(Payment payment) {
        PaymentHistoryResponse dto = new PaymentHistoryResponse();
        dto.setDate(String.valueOf(LocalDate.parse(payment.getDate().toString())));
        dto.setDescription(payment.getDescription());
        dto.setAmount(payment.getAmount());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setStatus(payment.getStatus());

        return dto;
    }

    private EarningsHistoryResponse earningsHistoryResponse(Payment payment) {
        EarningsHistoryResponse dto = new EarningsHistoryResponse();
        StudentEntity student = payment.getStudent();
		//Subject subject = payment.getStudent();
		
		Long tutorId = jwtService.getUserId();
		int classCount = (int) paymentRepository.countByTutorTutorId(tutorId);
		
        dto.setSNo(payment.getId());
        dto.setStudentName(student.getFirstName()  + " " + student.getLastName());
        dto.setDate(String.valueOf(LocalDate.parse(payment.getDate().toString())));
        dto.setTransactionId(payment.getTransactionId());
        dto.setAmount(payment.getAmount());
		dto.setNoOfClasses(classCount);
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setStatus(payment.getStatus());

        return dto;
    }


}
