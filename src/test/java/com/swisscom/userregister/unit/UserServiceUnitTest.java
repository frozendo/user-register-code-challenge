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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
        this.userService = new UserService(userRepository, opaServerService);
    }

    @Test
    void testCreateUser() {
        var userEve = new User("Eve", "eve@email.com", RoleEnum.ADMIN);
        var userAlice = new User("Alice", "alice@email.com", RoleEnum.ADMIN);
        var userBob = new User("Bob", "bob@email.com", RoleEnum.COMMON);

        var users = List.of(userAlice, userBob, userEve);

        when(userRepository.findAll()).thenReturn(users);

        userService.createAndSendToOpa(userEve);

        verify(userRepository, times(1)).save(userEve);
        verify(opaServerService, times(1)).synchronizeUsersToOpa(users);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testCreateUserWhenEmailExist() {
        var user = new User("test", "duplicate@email.com", RoleEnum.ADMIN);

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
