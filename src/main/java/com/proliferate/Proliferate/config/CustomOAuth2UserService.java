package com.proliferate.Proliferate.config;
import com.proliferate.Proliferate.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    
	@Autowired
    private final UserService userService;


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws org.springframework.security.oauth2.core.OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // Extract user attributes from OAuth2 response (e.g., Google or other providers)
        Map<String, Object> attributes = oAuth2User.getAttributes();
        
        // Extract email or username from OAuth2 attributes (depending on your OAuth2 provider, adjust the key as needed)
        String provider = userRequest.getClientRegistration().getRegistrationId();
        String email = extractEmailFromAttributes(attributes, provider);

        // Use UserDetailsService to check the database for the user details
        UserDetails userDetails;
        try {
            userDetails = userService.userDetailsService().loadUserByUsername(email);  // Load user from DB using UserDetailsService
        } catch (UsernameNotFoundException e) {
            // If the user doesn't exist in the DB, you can handle it (e.g., throw an exception or create the user)
            throw new UsernameNotFoundException("User not found in the database: " + email);
        }

        // Use the user’s role from the database to set authorities (e.g., ROLE_STUDENT, ROLE_TUTOR, ROLE_ADMIN)
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        // Return an OAuth2User with the same authorities as UserDetails
        return new DefaultOAuth2User(
            authorities, // Use the authorities from UserDetails
            attributes,  // OAuth2 user attributes
            "email"      // Key used to identify the user (use the appropriate key for your provider)
        );
		
		        // Return a user with appropriate authorities
        //return new CustomOAuth2User(userDetails, attributes);
    }
	
	// Helper method to extract email based on OAuth2 provider
    private String extractEmailFromAttributes(Map<String, Object> attributes, String provider) {
        return switch (provider) {
            case "google", "facebook" -> (String) attributes.get("email");// Adjust as needed
            case "linkedin" -> (String) attributes.get("emailAddress");
            default -> throw new IllegalArgumentException("Unsupported provider: " + provider);
        };
    }
}
