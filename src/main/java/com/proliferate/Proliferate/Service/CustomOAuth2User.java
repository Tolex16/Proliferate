package com.proliferate.Proliferate.Service;

import com.proliferate.Proliferate.Domain.Entities.StudentEntity;
import com.proliferate.Proliferate.Domain.Entities.TutorEntity;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@Data
public class CustomOAuth2User implements OAuth2User, UserDetails {

    private StudentEntity student;
    private TutorEntity tutor;
    private Map<String, Object> attributes;

    public CustomOAuth2User(UserDetails userDetails, Map<String, Object> attributes) {
        // Dynamically determine if the user is a student or tutor
        if (userDetails instanceof StudentEntity) {
            this.student = (StudentEntity) userDetails;
        } else if (userDetails instanceof TutorEntity) {
            this.tutor = (TutorEntity) userDetails;
        }
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return student != null ? student.getFirstName() : tutor.getFirstName();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return student != null ? student.getAuthorities() : tutor.getAuthorities();
    }

    @Override
    public String getPassword() {
        return student != null ? student.getPassword() : tutor.getPassword();
    }

    @Override
    public String getUsername() {
        return student != null ? student.getUsername() : tutor.getUsername();
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
        return student != null ? student.isEmailVerified() : tutor.isEmailVerified();
    }
}

