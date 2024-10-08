package com.proliferate.Proliferate.config;
import com.proliferate.Proliferate.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomOAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private final UserService userService;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        // OAuth2 authentication token
        OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;

        // Extract email from OAuth2 token (adjust if needed based on your provider)
        String email = oauth2Token.getPrincipal().getAttribute("email");

        // Load user details from the database using the UserDetailsService
        UserDetails userDetails = userService.userDetailsService().loadUserByUsername(email);

        // Manually set the authenticated user with the correct roles in the security context
        Authentication authWithAuthorities = new OAuth2AuthenticationToken(
                oauth2Token.getPrincipal(),
                userDetails.getAuthorities(),
                oauth2Token.getAuthorizedClientRegistrationId()
        );

        // Set the authentication in the security context to ensure the user has correct roles in the session
        SecurityContextHolder.getContext().setAuthentication(authWithAuthorities);

        // Redirect user to the default success URL (you can customize this as needed)
        response.sendRedirect("/api/v1/**");
    }
}
