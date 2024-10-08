package com.proliferate.Proliferate.Service.ServiceImpl;


import com.proliferate.Proliferate.Domain.Mappers.Mapper;
import com.proliferate.Proliferate.Repository.AdminRepository;
import com.proliferate.Proliferate.Repository.StudentRepository;
import com.proliferate.Proliferate.Repository.TutorRepository;
import com.proliferate.Proliferate.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class  UserServiceImpl implements UserService {

    private final StudentRepository studentRepository;

    private final AdminRepository adminRepository;
    private final TutorRepository tutorRepository;


@Override
public UserDetailsService userDetailsService() {
    return new UserDetailsService() {
        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            // Try to find the user as a student
            var studentOpt = studentRepository.findByUserNameIgnoreCase(username);
            if (studentOpt.isPresent()) {
                return studentOpt.get();
            }

            // If not found as a student, try to find the user as a tutor
            var tutorOpt = tutorRepository.findByEmailIgnoreCase(username);
            if (tutorOpt.isPresent()) {
                return tutorOpt.get();
            }

            // If not found as a student, try to find the user as a tutor
            var adminOpt = adminRepository.findByEmailIgnoreCase(username);
            if (adminOpt.isPresent()) {
                return adminOpt.get();
            }

            // If neither student nor tutor is found, throw an exception
            throw new UsernameNotFoundException("User not found: " + username);
        }
    };
}

}
