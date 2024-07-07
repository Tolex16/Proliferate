package com.proliferate.Proliferate.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenService {
    private final Map<String, Instant> tokenMap = new ConcurrentHashMap<>();

    public void addToken(String token, Instant expiryTime) {
        tokenMap.put(token, expiryTime);
    }

    public boolean isTokenBlacklisted(String token) {
        return tokenMap.containsKey(token);
    }

    public void removeToken(String token) {
        tokenMap.remove(token);
    }

    public Set<String> getTokens() {
        return tokenMap.keySet();
    }

    public Map<String, Instant> getTokenMap() {
        return tokenMap;
    }

    // Scheduled method to clean up expired tokens
    @Scheduled(fixedRate = 60000) // Run every minute
    public void cleanUpExpiredTokens() {
        Instant now = Instant.now();
        Iterator<Map.Entry<String, Instant>> iterator = tokenMap.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, Instant> entry = iterator.next();
            if (entry.getValue().isBefore(now)) {
                iterator.remove();
            }
        }
    }
}

