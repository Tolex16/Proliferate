package com.proliferate.Proliferate.Service;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    String extractUsername(String token);

    boolean isTokenValid(String token, UserDetails userDetails);

    String genToken(UserDetails userDetails);

    Long getUserId();
}
