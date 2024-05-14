package com.proliferate.Proliferate.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TokenService {
    private Map<String, String > usernameToTokenMap = new HashMap<>();


    public void storeToken(String userName, String token){
        usernameToTokenMap.put(userName,token);
    }

    public String getTokenByUsername(String username){
        return usernameToTokenMap.get(username);
    }
}
