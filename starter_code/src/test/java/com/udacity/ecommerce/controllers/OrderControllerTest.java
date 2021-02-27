package com.udacity.ecommerce.controllers;

import com.udacity.ecommerce.exceptions.UserNotFoundException;
import com.udacity.ecommerce.model.persistence.*;
import com.udacity.ecommerce.model.persistence.repositories.*;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.mockito.*;
import org.springframework.http.*;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class OrderControllerTest {
    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderController orderController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSubmitReturnsNotFoundWhenNoUserFound() throws UserNotFoundException {
        when(this.userRepository.findByUsername(anyString()))
            .thenReturn(null);

        exceptionRule.expect(UserNotFoundException.class);
        ResponseEntity<UserOrder> response = this.orderController.submit("testUsername");
        assertNotNull(response);
        assertNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testSubmitSucceeds() throws UserNotFoundException {
        Cart cart = new Cart();
        cart.setItems(Arrays.asList(new Item(), new Item()));

        User user = new User();
        user.setCart(cart);

        when(this.userRepository.findByUsername(anyString()))
            .thenReturn(user);

        ResponseEntity<UserOrder> response = this.orderController.submit("testUsername");
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testGetOrdersForUserFailsWhenNoUserFound() throws UserNotFoundException {
        when(this.userRepository.findByUsername(anyString()))
            .thenReturn(null);

        exceptionRule.expect(UserNotFoundException.class);
        ResponseEntity<List<UserOrder>> response = this.orderController.getOrdersForUser("testUsername");
        assertNotNull(response);
        assertNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testGerOrdersForUserSucceeds() throws UserNotFoundException {
        User user = new User();
        user.setUsername("testUser");

        when(this.userRepository.findByUsername(anyString()))
            .thenReturn(user);

        when(this.orderRepository.findByUser(user))
            .thenReturn(Collections.singletonList(new UserOrder()));

        ResponseEntity<List<UserOrder>> response = this.orderController.getOrdersForUser(user.getUsername());
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}