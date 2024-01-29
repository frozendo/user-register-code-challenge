package com.swisscom.userregister.unit;

import com.swisscom.userregister.domain.entity.User;
import com.swisscom.userregister.domain.exceptions.BusinessException;
import com.swisscom.userregister.repository.UserRepository;
import com.swisscom.userregister.service.UserService;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
class UserServiceUnitTest {

    private final UserRepository userRepository;
    private final UserService userService;

    public UserServiceUnitTest() {
        this.userRepository = mock(UserRepository.class);
        this.userService = new UserService(userRepository);
    }

    @Test
    void testCreateUser() {
        var user = new User("test", "test@email.com", "ADMIN");
        userService.create(user);

        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testCreateUserWhenEmailExist() {
        var user = new User("test", "duplicate@email.com", "ADMIN");

        var dataException = new DataIntegrityViolationException("uk_user_email");

        when(userRepository.save(user)).thenThrow(dataException);

        var exception = assertThrows(BusinessException.class,
                () -> userService.create(user));

        var expectedMessage = "Email %s already exist!".formatted(user.getEmail());

        assertNotNull(exception);
        assertNotNull(exception.getMessage());
        assertEquals(exception.getMessage(), expectedMessage);

        verify(userRepository, times(1)).save(user);

    }

}
