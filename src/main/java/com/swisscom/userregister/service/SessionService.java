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
    private final OpaServerService opaServerService;

    public SessionService(SessionRepository sessionRepository, OpaServerService opaServerService) {
        this.sessionRepository = sessionRepository;
        this.opaServerService = opaServerService;
    }

    public String generateAndRegisterToken(String email) {
        var existToken = validateExistSession(email);
        if (existToken.isEmpty()) {
            return createAndSaveNewSession(email);
        }
        return existToken;
    }

    private String validateExistSession(String email) {
        return sessionRepository.findByEmail(email)
                .filter(value -> value.getExpiration().isAfter(LocalDateTime.now()))
                .map(Session::getToken)
                .orElse("");
    }

    private String createAndSaveNewSession(String email) {
        var token = generateOpaqueToken();
        var session = createSession(token, email);
        sessionRepository.save(session);
        opaServerService.synchronizeTokenToOpa(token, email);
        return token;
    }

    private String generateOpaqueToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private Session createSession(String token, String email) {
        var updatedAt = LocalDateTime.now();
        var expirationAt = updatedAt.plusDays(1);
        return new Session(token, updatedAt, expirationAt, email);
    }
}
