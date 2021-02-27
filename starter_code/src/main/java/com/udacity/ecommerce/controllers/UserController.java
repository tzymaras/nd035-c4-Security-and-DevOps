package com.udacity.ecommerce.controllers;

import com.udacity.ecommerce.model.persistence.*;
import com.udacity.ecommerce.model.persistence.repositories.*;
import com.udacity.ecommerce.model.requests.CreateUserRequest;
import org.slf4j.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final Logger logger = LoggerFactory.getLogger("splunk.logger");

    public UserController(
        UserRepository userRepository,
        CartRepository cartRepository,
        BCryptPasswordEncoder bCryptPasswordEncoder
    ) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        if (!user.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.of(user);
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> findByUserName(@PathVariable String username) {
        User user = userRepository.findByUsername(username);
        return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
    }

    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
        Cart cart = new Cart();
        cartRepository.save(cart);

        User user = new User();
        user.setUsername(createUserRequest.getUsername());
        user.setCart(cart);

        if (createUserRequest.getPassword().length() < 7 ||
            !createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())
        ) {
            this.logger.info("createUser invalid password");
            return ResponseEntity.badRequest().build();
        }

        user.setPassword(this.bCryptPasswordEncoder.encode(createUserRequest.getPassword()));
        userRepository.save(user);

        this.logger.info("createUser successfully created user");

        return ResponseEntity.ok(user);
    }

}
