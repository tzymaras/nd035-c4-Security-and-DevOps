package com.udacity.ecommerce.controllers;

import com.udacity.ecommerce.exceptions.UserNotFoundException;
import com.udacity.ecommerce.model.persistence.*;
import com.udacity.ecommerce.model.persistence.repositories.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/order")
public class OrderController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    private final Logger logger = LoggerFactory.getLogger("splunk.logger");

    @PostMapping("/submit/{username}")
    public ResponseEntity<UserOrder> submit(@PathVariable String username) throws UserNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UserNotFoundException();
        }

        UserOrder order = UserOrder.createFromCart(user.getCart());
        orderRepository.save(order);

        this.logger.info("orderEvent:createdOrder");

        user.getCart().setItems(new ArrayList<>());

        return ResponseEntity.ok(order);
    }

    @GetMapping("/history/{username}")
    public ResponseEntity<List<UserOrder>> getOrdersForUser(@PathVariable String username) throws UserNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UserNotFoundException();
        }

        return ResponseEntity.ok(orderRepository.findByUser(user));
    }
}
