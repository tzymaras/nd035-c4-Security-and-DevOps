package com.udacity.ecommerce.controllers;

import com.udacity.ecommerce.exceptions.UserNotFoundException;
import com.udacity.ecommerce.model.persistence.*;
import com.udacity.ecommerce.model.persistence.repositories.*;
import com.udacity.ecommerce.model.requests.ModifyCartRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ItemRepository itemRepository;

    @PostMapping("/addToCart")
    public ResponseEntity<Cart> addTocart(@RequestBody ModifyCartRequest request) throws UserNotFoundException {
        User user = userRepository.findByUsername(request.getUsername());
        if (user == null) {
            throw new UserNotFoundException();
        }

        Optional<Item> item = itemRepository.findById(request.getItemId());
        if (!item.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Cart cart = user.getCart();
        IntStream.range(0, request.getQuantity())
            .forEach(i -> cart.addItem(item.get()));

        cartRepository.save(cart);

        return ResponseEntity.ok(cart);
    }

    @PostMapping("/removeFromCart")
    public ResponseEntity<Cart> removeFromcart(@RequestBody ModifyCartRequest request) throws UserNotFoundException {
        User user = userRepository.findByUsername(request.getUsername());
        if (user == null) {
            throw new UserNotFoundException();
        }

        Optional<Item> item = itemRepository.findById(request.getItemId());
        if (!item.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Cart cart = user.getCart();

        IntStream.range(0, request.getQuantity())
            .forEach(i -> cart.removeItem(item.get()));

        cartRepository.save(cart);

        return ResponseEntity.ok(cart);
    }

}
