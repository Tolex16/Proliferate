package com.proliferate.Proliferate.Service;

import com.proliferate.Proliferate.Domain.DTO.Gender;
import com.proliferate.Proliferate.ExeceptionHandler.EmailSendingException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.List;


@Service
@RequiredArgsConstructor
public class EmailService {

    @Autowired
    private final JavaMailSender javaMailSender;

   public void studentRegistrationConfirmationEmail(
            String to, String studentFirstName, String studentLastName, String email, Gender gender, String contactNumber, 
            int age, String grade, String subjects, String tutoringFormat, String availability, 
            String additionalPreferences, String shortTermGoals, String longTermGoals, String token) throws EmailSendingException {
        
        String subject = "Welcome to Proliferate!";
        String body = buildEmailBody(studentFirstName, studentLastName, email, gender, contactNumber, age, grade, subjects, 
                                     tutoringFormat, availability, additionalPreferences, shortTermGoals, longTermGoals, token);

        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true); // Enable HTML content
            helper.setFrom(new InternetAddress("tolex20004real@gmail.com", "Proliferate Team"));
            helper.setReplyTo("noreply@yourdomain.com");

        // Add the logo as an inline resource
        Resource logoResource = new ClassPathResource("logo/proliferate-logo.png");
        helper.addInline("proliferateLogo", logoResource);
        if (logoResource.exists()) {
			System.out.println("Logo found!");
			} else {
		System.out.println("Logo not found!");}
		
        // Add the drawing as an inline resource
        Resource signatureResource = new ClassPathResource("logo/signature.jpg");
        helper.addInline("signature", signatureResource);
        if (signatureResource.exists()) {
			System.out.println("Signature found!");
			} else {
		System.out.println("Signature not found!");}
		
            javaMailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
            // Optionally log the exception or rethrow it as a custom exception
        throw new EmailSendingException("Failed to send email", e);
        }
    }

    private String buildEmailBody(
        String studentFirstName, String studentLastName, String email, Gender gender, String contactNumber, 
        int age, String grade, String subjects, String tutoringFormat, String availability, 
        String additionalPreferences, String shortTermGoals, String longTermGoals, String token) {
    
    StringBuilder bodyBuilder = new StringBuilder();
    bodyBuilder.append("<html>");
    bodyBuilder.append("<body style=\"font-family: Arial, sans-serif; line-height: 1.6; background-color: #f9f9f9; margin: 0; padding: 0;\">");
    bodyBuilder.append("<div style=\"max-width: 600px; margin: 20px auto; padding: 20px; background-color: #fff; border-radius: 10px; border: 1px solid #ddd; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);\">");
    bodyBuilder.append("<h1 style=\"text-align: center; color: #4CAF50;\">Congratulations</h1>");
    bodyBuilder.append("<p style=\"text-align: center;\">Congratulations on successfully registering with Proliferate! We're excited to have you join our community of learners and tutors.</p>");
    bodyBuilder.append("<p>Here's a summary of the information you provided during registration:</p>");

    bodyBuilder.append("<h2 style=\"color: #333;\">Personal Information:</h2>");
    bodyBuilder.append("<ul>");
    bodyBuilder.append("<li>Full Name: ").append(studentLastName).append(" ").append(studentFirstName).append("</li>");
    bodyBuilder.append("<li>Email Address: ").append(email).append("</li>");
    bodyBuilder.append("<li>Gender: ").append(gender).append("</li>");
    bodyBuilder.append("<li>Contact Number: ").append(contactNumber).append("</li>");
    bodyBuilder.append("<li>Age: ").append(age).append("</li>");
    bodyBuilder.append("</ul>");
    
    bodyBuilder.append("<h2 style=\"color: #333;\">Academic Details:</h2>");
    bodyBuilder.append("<ul>");
    bodyBuilder.append("<li>Grade/Year: ").append(grade).append("</li>");
    bodyBuilder.append("<li>Subjects Needing Tutoring: ").append(subjects).append("</li>");
    bodyBuilder.append("</ul>");
    
    bodyBuilder.append("<h2 style=\"color: #333;\">Tutoring Preferences:</h2>");
    bodyBuilder.append("<ul>");
    bodyBuilder.append("<li>Preferred Tutoring Format: ").append(tutoringFormat).append("</li>");
    bodyBuilder.append("<li>Availability: ").append(availability).append("</li>");
    bodyBuilder.append("<li>Additional Preferences/Requirements: ").append(additionalPreferences).append("</li>");
    bodyBuilder.append("</ul>");
    
    bodyBuilder.append("<h2 style=\"color: #333;\">Learning Goals:</h2>");
    bodyBuilder.append("<ul>");
    bodyBuilder.append("<li>Short-term Goals: ").append(shortTermGoals).append("</li>");
    bodyBuilder.append("<li>Long-term Goals: ").append(longTermGoals).append("</li>");
    bodyBuilder.append("</ul>");
    
	bodyBuilder.append("<p style=\"text-align: left; margin-top: 30px;\"><a href=\"").append(token).append("\" style=\"background-color: #0000ff; color: #fff; padding: 10px 20px; text-decoration: none; border-radius: 5px;\">Login</a></p>");
	
    bodyBuilder.append("<p>Thank you for providing this information. It will help us match you with the most suitable tutors and tailor your learning experience to meet your needs and goals.</p>");
    bodyBuilder.append("<p>If you have any questions or need assistance, please don't hesitate to <a href=\"https://proliferate.ai/contact\" style=\"color: #4CAF50;\">contact us</a>. Our team is here to help you every step of the way.</p>");
    
	bodyBuilder.append("<p style=\"margin-top: 40px ;\"> We are excited to see you grow and excel with Proliferate.</p>");
    bodyBuilder.append("<p>Welcome aboard, and happy learning!</p>");
    
    
    // Add the drawing image
    bodyBuilder.append("<div style=\"text-align: left; margin: 20px 0;\"><img src=\"cid:signature\" alt=\"Signature\" style=\"width: 100%; max-width: 400px;\"></div>");
    
    bodyBuilder.append("<p>Best regards,<br>The Proliferate Team</p>");
    bodyBuilder.append("<div style=\"text-align: right; margin-top: 20px;\"><img src=\"cid:proliferateLogo\" alt=\"Proliferate Logo\" style=\"width: 100px;\"></div>");
    bodyBuilder.append("<hr style=\"margin: 20px 0;\">");
    bodyBuilder.append("<p style=\"font-size: 12px; color: #999; text-align: center;\">This is an automated email. Please do not reply to this email. If you have any questions, visit our <a href=\"https://proliferate.ai/contact\" style=\"color: #4CAF50;\">Contact Us</a> page.</p>");
    bodyBuilder.append("</div>");
    bodyBuilder.append("</body>");
    bodyBuilder.append("</html>");

    return bodyBuilder.toString();
}

	
	//"<img src=\"src\\main\\resources\\logo\\proliferate-logo.png\" alt=\"Proliferate Logo\">"


public void sendInvitationEmail(String to, String friendName, String senderName) {
    String subject = "Join Proliferate and Start Learning Together!";

    // Customize the email body with a user-friendly HTML template
    String body = "<html>" +
            "<body style=\"font-family: Arial, sans-serif;\">" +
            "<div style=\"max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px;\">" +
            "<div style=\"text-align: center;\">" +
            "<img src=\"https://proliferate.ai/logo.png\" alt=\"Proliferate Logo\" style=\"max-width: 200px;\">" +
            "</div>" +
            "<h2 style=\"color: #333;\">Hi " + friendName + ",</h2>" +
            "<p>I hope this email finds you well! I wanted to reach out and share something exciting with you.</p>" +
            "<p>I've recently started using <strong>Proliferate</strong>, an amazing online learning platform that I think you'd love too!</p>" +
            "<p>Proliferate offers a wide range of courses and resources to help you enhance your skills and knowledge in various subjects.</p>" +
            "<p>From math, reading, and science to languages and arts, there's something for everyone to explore and learn.</p>" +
            "<p>As a member of Proliferate, you'll have access to:</p>" +
            "<ul>" +
            "<li>The best-in-class teachers, well-versed in their field</li>" +
            "<li>Flexible learning options to fit your schedule</li>" +
            "<li>An Online book club, Educational Games And much more!</li>" +
            "</ul>" +
            "<p>I have already found the platform incredibly valuable in my own learning journey, and I believe you'll benefit from it too.</p>" +
            "<p>To join Proliferate and start learning, simply click on the link below to create your account.</p>" +
            "<div style=\"text-align: center; margin: 20px 0;\">" +
            "<a href=\"https://proliferate.ai\" style=\"background-color: #007BFF; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;\">Join Proliferate</a>" +
            "</div>" +
            "<p>I look forward to seeing you on Proliferate and exploring all the amazing learning opportunities together!</p>" +
             "<div style=\"text-align: left; margin: 20px 0;\"><img src=\"cid:signature\" alt=\"Signature\" style=\"width: 100%; max-width: 400px;\"></div>"
    
            + "<p>Best regards,</p>" +
			"<p>" + senderName + "</p>"
            + "<div style=\"text-align: right; margin-top: 20px;\"><img src=\"cid:proliferateLogo\" alt=\"Proliferate Logo\" style=\"width: 100px;\"></div>" +
            
            "<hr style=\"border-top: 1px solid #ddd;\">" +
            "<p style=\"font-size: 12px; color: #555; text-align: center;\">© 2024 proliferate.ai All rights reserved.</p>" +
            "<p style=\"font-size: 12px; color: #555; text-align: center;\">For any questions, please visit our <a href=\"https://proliferate.ai/contact\">contact us</a> page or call us at 1-289-952-2596.</p>" +
            "<p style=\"font-size: 12px; color: #555; text-align: center;\"><strong>Note: This email was sent from an address that cannot receive replies. Please do not reply to this email.</strong></p>" +
            "</div>" +
            "</body>" +
            "</html>";

    MimeMessage message = javaMailSender.createMimeMessage();
    try {
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, true); // Enable HTML content
        helper.setFrom(new InternetAddress("noreply@proliferate.ai", "The Proliferate Team"));
        helper.setReplyTo(new InternetAddress("noreply@proliferate.ai"));

        // Add the logo as an inline resource
        Resource logoResource = new ClassPathResource("logo/proliferate-logo.png");
        helper.addInline("proliferateLogo", logoResource);
        if (logoResource.exists()) {
			System.out.println("Logo found!");
			} else {
		System.out.println("Logo not found!");}
		
        // Add the drawing as an inline resource
        Resource signatureResource = new ClassPathResource("logo/signature.jpg");
        helper.addInline("signature", signatureResource);
        if (signatureResource.exists()) {
			System.out.println("Signature found!");
			} else {
		System.out.println("Signature not found!");}
		
        javaMailSender.send(message);
    } catch (MessagingException | UnsupportedEncodingException e) {
        // Handle exception
        e.printStackTrace();
    }
}


   public void sendPasswordMail(String to, String firstName, String token) {
    String subject = "Proliferate: Password Reset Notification";

    // Customize the email body with HTML formatting
    String body = "<html>"
            + "<body style=\"font-family: Arial, sans-serif; line-height: 1.6;\">"
            + "<div style=\"max-width: 600px; margin: auto; padding: 20px; border: 1px solid #ddd; border-radius: 5px;\">"
            + "<h2 style=\"color: #333;\">Hello " + firstName + ",</h2>"
            + "<p>We have received a request to reset your password.</p>"
            + "<p>Please use the following OTP to reset your password:</p>"
            + "<p><strong style=\"font-size: 24px;\">" + token + "</strong></p>"
            + "<p>If you did not request this password reset, please ignore this email.</p>"
            
			
            + "<div style=\"text-align: left; margin: 20px 0;\"><img src=\"cid:signature\" alt=\"Signature\" style=\"width: 100%; max-width: 400px;\"></div>"
    
            + "<p>Best regards,<br>The Proliferate Team</p>"
			
            + "<div style=\"text-align: right; margin-top: 20px;\"><img src=\"cid:proliferateLogo\" alt=\"Proliferate Logo\" style=\"width: 100px;\"></div>"
    
			
            + "<hr style=\"margin: 20px 0;\">"
            + "<p style=\"font-size: 12px; color: #999;\">This is an automated email. Please do not reply to this email. If you have any questions, visit our <a href=\"https://proliferate.ai/contact\">Contact Us</a> page.</p>"
            + "</div>"
            + "</body>"
            + "</html>";

    MimeMessage message = javaMailSender.createMimeMessage();
    try {
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, true); // Enable HTML content
        helper.setFrom(new InternetAddress("tolex20004real@gmail.com", "The Proliferate Team"));
        helper.setReplyTo("noreply@yourdomain.com");
		
        // Add the logo as an inline resource
        Resource logoResource = new ClassPathResource("logo/proliferate-logo.png");
        helper.addInline("proliferateLogo", logoResource);
        if (logoResource.exists()) {
			System.out.println("Logo found!");
			} else {
		System.out.println("Logo not found!");}
		
        // Add the drawing as an inline resource
        Resource signatureResource = new ClassPathResource("logo/signature.jpg");
        helper.addInline("signature", signatureResource);
        if (signatureResource.exists()) {
			System.out.println("Signature found!");
			} else {
		System.out.println("Signature not found!");}
		
        javaMailSender.send(message);
    } catch (MessagingException | UnsupportedEncodingException e) {
        e.printStackTrace();
        // Optionally log the exception or rethrow it as a custom exception
    }
}


    public void tutorRegistrationConfirmationEmail(
            String to, String tutorFirstName, String tutorLastName, String email, Gender gender,
            String contactNumber, int age, String highestEducationLevelAttained, String majorFieldOfStudy, String yearsOfTeachingExperience,
            String teachingGrade, String currentSchool, String teachingStyle, String studentAssessmentApproach, String availableForAdditionalSupport,
            String attendanceType, List<String> preferredSubjects, String token) {

        String subject = "Welcome to Proliferate!";
        String body = tutorEmailBody(tutorFirstName, tutorLastName, email, gender,
                contactNumber, age, highestEducationLevelAttained, majorFieldOfStudy, yearsOfTeachingExperience,
                teachingGrade, currentSchool, teachingStyle, studentAssessmentApproach, availableForAdditionalSupport,
                attendanceType, preferredSubjects, token);

        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true); // Enable HTML content
            helper.setFrom(new InternetAddress("tolex20004real@gmail.com", "Proliferate Team"));
            helper.setReplyTo("noreply@yourdomain.com");

        // Add the logo as an inline resource
        Resource logoResource = new ClassPathResource("logo/proliferate-logo.png");
        helper.addInline("proliferateLogo", logoResource);
        if (logoResource.exists()) {
			System.out.println("Logo found!");
			} else {
		System.out.println("Logo not found!");}
		
        // Add the drawing as an inline resource
        Resource signatureResource = new ClassPathResource("logo/signature.jpg");
        helper.addInline("signature", signatureResource);
        if (signatureResource.exists()) {
			System.out.println("Signature found!");
			} else {
		System.out.println("Signature not found!");}
		
            javaMailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
            // Optionally log the exception or rethrow it as a custom exception
        }
    }

    private String tutorEmailBody(
            String tutorFirstName, String tutorLastName, String email, Gender gender,
            String contactNumber, int age, String highestEducationLevelAttained, String majorFieldOfStudy, String yearsOfTeachingExperience,
            String teachingGrade, String currentSchool, String teachingStyle, String studentAssessmentApproach, String availableForAdditionalSupport,
            String attendanceType, List<String> preferredSubjects, String token) {

        StringBuilder bodyBuilder = new StringBuilder();
        bodyBuilder.append("<html>");
        bodyBuilder.append("<body style=\"font-family: Arial, sans-serif; line-height: 1.6; background-color: #f9f9f9; margin: 0; padding: 0;\">");
        bodyBuilder.append("<div style=\"max-width: 600px; margin: 20px auto; padding: 20px; background-color: #fff; border-radius: 10px; border: 1px solid #ddd; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);\">");
        bodyBuilder.append("<h1 style=\"text-align: center; color: #4CAF50;\">Congratulations</h1>");

        bodyBuilder.append("<h2 style=\"color: #333;\">Dear ").append(tutorLastName).append(" ").append(tutorFirstName).append(",</h2>");
        bodyBuilder.append("<p>Congratulations on successfully registering with Proliferate! We're excited to have you join our community of learners and tutors.</p>");
        bodyBuilder.append("<p>Here's a summary of the information you provided during registration:</p>");

        bodyBuilder.append("<h3>Personal Information:</h3>");
        bodyBuilder.append("<ul>");
        bodyBuilder.append("<li>Full Name: ").append(tutorLastName).append(" ").append(tutorFirstName).append("</li>");
        bodyBuilder.append("<li>Email Address: ").append(email).append("</li>");
        bodyBuilder.append("<li>Gender: ").append(gender).append("</li>");
        bodyBuilder.append("<li>Contact Number: ").append(contactNumber).append("</li>");
        bodyBuilder.append("<li>Age: ").append(age).append("</li>");
        bodyBuilder.append("</ul>");

        bodyBuilder.append("<h3>Education and Experience:</h3>");
        bodyBuilder.append("<ul>");
        bodyBuilder.append("<li>Highest Education Level Attained: ").append(highestEducationLevelAttained).append("</li>");
        bodyBuilder.append("<li>Major/Field of Study: ").append(majorFieldOfStudy).append("</li>");
        bodyBuilder.append("<li>Years of Teaching Experience: ").append(yearsOfTeachingExperience).append("</li>");
        bodyBuilder.append("<li>Teaching Grade: ").append(teachingGrade).append("</li>");
        bodyBuilder.append("<li>Current School: ").append(currentSchool).append("</li>");
        bodyBuilder.append("</ul>");

        bodyBuilder.append("<h3>Teaching Style and Approach:</h3>");
        bodyBuilder.append("<ul>");
        bodyBuilder.append("<li>Teaching Style: ").append(teachingStyle).append("</li>");
        bodyBuilder.append("<li>Student Assessment Approach: ").append(studentAssessmentApproach).append("</li>");
        bodyBuilder.append("<li>Available for Additional Support: ").append(availableForAdditionalSupport).append("</li>");
        bodyBuilder.append("<li>Attendance Type: ").append(attendanceType).append("</li>");
        bodyBuilder.append("<li>Preferred Subjects: ").append(preferredSubjects).append("</li>");
        bodyBuilder.append("</ul>");

        bodyBuilder.append("<p>Thank you for providing this information. It will help us match you with students that fit your expertise, teaching style, and availability. If you have any questions or need assistance, please don't hesitate to <a href=\"https://proliferate.ai/contact\">contact us</a>. Our team is here to help you every step of the way.</p>");
        bodyBuilder.append("<p>We are confident that you will make a valuable contribution to our platform and positively impact the learning journey of our students. If you have any questions or need assistance, please feel free to reach out to us at <a href=\"mailto:support@proliferate.ai\">support@proliferate.ai</a>. Our team is here to support you every step of the way.</p>");
        bodyBuilder.append("<p>We are excited to support you as you help shape the futures of learners around the world. Our support team is always here to help.</p>");
        bodyBuilder.append("<p>Welcome aboard, and we're excited to start this journey together!</p>");

        bodyBuilder.append("<p style=\"text-align: center; margin-top: 30px;\"><a style=\"background-color: #0000ff; color: #fff; padding: 10px 20px; text-decoration: none; border-radius: 5px;\" href=\"").append(token).append("\">Setup Your Tutor Profile Now</a></p>");
        bodyBuilder.append("<div style=\"text-align: left; margin: 40px 0;\"><img src=\"cid:signature\" alt=\"Signature\" style=\"width: 100px;\"></div>");

        bodyBuilder.append("<p>Best regards,<br>The Proliferate Team</p>");
        bodyBuilder.append("<div style=\"text-align: center; margin-top: 20px;\"><img src=\"cid:proliferateLogo\" alt=\"Proliferate Logo\" style=\"width: 100px;\"></div>");
        bodyBuilder.append("<hr style=\"margin: 20px 0;\">");
        bodyBuilder.append("<p style=\"font-size: 12px; color: #999;\">This is an automated email. Please do not reply to this email. If you have any questions, visit our <a href=\"https://proliferate.ai/contact\">Contact Us</a> page.</p>");
        bodyBuilder.append("</div>");
        bodyBuilder.append("</body>");
        bodyBuilder.append("</html>");

        return bodyBuilder.toString();
    }
 	
    public void sendClassEnrollmentConfirmationEmail(
            String to, String studentFirstName, String studentLastName, String className, String startDate,String tutorFirstName, String tutorLastName) throws EmailSendingException {

        String subject = "Class Enrollment Confirmation";
        String body = buildEnrollmentEmailBody(studentFirstName, studentLastName, className, startDate, tutorFirstName, tutorLastName);

        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true); // Enable HTML content
            helper.setFrom(new InternetAddress("tolex20004real@gmail.com", "Proliferate Team"));
            helper.setReplyTo("noreply@yourdomain.com");
			
        // Add the logo as an inline resource
        Resource logoResource = new ClassPathResource("logo/proliferate-logo.png");
        helper.addInline("proliferateLogo", logoResource);
        if (logoResource.exists()) {
			System.out.println("Logo found!");
			} else {
		System.out.println("Logo not found!");}
		
        // Add the drawing as an inline resource
        Resource signatureResource = new ClassPathResource("logo/signature.jpg");
        helper.addInline("signature", signatureResource);
        if (signatureResource.exists()) {
			System.out.println("Signature found!");
			} else {
		System.out.println("Signature not found!");}

            javaMailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
            // Optionally log the exception or rethrow it as a custom exception
            throw new EmailSendingException("Failed to send email", e);
        }
    }

    private String buildEnrollmentEmailBody(String studentFirstName, String studentLastName, String className, String startDate,String tutorFirstName, String tutorLastName) {
        return "<html>" +
                "<body style=\"font-family: Arial, sans-serif; line-height: 1.6; background-color: #f9f9f9; margin: 0; padding: 0;\">" +
                "<div style=\"max-width: 600px; margin: 20px auto; padding: 20px; background-color: #fff; border-radius: 10px; border: 1px solid #ddd; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);\">"  +
                "<h2 style=\"text-align: center; color: #333;\">Class Enrollment <br> Confirmation</h2>" +
                "<p>Dear " + studentLastName + " " + studentFirstName + ",</p>" +
                "<p>Congratulations! You are now enrolled in <strong>" + className + "</strong> on Proliferate.ai.</p>" +
                "<p><strong>Class Start Date:</strong> " + startDate + "<br>" +
                "<strong>Tutor:</strong> " + tutorLastName + " " + tutorFirstName +  "</p>" +
                "<p>We're excited to have you in the class and look forward to your participation.</p>" +
                "<p>Happy learning!</p>" +
                "<div style=\"text-align: left; margin: 20px 0;\">" +
                "<img src=\"cid:signature\" alt=\"Signature\" style=\"width: 100px;\">" +
                "</div>" +
                "<p>Best regards,<br>The Proliferate Team</p>" +
                "<div style=\"text-align: center; margin-top: 20px;\"><img src=\"cid:proliferateLogo\" alt=\"Proliferate Logo\" style=\"width: 100px;\"></div>" +
                "<hr style=\"margin: 20px 0;\">" +
                "<p style=\"font-size: 12px; color: #999; text-align: center;\">This is an automated email. Please do not reply to this email. If you have any questions, visit our <a href=\"https://proliferate.ai/contact\" style=\"color: #4CAF50;\">Contact Us</a> page.</p>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
	
 public void sendPaymentConfirmationEmail(
            String to, String studentFirstName, String studentLastName, double amount, LocalDate date,String paymentMethod) throws EmailSendingException {

        String subject = "Payment Confirmation";
        String body = buildPaymentConfirmationEmailBody(studentFirstName, studentLastName,amount, String.valueOf(date), paymentMethod);

        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true); // Enable HTML content
            helper.setFrom(new InternetAddress("tolex20004real@gmail.com", "Proliferate Team"));
            helper.setReplyTo("noreply@yourdomain.com");
			
        // Add the logo as an inline resource
        Resource logoResource = new ClassPathResource("logo/proliferate-logo.png");
        helper.addInline("proliferateLogo", logoResource);
        if (logoResource.exists()) {
			System.out.println("Logo found!");
			} else {
		System.out.println("Logo not found!");}
		
        // Add the drawing as an inline resource
        Resource signatureResource = new ClassPathResource("logo/signature.jpg");
        helper.addInline("signature", signatureResource);
        if (signatureResource.exists()) {
			System.out.println("Signature found!");
			} else {
		System.out.println("Signature not found!");}

            javaMailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
            // Optionally log the exception or rethrow it as a custom exception
            throw new EmailSendingException("Failed to send email", e);
        }
    }

    private String buildPaymentConfirmationEmailBody(String studentFirstName, String studentLastName, double amount, String date,String paymentMethod) {
        return "<html>" +
                "<body style=\"font-family: Arial, sans-serif; line-height: 1.6; background-color: #f9f9f9; margin: 0; padding: 0;\">" +
                "<div style=\"max-width: 600px; margin: 20px auto; padding: 20px; background-color: #fff; border-radius: 10px; border: 1px solid #ddd; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);\">"  +
                "<h2 style=\"text-align: center; color: #333;\">Payment Confirmation</h2>" +
                "<p>Dear " + studentLastName + " " + studentFirstName + ",</p>" +
                "<p>Thank you for your payment! We have successfully received your monthly subscription payment for Proliferate.ai.</p>" +
                
				"<p><strong>Transaction Details:</strong>" +
                "<p> Amount: </p> " + amount +  "</p>" +
				"<p> Date: </p> " + date +  "</p>" +
				"<p> Payment Method: </p> " + paymentMethod +  "</p>" +
				
                "<p>Your subscription is now active for another month. Enjoy learning with us!</p>" +
                
                "<div style=\"text-align: left; margin: 40px 0;\">" +
                "<img src=\"cid:signature\" alt=\"Signature\" style=\"width: 100px;\">" +
                "</div>" +
                "<p>Best regards,<br>The Proliferate Team</p>" +
                "<div style=\"text-align: center; margin-top: 20px;\"><img src=\"cid:proliferateLogo\" alt=\"Proliferate Logo\" style=\"width: 100px;\"></div>" +
                "<hr style=\"margin: 20px 0;\">" +
                "<p style=\"font-size: 12px; color: #999; text-align: center;\">This is an automated email. Please do not reply to this email. If you have any questions, visit our <a href=\"https://proliferate.ai/contact\" style=\"color: #4CAF50;\">Contact Us</a> page.</p>" +
                "</div>" +
                "</body>" +
                "</html>";
    }

public void sendPaymentFailureEmail(
            String to, String studentFirstName, String studentLastName) throws EmailSendingException {

        String subject = "Payment Failure Notification";
        String body = buildPaymentFailureEmailBody(studentFirstName, studentLastName);

        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true); // Enable HTML content
            helper.setFrom(new InternetAddress("tolex20004real@gmail.com", "Proliferate Team"));
            helper.setReplyTo("noreply@yourdomain.com");
			
        // Add the logo as an inline resource
        Resource logoResource = new ClassPathResource("logo/proliferate-logo.png");
        helper.addInline("proliferateLogo", logoResource);
        if (logoResource.exists()) {
			System.out.println("Logo found!");
			} else {
		System.out.println("Logo not found!");}
		
        // Add the drawing as an inline resource
        Resource signatureResource = new ClassPathResource("logo/signature.jpg");
        helper.addInline("signature", signatureResource);
        if (signatureResource.exists()) {
			System.out.println("Signature found!");
			} else {
		System.out.println("Signature not found!");}

            javaMailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
            // Optionally log the exception or rethrow it as a custom exception
            throw new EmailSendingException("Failed to send email", e);
        }
    }

    private String buildPaymentFailureEmailBody(String studentFirstName, String studentLastName) {
        return "<html>" +
                "<body style=\"font-family: Arial, sans-serif; line-height: 1.6; background-color: #f9f9f9; margin: 0; padding: 0;\">" +
                "<div style=\"max-width: 600px; margin: 20px auto; padding: 20px; background-color: #fff; border-radius: 10px; border: 1px solid #ddd; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);\">"  +
                "<h2 style=\"text-align: center; color: #333;\">Payment Failure  <br> Notification</h2>" +
                "<p>Dear " + studentLastName + " " + studentFirstName + ",</p>" +
				
                "<p>We were unable to process your monthly subscription payment for Proliferate.ai. This could be due to an issue with your payment method.</p>" +
				"<p>Please update your payment information to ensure uninterrupted access to our classs and resources.</p>" +

				
                "<p>If you have any questions or need assistance, feel free to contact our support team at support@proliferate.ai.</p>" +
                
                "<div style=\"text-align: left; margin: 40px 0;\">" +
                "<img src=\"cid:signature\" alt=\"Signature\" style=\"width: 100px;\">" +
                "</div>" +
                "<p>Best regards,<br>The Proliferate Team</p>" +
                "<div style=\"text-align: center; margin-top: 20px;\"><img src=\"cid:proliferateLogo\" alt=\"Proliferate Logo\" style=\"width: 100px;\"></div>" +
                "<hr style=\"margin: 20px 0;\">" +
                "<p style=\"font-size: 12px; color: #999; text-align: center;\">This is an automated email. Please do not reply to this email. If you have any questions, visit our <a href=\"https://proliferate.ai/contact\" style=\"color: #4CAF50;\">Contact Us</a> page.</p>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
	
	public void sendNewAssignmentNotificationEmail(
            String to, String studentFirstName, String studentLastName, String assignmentTitle, String className, LocalDate dueDate) throws EmailSendingException {

        String subject = "New Assignment Notification";
        String body = buildNewAssignmentNotificationEmailBody(studentFirstName,studentLastName, assignmentTitle, className, dueDate);

        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true); // Enable HTML content
            helper.setFrom(new InternetAddress("tolex20004real@gmail.com", "Proliferate Team"));
            helper.setReplyTo("noreply@yourdomain.com");
			
        // Add the logo as an inline resource
        Resource logoResource = new ClassPathResource("logo/proliferate-logo.png");
        helper.addInline("proliferateLogo", logoResource);
        if (logoResource.exists()) {
			System.out.println("Logo found!");
			} else {
		System.out.println("Logo not found!");}
		
        // Add the drawing as an inline resource
        Resource signatureResource = new ClassPathResource("logo/signature.jpg");
        helper.addInline("signature", signatureResource);
        if (signatureResource.exists()) {
			System.out.println("Signature found!");
			} else {
		System.out.println("Signature not found!");}

            javaMailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
            // Optionally log the exception or rethrow it as a custom exception
            throw new EmailSendingException("Failed to send email", e);
        }
    }

    private String buildNewAssignmentNotificationEmailBody(String studentFirstName, String studentLastName, String assignmentTitle, String className, LocalDate dueDate) {
        return "<html>" +
                "<body style=\"font-family: Arial, sans-serif; line-height: 1.6; background-color: #f9f9f9; margin: 0; padding: 0;\">" +
                "<div style=\"max-width: 600px; margin: 20px auto; padding: 20px; background-color: #fff; border-radius: 10px; border: 1px solid #ddd; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);\">"  +
                "<h2 style=\"text-align: center; color: #333;\">New Assignment <br>  Notification</h2>" +
                "<p>Dear " + studentLastName + " " + studentFirstName + ",</p>" +
				
                "<p>A new assignment, " + assignmentTitle + " , has been posted in " + className+"</p>" +
				
				"<p>Due Date: </p> " + dueDate +  "</p>" +
				
				"<p>Please log in to your account to review the assignment details and submit your work by the due date.</p>" +

				     
                "<div style=\"text-align: left; margin: 40px 0;\">" +
                "<img src=\"cid:signature\" alt=\"Signature\" style=\"width: 100px;\">" +
                "</div>" +
                "<p>Best regards,<br>The Proliferate Team</p>" +
                "<div style=\"text-align: center; margin-top: 20px;\"><img src=\"cid:proliferateLogo\" alt=\"Proliferate Logo\" style=\"width: 100px;\"></div>" +
                "<hr style=\"margin: 20px 0;\">" +
                "<p style=\"font-size: 12px; color: #999; text-align: center;\">This is an automated email. Please do not reply to this email. If you have any questions, visit our <a href=\"https://proliferate.ai/contact\" style=\"color: #4CAF50;\">Contact Us</a> page.</p>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
	
		public void sendAssignmentSubmissionEmail(
            String to, String studentFirstName, String studentLastName, String assignmentTitle, String className) throws EmailSendingException {

        String subject = "Assignment Submission Confirmation";
        String body = buildAssignmentSubmissionEmailBody(studentFirstName, studentLastName, assignmentTitle, className);

        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true); // Enable HTML content
            helper.setFrom(new InternetAddress("tolex20004real@gmail.com", "Proliferate Team"));
            helper.setReplyTo("noreply@yourdomain.com");
			
        // Add the logo as an inline resource
        Resource logoResource = new ClassPathResource("logo/proliferate-logo.png");
        helper.addInline("proliferateLogo", logoResource);
        if (logoResource.exists()) {
			System.out.println("Logo found!");
			} else {
		System.out.println("Logo not found!");}
		
        // Add the drawing as an inline resource
        Resource signatureResource = new ClassPathResource("logo/signature.jpg");
        helper.addInline("signature", signatureResource);
        if (signatureResource.exists()) {
			System.out.println("Signature found!");
			} else {
		System.out.println("Signature not found!");}

            javaMailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
            // Optionally log the exception or rethrow it as a custom exception
            throw new EmailSendingException("Failed to send email", e);
        }
    }

    private String buildAssignmentSubmissionEmailBody(String studentFirstName, String studentLastName, String assignmentTitle, String className) {
        return "<html>" +
                "<body style=\"font-family: Arial, sans-serif; line-height: 1.6; background-color: #f9f9f9; margin: 0; padding: 0;\">" +
                "<div style=\"max-width: 600px; margin: 20px auto; padding: 20px; background-color: #fff; border-radius: 10px; border: 1px solid #ddd; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);\">"  +
                "<h2 style=\"text-align: center; color: #333;\">Assignment Submission <br> Confirmation</h2>" +
                "<p>Dear " + studentLastName + " " + studentFirstName + ",</p>" +
				
                "<p>We have received your assignment submission for  " + assignmentTitle + " , in " + className +"</p>" +
				
				"<p> Our instructors will review your submission and provide feedback shortly </p>" +
				
				"<p>Thank you for your hard work and dedication.</p>" +

				     
                "<div style=\"text-align: left; margin: 40px 0;\">" +
                "<img src=\"cid:signature\" alt=\"Signature\" style=\"width: 100px;\">" +
                "</div>" +
                "<p>Best regards,<br>The Proliferate Team</p>" +
                "<div style=\"text-align: center; margin-top: 20px;\"><img src=\"cid:proliferateLogo\" alt=\"Proliferate Logo\" style=\"width: 100px;\"></div>" +
                "<hr style=\"margin: 20px 0;\">" +
                "<p style=\"font-size: 12px; color: #999; text-align: center;\">This is an automated email. Please do not reply to this email. If you have any questions, visit our <a href=\"https://proliferate.ai/contact\" style=\"color: #4CAF50;\">Contact Us</a> page.</p>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
	
		public void sendClassRescheduledNotificationEmail(
            String to, String studentFirstName, String studentLastName, String tutorFirstName, String tutorLastName, String reason, String oldDate, String newDate) throws EmailSendingException {

        String subject = "Class Rescheduled Notification";
        String body = buildClassRescheduleNotificationEmailBody(studentFirstName, studentLastName, tutorFirstName, tutorLastName, reason, oldDate, newDate);

        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true); // Enable HTML content
            helper.setFrom(new InternetAddress("tolex20004real@gmail.com", "Proliferate Team"));
            helper.setReplyTo("noreply@yourdomain.com");
			
        // Add the logo as an inline resource
        Resource logoResource = new ClassPathResource("logo/proliferate-logo.png");
        helper.addInline("proliferateLogo", logoResource);
        if (logoResource.exists()) {
			System.out.println("Logo found!");
			} else {
		System.out.println("Logo not found!");}
		
        // Add the drawing as an inline resource
        Resource signatureResource = new ClassPathResource("logo/signature.jpg");
        helper.addInline("signature", signatureResource);
        if (signatureResource.exists()) {
			System.out.println("Signature found!");
			} else {
		System.out.println("Signature not found!");}

            javaMailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
            // Optionally log the exception or rethrow it as a custom exception
            throw new EmailSendingException("Failed to send email", e);
        }
    }

    private String buildClassRescheduleNotificationEmailBody(String studentFirstName, String studentLastName, String tutorFirstName, String tutorLastName, String reason, String oldDate, String newDate) {
        return "<html>" +
                "<body style=\"font-family: Arial, sans-serif; line-height: 1.6; background-color: #f9f9f9; margin: 0; padding: 0;\">" +
                "<div style=\"max-width: 600px; margin: 20px auto; padding: 20px; background-color: #fff; border-radius: 10px; border: 1px solid #ddd; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);\">"  +
                "<h2 style=\"text-align: center; color: #333;\">Class Rescheduled Notification <br> Confirmation</h2>" +
                "<p>Dear " + tutorLastName + " " + tutorFirstName + ",</p>" +
				
                "<p>Session rescheduled: " + studentLastName + " " + studentFirstName + "has rescheduled the tutoring session with you from "  + oldDate + " to "  + newDate + ". </p>"+
				
				"<p>Reason: " + reason + "</p>" +
				
				"<p>Thank you for your understanding.</p>" +

                "<div style=\"text-align: left; margin: 40px 0;\">" +
                "<img src=\"cid:signature\" alt=\"Signature\" style=\"width: 100px;\">" +
                "</div>" +
                "<p>Best regards,<br>The Proliferate Team</p>" +
                "<div style=\"text-align: center; margin-top: 20px;\"><img src=\"cid:proliferateLogo\" alt=\"Proliferate Logo\" style=\"width: 100px;\"></div>" +
                "<hr style=\"margin: 20px 0;\">" +
                "<p style=\"font-size: 12px; color: #999; text-align: center;\">This is an automated email. Please do not reply to this email. If you have any questions, visit our <a href=\"https://proliferate.ai/contact\" style=\"color: #4CAF50;\">Contact Us</a> page.</p>" +
                "</div>" +
                "</body>" +
                "</html>";
    }

}


