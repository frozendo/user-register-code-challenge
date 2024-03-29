package com.swisscom.userregister.unit;

import com.swisscom.userregister.domain.entity.User;
import com.swisscom.userregister.domain.enums.RoleEnum;
import com.swisscom.userregister.domain.exceptions.BusinessException;
import com.swisscom.userregister.repository.UserRepository;
import com.swisscom.userregister.service.OpaServerService;
import com.swisscom.userregister.service.UserService;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@Tag("unit")
class UserServiceUnitTest {

    private final UserRepository userRepository;
    private final OpaServerService opaServerService;
    private final UserService userService;

    public UserServiceUnitTest() {
        this.userRepository = mock(UserRepository.class);
        this.opaServerService = mock(OpaServerService.class);
        var passwordEncoder = mock(BCryptPasswordEncoder.class);
        this.userService = new UserService(userRepository, opaServerService, passwordEncoder);
    }

    @Test
    void testCreateUser() {
        var userEve = new User("Eve", "eve@email.com", "456789", RoleEnum.ADMIN);

        when(userRepository.save(any(User.class))).thenReturn(userEve);

        userService.createAndSendToOpa(userEve);

        verify(userRepository, times(1)).save(userEve);
        verify(opaServerService, times(1)).synchronizeUsersToOpa(any(User.class));
    }

    @Test
    void testCreateUserWhenEmailExist() {
        var user = new User("test", "duplicate@email.com", "456789", RoleEnum.ADMIN);

        var dataException = new DataIntegrityViolationException("uk_user_email");

        when(userRepository.save(user)).thenThrow(dataException);

        var exception = assertThrows(BusinessException.class,
                () -> userService.createAndSendToOpa(user));

        var expectedMessage = "Email %s already exist!".formatted(user.getEmail());

        assertNotNull(exception);
        assertNotNull(exception.getMessage());
        assertEquals(expectedMessage, exception.getMessage());

        verify(userRepository, times(1)).save(user);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(opaServerService);

    }

}
