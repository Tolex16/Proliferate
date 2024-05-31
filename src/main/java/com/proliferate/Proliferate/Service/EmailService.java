package com.proliferate.Proliferate.Service;

import com.proliferate.Proliferate.Domain.DTO.Gender;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class EmailService {

    @Autowired
    private final JavaMailSender javaMailSender;

  public void studentRegistrationConfirmationEmail(String to, String studentFirstName, String studentLastName, String email, Gender gender, String contactNumber, int age, String grade, String subjects, String availability, String additionalPreferences, String shortTermGoals, String longTermGoals) {
        String subject = "Welcome to Proliferate!";
        String body = buildEmailBody(studentFirstName, studentLastName, email, gender, contactNumber, age, grade, subjects, availability, additionalPreferences, shortTermGoals, longTermGoals);

        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true); // Enable HTML content
            helper.setFrom(new InternetAddress("tolex20004real@gmail.com", "Proliferate Team"));

            javaMailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
            // Optionally log the exception or rethrow it as a custom exception
        }
    }

    private String buildEmailBody(String studentFirstName, String studentLastName, String email, Gender gender, String contactNumber, int age, String grade, String subjects, String availability, String additionalPreferences, String shortTermGoals, String longTermGoals) {
        StringBuilder bodyBuilder = new StringBuilder();
        bodyBuilder.append("<html>");
        bodyBuilder.append("<body>");
        bodyBuilder.append("<h2>Dear ").append(studentFirstName).append(",</h2>");
        bodyBuilder.append("<p>Congratulations on successfully registering with Proliferate! We're excited to have you join our community of learners and tutors.</p>");
        bodyBuilder.append("<p>Here's a summary of the information you provided during registration:</p>");
        bodyBuilder.append("<h3>Personal Information:</h3>");
        bodyBuilder.append("<ul>");
        bodyBuilder.append("<li>Full Name: ").append(studentFirstName).append(" ").append(studentLastName);
        bodyBuilder.append("<li>Email Address: ").append(email).append("</li>");
        bodyBuilder.append("<li>Gender: ").append(gender).append("</li>");
        bodyBuilder.append("<li>Contact Number: ").append(contactNumber).append("</li>");
        bodyBuilder.append("<li>Age: ").append(age).append("</li>");
        bodyBuilder.append("</ul>");
        bodyBuilder.append("<h3>Academic Details:</h3>");
        bodyBuilder.append("<ul>");
        bodyBuilder.append("<li>Grade/Year: ").append(grade).append("</li>");
        bodyBuilder.append("<li>Subjects Needing Tutoring: ").append(subjects).append("</li>");
        bodyBuilder.append("</ul>");
        bodyBuilder.append("<h3>Tutoring Preferences:</h3>");
        bodyBuilder.append("<ul>");
        //bodyBuilder.append("<li>Preferred Tutoring Format: ").append(tutoringFormat).append("</li>");
        bodyBuilder.append("<li>Availability: ").append(availability).append("</li>");
        bodyBuilder.append("<li>Additional Preferences/Requirements: ").append(additionalPreferences).append("</li>");
        bodyBuilder.append("</ul>");
        bodyBuilder.append("<h3>Learning Goals:</h3>");
        bodyBuilder.append("<ul>");
        bodyBuilder.append("<li>Short-term Goals: ").append(shortTermGoals).append("</li>");
        bodyBuilder.append("<li>Long-term Goals: ").append(longTermGoals).append("</li>");
        bodyBuilder.append("</ul>");
        bodyBuilder.append("<p>Thank you for providing this information. It will help us match you with the most suitable tutors and tailor your learning experience to meet your needs and goals.</p>");
        bodyBuilder.append("<p>If you have any questions or need assistance, please don't hesitate to <a href=\"https://proliferate.ai/contact\">contact us</a>. Our team is here to help you every step of the way.</p>");
        bodyBuilder.append("<p>We are excited to see you grow and excel with Proliferate.</p>");
        bodyBuilder.append("<p>Welcome aboard, and happy learning!</p>");
        bodyBuilder.append("<p>Best regards,<br>The Proliferate Team</p>");
        bodyBuilder.append("<a href=\"http://example.com/dashboard\" th:href=\"@{http://example.com/dashboard}\">Access Your Dashboard</a>");
        bodyBuilder.append("</body>");
        bodyBuilder.append("</html>");

        return bodyBuilder.toString();
    }


    public void sendInvitationEmail(String to, String friendName, String senderName) {
        String subject = "Join Proliferate and Start Learning Together!";

        // Customize the email body with HTML formatting
        StringBuilder bodyBuilder = new StringBuilder();
        bodyBuilder.append("<html>");
        bodyBuilder.append("<body>");
        bodyBuilder.append("<h2>Hi ").append(friendName).append(",</h2>");
        bodyBuilder.append("<p>I hope this email finds you well! I wanted to reach out and share something exciting with you.</p>");
        bodyBuilder.append("<p>I've recently started using Proliferate, an amazing online learning platform that I think you'd love too!</p>");
        bodyBuilder.append("<p>Proliferate offers a wide range of courses and resources to help you enhance your skills and knowledge in various subjects.</p>");
        bodyBuilder.append("<p>From math, reading, and science to languages and arts, there's something for everyone to explore and learn.</p>");
        bodyBuilder.append("<p>As a member of Proliferate, you'll have access to:</p>");
        bodyBuilder.append("<ul>");
        bodyBuilder.append("<li>The best-in-class teachers, well-versed in their field</li>");
        bodyBuilder.append("<li>Flexible learning options to fit your schedule</li>");
        bodyBuilder.append("<li>An Online book club, Educational Games And much more!</li>");
        bodyBuilder.append("</ul>");
        bodyBuilder.append("<p>I have already found the platform incredibly valuable in my own learning journey, and I believe you'll benefit from it too.</p>");
        bodyBuilder.append("<p>To join Proliferate and start learning, simply click on the link below to create your account.</p>");
        // Add link here
        bodyBuilder.append("<p><a href=\"https://proliferate.ai\">Join Proliferate</a></p>");
        bodyBuilder.append("<p>I look forward to seeing you on Proliferate and exploring all the amazing learning opportunities together!</p>");
        bodyBuilder.append("<p>Best regards,</p>");
        bodyBuilder.append("<p>").append(senderName).append("</p>");
        bodyBuilder.append("<p>© 2024 proliferate.ai All rights reserved.</p>");
        bodyBuilder.append("<p>For any questions, please visit our <a href=\"https://proliferate.ai/contact\">contact us</a> page or call us at 1-289-952-2596.</p>");
        bodyBuilder.append("</body>");
        bodyBuilder.append("</html>");

        String body = bodyBuilder.toString();

        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true); // Enable HTML content
            helper.setFrom(new InternetAddress("tolex20004real@gmail.com", senderName));

            javaMailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            // Handle exception
            e.printStackTrace();
        }
    }
//

    public void sendPasswordMail(String to, String firstName, String token) {
        String subject = "Proliferate: Password Reset Notification";

        // Customize the email body with HTML formatting
        String body = "<html>"
                + "<body>"
                + "<h2>Hello " + firstName + ",</h2>"
                + "<p>We have received a request to reset your password.</p>"
                + "<p>Please use the following OTP to reset your password:</p>"
                + "<p><strong>" + token + "</strong></p>"
                + "<p>If you did not request this password reset, please ignore this email.</p>"
                + "<p>Best regards,<br>"
                + "The Proliferate Team</p>"
                + "</body>"
                + "</html>";

        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true); // Enable HTML content
            helper.setFrom(new InternetAddress("tolex20004real@gmail.com", "The Proliferate Team"));

            javaMailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            // Handle exception
            e.printStackTrace();
        }
    }

	public void tutorRegistrationConfirmationEmail(
            String to, String tutorFirstName, String tutorLastName, String email, Gender gender,
            String contactNumber, int age, String highestEducationLevelAttained, String majorFieldOfStudy,
            String teachingGuide, String currentSchool, String teachingStyle, String studentAssessmentApproach, String availableForAdditionalSupport,
            LocalDate availableDate, LocalTime availableTime , String attendanceType, List<String> preferredSubjects) {

        String subject = "Welcome to Proliferate!";
        String body = tutorEmailBody(tutorFirstName,tutorLastName, email, gender,
                contactNumber, age, highestEducationLevelAttained, majorFieldOfStudy,
                teachingGuide, currentSchool,teachingStyle, studentAssessmentApproach, availableForAdditionalSupport,
                availableDate, availableTime , attendanceType, preferredSubjects);

        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true); // Enable HTML content
            helper.setFrom(new InternetAddress("tolex20004real@gmail.com", "Proliferate Team"));

            javaMailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
            // Optionally log the exception or rethrow it as a custom exception
        }
    }

    private String tutorEmailBody(
            String tutorFirstName, String tutorLastName, String email, Gender gender,
            String contactNumber, int age, String highestEducationLevelAttained, String majorFieldOfStudy,
            String teachingGuide, String currentSchool, String teachingStyle, String studentAssessmentApproach, String availableForAdditionalSupport,
            LocalDate availableDate, LocalTime availableTime , String attendanceType, List<String> preferredSubjects) {

        StringBuilder bodyBuilder = new StringBuilder();
        bodyBuilder.append("<html>");
        bodyBuilder.append("<body>");
        bodyBuilder.append("<p>Dear ").append(tutorFirstName).append(",</p>");
        bodyBuilder.append("<p>Congratulations on successfully registering with Proliferate! We're excited to have you join our community of learners and tutors.</p>");
        bodyBuilder.append("<p>Here's a summary of the information you provided during registration:</p>");

        bodyBuilder.append("<p><strong>Personal Information:</strong></p>");
        bodyBuilder.append("<ul>");
        bodyBuilder.append("<li>Full Name: ").append(tutorFirstName).append(" ").append(tutorLastName);
        bodyBuilder.append("<li>Email Address: ").append(email).append("</li>");
        bodyBuilder.append("<li>Gender: ").append(gender).append("</li>");
        bodyBuilder.append("<li>Contact Number: ").append(contactNumber).append("</li>");
        bodyBuilder.append("<li>Age: ").append(age).append("</li>");
        bodyBuilder.append("</ul>");

        bodyBuilder.append("<p><strong>Education and Experience:</strong></p>");
        bodyBuilder.append("<ul>");
        bodyBuilder.append("<li>Higher Education Level Attained: ").append(highestEducationLevelAttained).append("</li>");
        bodyBuilder.append("<li>Major/Field of Study: ").append(majorFieldOfStudy).append("</li>");
        //bodyBuilder.append("<li>Years of Experience: ").append(subjects).append("</li>");
		bodyBuilder.append("<li>Teaching Guide: ").append(teachingGuide).append("</li>");
		bodyBuilder.append("<li>Current School: ").append(currentSchool).append("</li>");
		bodyBuilder.append("</ul>");

        bodyBuilder.append("<p><strong>Teaching Style and Approach:</strong></p>");
        bodyBuilder.append("<ul>");
        bodyBuilder.append("<li>Teaching Style: ").append(teachingStyle).append("</li>");
        bodyBuilder.append("<li>student Assessment Approach: ").append(studentAssessmentApproach).append("</li>");
        bodyBuilder.append("<li>Availability for Additional Approach: ").append(availableForAdditionalSupport).append("</li>");
        bodyBuilder.append("<li>Availability: ").append(availableDate).append(" " + availableTime).append("</li>");
        bodyBuilder.append("<li>Attendance Type: ").append(attendanceType).append("</li>");
        bodyBuilder.append("<li>Preferred Subjects: ").append(preferredSubjects).append("</li>");
        bodyBuilder.append("</ul>");

        bodyBuilder.append("<p>Thank you for providing this information. They will help us match you with students that match your expertise, teaching style and availability. If you have any questions or need assistance, please don't hesitate to <a href=\"https://proliferate.ai/contact\">contact us</a>. Our team is here to help you every step of the way.</p>");
        bodyBuilder.append("<p>We are confident that you will make a valuable contribution to our platform and positively impact the learning journey of our students. If you have any questions or need assistance, please feel free to reach out to us at [support@proliferate.ai/contact] Our team is here to support you every step of the way.</p>");
        bodyBuilder.append("<p>We are excited to support you as you help shape the futures of learners around the world. If you have any questions or need assistance, our support team is always here to help.</p>");
		 bodyBuilder.append("<p>Welcome aboard, and we're excited to start this journey together!.</p>");
		bodyBuilder.append("<p>Best regards,<br>The Proliferate Team</p>");
        bodyBuilder.append("<a href=\"http://example.com/dashboard\" th:href=\"@{http://example.com/dashboard}\">Setup Your Tutor Profile Now</a>");
        bodyBuilder.append("</body>");
        bodyBuilder.append("</html>");

        return bodyBuilder.toString();
    }
}


