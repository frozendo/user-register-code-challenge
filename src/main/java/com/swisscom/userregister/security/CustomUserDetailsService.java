package com.swisscom.userregister.security;

import com.swisscom.userregister.repository.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var optionalUser = userRepository.findByEmail(username);
        if (optionalUser.isPresent()) {
            var entity = optionalUser.get();
            return User.builder()
                    .username(entity.getEmail())
                    .password(entity.getPassword())
                    .roles(entity.getRoleName())
                    .build();
        }
        throw new UsernameNotFoundException("User not found on database!");
    }

}
