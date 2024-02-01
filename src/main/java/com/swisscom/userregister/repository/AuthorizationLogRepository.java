package com.swisscom.userregister.repository;

import com.swisscom.userregister.domain.entity.AuthorizationLog;
import org.springframework.data.repository.CrudRepository;

public interface AuthorizationLogRepository extends CrudRepository<AuthorizationLog, Long> {
}
