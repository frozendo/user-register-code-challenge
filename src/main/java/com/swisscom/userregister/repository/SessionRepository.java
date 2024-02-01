package com.swisscom.userregister.repository;

import com.swisscom.userregister.domain.entity.Session;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SessionRepository extends CrudRepository<Session, Long> {
    Optional<Session> findByEmail(String email);

    Optional<Session> findByToken(String token);
}
