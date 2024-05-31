package com.proliferate.Proliferate.config;

import com.proliferate.Proliferate.ExeceptionHandler.UserNotFoundException;
import com.proliferate.Proliferate.Service.JwtService;
import com.proliferate.Proliferate.Service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CustomLogOutHandler implements LogoutHandler {

    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {

            return;
        }
        //Long userId = jwtService.getUserId();

        String jwt = authHeader.substring(7);

        //UserEntity user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
//        get stored token from database


        //var token =  jwtService.genToken(user);;

        if (jwt != null){
            SecurityContextHolder.clearContext();
        }
}

    }

