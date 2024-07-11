package com.proliferate.Proliferate.Domain.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.proliferate.Proliferate.Domain.DTO.Gender;
import com.proliferate.Proliferate.Domain.Entities.Role;
import com.proliferate.Proliferate.config.StrongPassword;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.annotations.Type;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name="proliferate_tutors")
@NoArgsConstructor
public class TutorEntity implements UserDetails {

    @Id
    @Column(name = "tutor_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long tutorId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email", unique = true)
    @NotNull(message = "email can't be null")
    @NotBlank(message = " email cannot be blank")
    @Email(message = "Input a real email address")
    private String email;

    @Column(name = "contact_number")
    @NotNull(message = "contact number can't be null")
    @NotBlank(message = "contact number cannot be blank")
    private String contactNumber;

    @Column(name = "password")
    @NotNull(message = "Password can't be null")
    @NotBlank(message = " Password cannot be blank")
    @StrongPassword
    @JsonIgnore
    private String password;

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "age")
    private int age;
	
	@Column(name = "highest_education_level_attained")
	private String highestEducationLevelAttained;

    @Column(name = "major_field_of_study")
    private String majorFieldOfStudy;

	@Column(name = "teaching_grade")
    private String teachingGrade;

    @Column(name = "years_of_teaching_experience")
    private String yearsOfTeachingExperience;
	
	@Column(name = "current_school")
    private String currentSchool;
	
	@Column(name = "location")
    private String location;
	
	@Column(name = "teaching_style")
    private String teachingStyle;
	
	@Column(name = "approach_to_tutoring")
    private String approachToTutoring;
	
	@Column(name = "attendance_type")
    private String attendanceType;

	@Column(name = "preferred_subjects")
	@ElementCollection
	private List<String> preferredSubjects;

    @Column(name = "weekly_availability")
    private String weeklyAvailability;

    @Column(name = "timeslot_availability")
    private String timeslotAvailability;

    @Column(name = "selected_timezone")
    private String selectTimezone;

    @Column(name = "communication_language")
    private String communicationLanguage;

    @Lob
	@Basic(fetch = FetchType.LAZY)
	@Column(name = "educational_certificates")
    private byte[] educationalCertificates;

    @Lob
	@Basic(fetch = FetchType.LAZY)
	@Column(name = "resume_curriculum_vitae")
    private byte[] resumeCurriculumVitae;

    @Lob
	@Basic(fetch = FetchType.LAZY)
	@Column(name = "professional_development_cert")
    private byte[] professionalDevelopmentCert;

    @Lob
	@Basic(fetch = FetchType.LAZY)
	@Column(name = "identification_documents")
    private byte[] identificationDocuments;

	@Column(name = "terms_and_conditions_approved")
    private boolean termsAndConditionsApproved;
	
	@Column(name = "registration_completed")
	private boolean registrationCompleted;

    @Column(name = "email_verified")
    private boolean emailVerified;

    @JsonIgnore
    @Column(name = "verification_token",length = 6)
    private String verificationToken;

    @JsonIgnore
    @Column(name = "token_expiration_time")
    private LocalDateTime tokenExpirationTime;
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    @Lob
	@Basic(fetch = FetchType.LAZY)
	@Column(name = "tutor_image")
    private byte[] tutorImage;
	
	@Column(name = "bio")
    private String bio;
	
	@OneToMany(mappedBy = "tutor")
    private List<Subject> subjects;

    @OneToMany(mappedBy = "tutor")
    private List<ClassSchedule> classSchedules;

    @OneToMany(mappedBy = "tutor")
    private List<Feedback> feedbacks;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}