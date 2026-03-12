package com.kaizen.gym_api.security;

import com.kaizen.gym_api.model.User;
import com.kaizen.gym_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        // Try to find the user by email first, then by username
        User user = userRepository.findByEmail(usernameOrEmail)
                .orElseGet(() -> userRepository.findByUsername(usernameOrEmail)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found with email or username: " + usernameOrEmail)));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), // We use email as the principal "username" in Spring Security context
                user.getPasswordHash(),
                new ArrayList<>() // Empty authorities list as roles aren't fully implemented yet
        );
    }
}
