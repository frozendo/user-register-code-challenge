package com.swisscom.userregister.service;

import com.swisscom.userregister.domain.entity.User;
import com.swisscom.userregister.domain.exceptions.BusinessException;
import com.swisscom.userregister.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final OpaServerService opaServerService;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       OpaServerService opaServerService,
                       BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.opaServerService = opaServerService;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> listUsers() {
        logger.info("List all users from database");
        return userRepository.findAll();
    }

    public User createAndSendToOpa(User user) {
        try {
            logger.info("Save user with email {} on database", user.getEmail());
            user.encryptPassword(passwordEncoder);
            var savedUser = userRepository.save(user);

            logger.info("User saved, try to synchronize to OPA server");
            synchronizeUsersToOpa();

            logger.info("User {} synchronized to OPA server!", user.getEmail());
            return savedUser;
        } catch (DataIntegrityViolationException ex) {
            dataIntegrityException(user, ex);
            throw ex;
        }
    }

    private void synchronizeUsersToOpa() {
        var users = listUsers();
        opaServerService.synchronizeUsersToOpa(users);
    }

    private void dataIntegrityException(User user, DataIntegrityViolationException ex) {
        var message = ex.getMessage();
        if (message != null && message.contains("uk_user_email")) {
            logger.error("Email {} already exist on database", user.getEmail());
            var businessMessage = "Email %s already exist!".formatted(user.getEmail());
            throw new BusinessException(businessMessage);
        }
        logger.error("Error when try to save an user");
    }
}
