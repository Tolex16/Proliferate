package com.proliferate.Proliferate.Domain.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.proliferate.Proliferate.Domain.DTO.Gender;
import com.proliferate.Proliferate.Domain.DTO.PreferredTime;
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

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name="proliferate_student")
@NoArgsConstructor
public class StudentEntity implements UserDetails {

    @Id
    @Column(name = "student_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long studentId;

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

    @Column(name = "grade_year")
    private String gradeYear;

    @Column(name = "subjects_needing_tutoring")
    private String subjectsNeedingTutoring;

    @Column(name = "attendance_type")
    private String attendanceType;
	
	@Column(name = "preferred_time")
    @Enumerated(EnumType.STRING)
    private PreferredTime preferredTime;

    @Column(name = "current_location")
    private String currentLocation;

    @Column(name = "availability")
    private String availability;

    @Column(name = "additional_preferences")
    private String additionalPreferences;

    @Column(name = "requirements")
    private String requirements;

    @Column(name = "communication_language")
    private String communicationLanguage;

    @Column(name = "short_term_goals")
    private String shortTermGoals;

    @Column(name = "long_term_goals")
    private String longTermGoals;
	
	@Column(name = "terms_and_conditions_approved")
    private boolean termsAndConditionsApproved;

	@Column(name = "registration_completed")
	private boolean registrationCompleted;

    @Column(name = "email_verified")
    private boolean emailVerified;

    @Column(name = "verification_token",length = 6)
    private String verificationToken;

    @Column(name = "token_expiration_time")
    private LocalDateTime tokenExpirationTime;
	
//	@Column(name = "is_2fa_enabled")
//    private boolean is2FAEnabled;
//
//    @Column(name = "2fa_code", length = 6)
//    private String twoFactorCode;
//
//    @Column(name = "2fa_code_expiry")
//    private LocalDateTime twoFactorCodeExpiry;

    @Column(name = "friend_name")
    private String friendName;

    @Column(name = "friend_email")
    @Email
    private String friendEmail;
	
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "student_image")
    private byte[] studentImage;

    @Column(name = "bio", length = 2500)
    private String bio;

	@OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Score> scores;

	@OneToMany(mappedBy = "student")
    @JsonManagedReference
    private List<ClassSchedule> classSchedules;

	@OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Payment> payments;
	
	@OneToMany(mappedBy = "student")
	@JsonManagedReference
    private List<Notifications> notifications;

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
        return userName;
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