package com.proliferate.Proliferate.Service;

import com.proliferate.Proliferate.Domain.DTO.Oauth2Request;
import com.proliferate.Proliferate.Response.LoginResponse;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface OAuthLoginService {
    LoginResponse loginWithGoogle(Oauth2Request request) throws GeneralSecurityException, IOException;
}
