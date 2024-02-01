package com.swisscom.userregister.domain.entity;

import com.swisscom.userregister.domain.convert.AuthorizationResultConvert;
import com.swisscom.userregister.domain.enums.AuthorizationResultEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

@Entity
@Table(name = "authorization_log")
public class AuthorizationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "decision_id")
    private String decisionId;

    @NotNull
    @Column(name = "path")
    private String path;

    @NotNull
    @Column(name = "actions")
    private String action;

    @NotNull
    @Column(name = "email")
    private String email;

    @NotNull
    @Convert(converter = AuthorizationResultConvert.class)
    @Column(name = "result")
    private AuthorizationResultEnum role;


    public AuthorizationLog() {}

    public AuthorizationLog(String decisionId,
                            String path,
                            String action,
                            String email,
                            AuthorizationResultEnum role) {
        this.decisionId = decisionId;
        this.path = path;
        this.action = action;
        this.email = email;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public String getDecisionId() {
        return decisionId;
    }

    public String getPath() {
        return path;
    }

    public String getAction() {
        return action;
    }

    public String getEmail() {
        return email;
    }

    public AuthorizationResultEnum getRole() {
        return role;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        AuthorizationLog that = (AuthorizationLog) object;
        return Objects.equals(id, that.id) && Objects.equals(decisionId, that.decisionId) && Objects.equals(path, that.path) && Objects.equals(action, that.action) && Objects.equals(email, that.email) && role == that.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, decisionId, path, action, email, role);
    }

    @Override
    public String toString() {
        return "AuthorizationLog{" +
                "id=" + id +
                ", decisionId='" + decisionId + '\'' +
                ", path='" + path + '\'' +
                ", action='" + action + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                '}';
    }
}
