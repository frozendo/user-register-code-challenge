package com.swisscom.userregister.service;

import com.swisscom.userregister.domain.entity.User;
import com.swisscom.userregister.domain.exceptions.BusinessException;
import com.swisscom.userregister.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> listUsers() {
        return userRepository.findAll();
    }

    public User create(User user) {
        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            dataIntegrityException(user, ex);
            throw ex;
        }
    }

    private void dataIntegrityException(User user, DataIntegrityViolationException ex) {
        var message = ex.getMessage();
        if (message != null && message.contains("uk_user_email")) {
            var businessMessage = "Email %s already exist!".formatted(user.getEmail());
            throw new BusinessException(businessMessage);
        }
    }
}
