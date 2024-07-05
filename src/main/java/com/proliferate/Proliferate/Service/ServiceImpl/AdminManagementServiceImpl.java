package com.proliferate.Proliferate.Service.ServiceImpl;

import com.proliferate.Proliferate.Domain.DTO.Tutor.LoginTutorRequest;
import com.proliferate.Proliferate.Domain.DTO.Tutor.TutorDto;
import com.proliferate.Proliferate.Domain.Entities.AdminEntity;
import com.proliferate.Proliferate.Domain.Entities.Role;
import com.proliferate.Proliferate.Repository.AdminRepository;
import com.proliferate.Proliferate.Response.LoginResponse;
import com.proliferate.Proliferate.Service.JwtService;
import com.proliferate.Proliferate.Service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminManagementServiceImpl {

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private final UserService userService;
    @Autowired
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final AdminRepository adminRepository;

    @PostConstruct
    public void createAdminUsers() {
        Optional<AdminEntity> adminUser = adminRepository.findByEmail("techproliferate@gmail.com");
        if (null == adminUser ){
            AdminEntity createAdmin = new AdminEntity();
            createAdmin.setFirstName("tech");
            createAdmin.setLastName("proliferate");
            createAdmin.setEmail("techproliferate@gmail.com");
            createAdmin.setPassword(passwordEncoder.encode("Winner123!"));
            createAdmin.setRole(Role.ADMIN);
            adminRepository.save(createAdmin);
        }

        Optional<AdminEntity> adminUser2 = adminRepository.findByEmail("oseremio@gmail.com");
        if (null == adminUser2 ){
            AdminEntity createAdmin = new AdminEntity();
            createAdmin.setFirstName("tech");
            createAdmin.setLastName("oseremio");
            createAdmin.setEmail("oseremio@gmail.com");
            createAdmin.setPassword(passwordEncoder.encode("Winner123!"));
            createAdmin.setRole(Role.ADMIN);
            adminRepository.save(createAdmin);
        }
    }

    public LoginResponse login(LoginTutorRequest loginTutorRequest)
    {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginTutorRequest.getEmail(), loginTutorRequest.getPassword()));
        } catch (BadCredentialsException e){
            throw new IllegalArgumentException("Invalid email or Password", e);
        }
        // Try to find the user as a admin

        var adminOpt = adminRepository.findByEmail(loginTutorRequest.getEmail());

        if (adminOpt.isPresent()) {
            var admin = adminOpt.get();
                UserDetails userDetails = userService.userDetailsService().loadUserByUsername(admin.getEmail());
                var jwt = jwtService.genToken(userDetails, admin);
                return new LoginResponse(null, null, jwt);
            }

        // If neither student nor tutor is found, throw an exception
        throw new IllegalArgumentException("Error in email and password");
    }
}
