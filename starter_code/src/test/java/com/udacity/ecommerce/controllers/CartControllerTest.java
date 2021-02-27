package com.udacity.ecommerce.controllers;

import com.udacity.ecommerce.exceptions.UserNotFoundException;
import com.udacity.ecommerce.model.persistence.*;
import com.udacity.ecommerce.model.persistence.repositories.*;
import com.udacity.ecommerce.model.requests.ModifyCartRequest;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.mockito.*;
import org.springframework.http.*;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class CartControllerTest {
    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private CartController cartController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAddTocart() throws UserNotFoundException {
        Item item1 = new Item();
        item1.setPrice(BigDecimal.valueOf(100L));

        Cart cart = new Cart();
        cart.setItems(new ArrayList<>());

        User user = new User();
        user.setCart(cart);

        ModifyCartRequest cartRequest = new ModifyCartRequest();
        cartRequest.setUsername("testUser");
        cartRequest.setQuantity(5);

        when(this.userRepository.findByUsername(anyString()))
            .thenReturn(null)
            .thenReturn(user);

        when(this.itemRepository.findById(any()))
            .thenReturn(Optional.of(item1))
            .thenReturn(Optional.empty());

        exceptionRule.expect(UserNotFoundException.class);
        ResponseEntity<Cart> response = this.cartController.addTocart(cartRequest);
        assertNotNull(response);
        assertNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        response = this.cartController.addTocart(cartRequest);
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(5, response.getBody().getItems().size());

        response = this.cartController.addTocart(cartRequest);
        assertNotNull(response);
        assertNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testRemoveFromcart() throws UserNotFoundException {
        Item item = new Item();
        item.setPrice(BigDecimal.valueOf(100L));
        item.setId(1L);

        Item item2 = new Item();
        item2.setPrice(BigDecimal.valueOf(100L));

        List<Item> items = new ArrayList<>();
        items.add(item);
        items.add(item2);

        Cart cart = new Cart();
        cart.setItems(items);

        User user = new User();
        user.setCart(cart);

        ModifyCartRequest cartRequest = new ModifyCartRequest();
        cartRequest.setUsername("testUser");
        cartRequest.setQuantity(1);
        cartRequest.setItemId(item.getId());

        when(this.userRepository.findByUsername(anyString()))
            .thenReturn(null)
            .thenReturn(user);

        when(this.itemRepository.findById(any()))
            .thenReturn(Optional.of(item))
            .thenReturn(Optional.empty());

        exceptionRule.expect(UserNotFoundException.class);
        ResponseEntity<Cart> response = this.cartController.removeFromcart(cartRequest);
        assertNotNull(response);
        assertNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        response = this.cartController.removeFromcart(cartRequest);
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getItems().size());

        response = this.cartController.removeFromcart(cartRequest);
        assertNotNull(response);
        assertNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}