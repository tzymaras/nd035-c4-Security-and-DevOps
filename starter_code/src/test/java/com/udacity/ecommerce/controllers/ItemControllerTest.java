package com.udacity.ecommerce.controllers;

import com.udacity.ecommerce.model.persistence.Item;
import com.udacity.ecommerce.model.persistence.repositories.ItemRepository;
import org.junit.*;
import org.mockito.*;
import org.springframework.http.*;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class ItemControllerTest {
    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemController itemController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetItems() {
        when(this.itemRepository.findAll())
            .thenReturn(Arrays.asList(new Item(), new Item()));

        ResponseEntity<List<Item>> response = this.itemController.getItems();
        assertNotNull(response);
        assertNotNull(response.getBody());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    @Test
    public void testGetItemById() {
        when(this.itemRepository.findById(anyLong()))
            .thenReturn(Optional.of(new Item()));

        ResponseEntity<Item> response = this.itemController.getItemById(1L);
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testGetItemsByName() {
        when(this.itemRepository.findByName(anyString()))
            .thenReturn(null)
            .thenReturn(Arrays.asList(new Item(), new Item()));

        ResponseEntity<List<Item>> response = this.itemController.getItemsByName("testName");
        assertNotNull(response);
        assertNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        response = this.itemController.getItemsByName("testName");
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals(2, response.getBody().size());
    }
}