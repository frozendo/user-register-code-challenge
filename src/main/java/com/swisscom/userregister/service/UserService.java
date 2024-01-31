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
    private final OpaServerService opaServerService;

    public UserService(UserRepository userRepository, OpaServerService opaServerService) {
        this.userRepository = userRepository;
        this.opaServerService = opaServerService;
    }

    public List<User> listUsers() {
        return userRepository.findAll();
    }

    public User createAndSendToOpa(User user) {
        try {
            var savedUser = userRepository.save(user);
            synchronizeUsersToOpa();
            return savedUser;
        } catch (DataIntegrityViolationException ex) {
            dataIntegrityException(user, ex);
            throw ex;
        }
    }

    private void synchronizeUsersToOpa() {
        var users = listUsers();
        opaServerService.synchronizedUserToOpa(users);
    }

    private void dataIntegrityException(User user, DataIntegrityViolationException ex) {
        var message = ex.getMessage();
        if (message != null && message.contains("uk_user_email")) {
            var businessMessage = "Email %s already exist!".formatted(user.getEmail());
            throw new BusinessException(businessMessage);
        }
    }
}
