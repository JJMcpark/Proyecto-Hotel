package com.proyecto.hotel.auth.service;

import com.proyecto.hotel.auth.exception.TooManyLoginAttemptsException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {

    private final int MAX_ATTEMPTS = 5;
    private final Duration LOCK_DURATION = Duration.ofMinutes(15);

    private static class AttemptInfo {
        int attempts;
        Instant lockUntil;
    }

    private final Map<String, AttemptInfo> attempts = new ConcurrentHashMap<>();

    public void validateLoginAllowed(String numDocumento) {
        AttemptInfo info = attempts.get(numDocumento);
        if (info == null) return;

        if (info.lockUntil != null && Instant.now().isBefore(info.lockUntil)) {
            throw new TooManyLoginAttemptsException("Cuenta bloqueada temporalmente. Intenta más tarde.");
        }

        if (info.attempts >= MAX_ATTEMPTS) {
            info.lockUntil = Instant.now().plus(LOCK_DURATION);
            throw new TooManyLoginAttemptsException("Cuenta bloqueada temporalmente. Intenta más tarde.");
        }
    }

    public void loginSucceeded(String numDocumento) {
        attempts.remove(numDocumento);
    }

    public void loginFailed(String numDocumento) {
        AttemptInfo info = attempts.computeIfAbsent(numDocumento, k -> new AttemptInfo());
        info.attempts++;
        if (info.attempts >= MAX_ATTEMPTS) {
            info.lockUntil = Instant.now().plus(LOCK_DURATION);
        }
    }
}
