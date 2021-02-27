package com.udacity.ecommerce.controllers;

import com.udacity.ecommerce.model.persistence.User;
import com.udacity.ecommerce.model.persistence.repositories.*;
import com.udacity.ecommerce.model.requests.CreateUserRequest;
import org.junit.*;
import org.mockito.*;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

public class UserControllerTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private UserController userController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFindById() {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("testHashedPassword");

        Optional<User> storedUser = Optional.of(user);

        when(this.userRepository.findById(anyLong()))
            .thenReturn(storedUser)
            .thenReturn(Optional.empty());

        ResponseEntity<User> response = this.userController.findById(5L);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user.getUsername(), response.getBody().getUsername());
        assertEquals(user.getPassword(), response.getBody().getPassword());

        response = this.userController.findById(5L);

        assertNotNull(response);
        assertNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testFindByUserName() {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("testHashedPassword");

        when(this.userRepository.findByUsername(user.getUsername()))
            .thenReturn(user)
            .thenReturn(null);

        ResponseEntity<User> response = this.userController.findByUserName(user.getUsername());

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user.getUsername(), response.getBody().getUsername());
        assertEquals(user.getPassword(), response.getBody().getPassword());

        response = this.userController.findByUserName(user.getUsername());

        assertNotNull(response);
        assertNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void createUserHappyPath() {
        when(this.bCryptPasswordEncoder.encode("testPassword")).thenReturn("thisIsHashed");

        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("test");
        createUserRequest.setPassword("testPassword");
        createUserRequest.setConfirmPassword("testPassword");

        ResponseEntity<User> response = this.userController.createUser(createUserRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User user = response.getBody();

        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals(createUserRequest.getUsername(), user.getUsername());
        assertEquals("thisIsHashed", user.getPassword());
    }

    @Test
    public void testCreateUserFailsWithNonMatchingPasswords() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("test");
        createUserRequest.setPassword("testPassword");
        createUserRequest.setConfirmPassword("notTestPassword");

        ResponseEntity<User> response = this.userController.createUser(createUserRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}