package com.example.demo.security;

import com.example.demo.model.persistence.repositories.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.example.demo.model.persistence.User user = userRepository.findByUsername(username);
        if (null == user) {
            throw new UsernameNotFoundException(username);
        }

        return new User(user.getUsername(), user.getPassword(), Collections.emptyList());
    }
}
