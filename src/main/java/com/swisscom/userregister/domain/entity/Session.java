package com.swisscom.userregister.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "session")
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "token")
    private String token;

    @NotNull
    @Column(name = "updated_at")
    private LocalDateTime updated;

    @NotNull
    @Column(name = "expiration_at")
    private LocalDateTime expiration;

    @NotNull
    @Column(name = "email")
    private String email;

    public Session() {
    }

    public Session(String token, LocalDateTime updated, LocalDateTime expiration, String email) {
        this.token = token;
        this.updated = updated;
        this.expiration = expiration;
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public LocalDateTime getUpdated() {
        return updated;
    }

    public LocalDateTime getExpiration() {
        return expiration;
    }

    public String getEmail() {
        return email;
    }
}
