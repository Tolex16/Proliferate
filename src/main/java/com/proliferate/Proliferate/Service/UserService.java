package com.proliferate.Proliferate.Service;


import org.springframework.security.core.userdetails.UserDetailsService;


public interface UserService {
    UserDetailsService userDetailsService();
    ;

//   ResponseEntity<UserDto>  updateUser(UserDto userDto, Long id);
//
//   ResponseEntity changePassword(ChangePasswordRequest newPassword, Long id);
}

