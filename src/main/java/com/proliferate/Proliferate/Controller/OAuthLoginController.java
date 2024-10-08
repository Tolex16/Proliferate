package com.proliferate.Proliferate.Controller;

import com.proliferate.Proliferate.Domain.DTO.Oauth2Request;
import com.proliferate.Proliferate.Response.LoginResponse;
import com.proliferate.Proliferate.Service.OAuthLoginService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
@RequestMapping("/api/v1/oauth")
@RequiredArgsConstructor
public class OAuthLoginController {

    
    private final OAuthLoginService oAuthLoginService;

    @PostMapping("/login-google")
    public ResponseEntity<?> loginWithGoogle(@Valid @RequestBody Oauth2Request oauth2Request) throws GeneralSecurityException, IOException {
        // Simplified check for a valid token using Apache Commons StringUtils
        if (StringUtils.isBlank(oauth2Request.getIdToken())) {
            throw new BadRequestException("Id token is required");
        }

        // Delegate login logic to the service
        LoginResponse response = oAuthLoginService.loginWithGoogle(oauth2Request);

        // Return response using ResponseEntity
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }


}
