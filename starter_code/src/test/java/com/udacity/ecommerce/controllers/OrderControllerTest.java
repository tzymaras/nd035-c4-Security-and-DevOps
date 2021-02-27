package com.udacity.ecommerce.controllers;

import com.udacity.ecommerce.model.persistence.*;
import com.udacity.ecommerce.model.persistence.repositories.*;
import org.junit.*;
import org.mockito.*;
import org.springframework.http.*;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class OrderControllerTest {
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
    public void testSubmitReturnsNotFoundWhenNoUserFound() {
        when(this.userRepository.findByUsername(anyString()))
            .thenReturn(null);

        ResponseEntity<UserOrder> response = this.orderController.submit("testUsername");
        assertNotNull(response);
        assertNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testSubmitSucceeds() {
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
    public void testGetOrdersForUserFailsWhenNoUserFound() {
        when(this.userRepository.findByUsername(anyString()))
            .thenReturn(null);

        ResponseEntity<List<UserOrder>> response = this.orderController.getOrdersForUser("testUsername");
        assertNotNull(response);
        assertNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testGerOrdersForUserSucceeds() {
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