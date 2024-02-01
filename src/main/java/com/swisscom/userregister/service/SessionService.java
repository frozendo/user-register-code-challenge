package com.swisscom.userregister.service;

import com.swisscom.userregister.domain.entity.Session;
import com.swisscom.userregister.repository.SessionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class SessionService {

    private final SessionRepository sessionRepository;

    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public String generateAndRegisterToken(String email) {
        var existToken = validateExistSession(email);
        if (existToken.isEmpty()) {
            return createAndSaveNewSession(email);
        }
        return existToken;
    }

    public Optional<Session> validateToken(String token) {
        return sessionRepository.findByToken(token)
                .filter(value ->
                        value.getExpiration().isAfter(LocalDateTime.now()));
    }

    private String createAndSaveNewSession(String email) {
        var token = generateOpaqueToken();
        var session = createSession(token, email);
        sessionRepository.save(session);
        return token;
    }

    private String validateExistSession(String email) {
        return sessionRepository.findByEmail(email)
                .filter(value -> value.getExpiration().isAfter(LocalDateTime.now()))
                .map(Session::getToken)
                .orElse("");
    }

    private static String generateOpaqueToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private Session createSession(String token, String email) {
        var updatedAt = LocalDateTime.now();
        var expirationAt = updatedAt.plusDays(1);
        return new Session(token, updatedAt, expirationAt, email);
    }
}
