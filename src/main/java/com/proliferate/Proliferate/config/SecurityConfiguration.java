package com.proliferate.Proliferate.config;


import com.proliferate.Proliferate.Domain.Entities.Role;
import com.proliferate.Proliferate.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration   {

    private final JWTAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private final UserService userService;

    private final CustomLogOutHandler logoutHandler;
    private final PasswordEncoderConfig passwordEncoderConfig;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request -> request.requestMatchers("/api/v1/auth/studentPersonalDetails","/api/v1/authorize/tutorPersonalDetails","/api/v1/auth/terms-and-conditions","/api/v1/auth/login","/api/v1/forgot-password/**","/api/v1/change-password","/api/v1/authorize/login-tutor","/api/v1/auth/logout")
                        .permitAll()
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers("/api/v1/student/**","/api/v1/auth/academicDetail", "/api/v1/auth/preferences","/api/v1/auth/learningGoals","/api/v1/auth/student-completeRegistration","/api/v1/friend-invite").hasAnyAuthority(Role.STUDENT.name())
                        .requestMatchers("/api/v1/tutor/**", "/api/v1/authorize/educationExperience","/api/v1/authorize/teachingStyleApproach","/api/v1/authorize/availabilityPreference", "/api/v1/authorize/upload-documents","/api/v1/authorize/tutorCompleteRegistration").hasAnyAuthority(Role.TUTOR.name())
                        .anyRequest().authenticated())
                .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(
                        jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class
                )
                .exceptionHandling(
                        e->e.accessDeniedHandler(
                                        (request, response, accessDeniedException)->response.setStatus(403)
                                )
                                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .logout(logout -> logout
                        .logoutUrl("/api/v1/auth/t")
                        .addLogoutHandler(logoutHandler)
                        .logoutSuccessHandler((request, response, authentication) -> {
                            SecurityContextHolder.clearContext();
                            // Add logging for successful logout
                            System.out.println("Logout successful");
                        }).permitAll()
                        .logoutUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                );

        return http.build();
    }

    @Bean
    AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userService.userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoderConfig.passwordEncoder());
        return authenticationProvider;
    }



    @Bean
    AuthenticationManager authenticationManager (AuthenticationConfiguration config) throws Exception{
        return config.getAuthenticationManager();
    }
}