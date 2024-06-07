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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
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

    @Column(name = "user_name", unique = true)
    @NotNull(message = "UserName cannot be empty")
    @NotBlank(message = "UserName cannot be Blank")
    private String userName;

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
	
	@ElementCollection
	@Column(name = "preferred_subjects")
	private List<String> preferredSubjects;
	
	@Column(name = "student_assessment_approach")
    private String studentAssessmentApproach;
	
	@Column(name = "availability_additional_support")
    private String availableForAdditionalSupport;
	
	@Column(name = "available_Date_Time")
    private List<String> availableDateTime;
	
	@Column(name = "educational_certificates")
	@Lob
    private byte[] educationalCertificates;
	
	@Column(name = "resume_curriculum_vitae")
    @Lob
    private byte[] resumeCurriculumVitae;
	
	@Column(name = "professional_development_cert")
    @Lob
    private byte[] professionalDevelopmentCert;

	@Column(name = "identification_documents")
    @Lob
    private byte[] identificationDocuments;

	@Column(name = "terms_and_conditions_approved")
    private boolean termsAndConditionsApproved;
	
	@Column(name = "registration_completed")
	private boolean registrationCompleted;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

	@Column(name = "student_image")
    @Lob
    private byte[] studentImage;
	
	@Column(name = "bio")
    private String bio;

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