package com.udacity.ecommerce.model.persistence.repositories;

import com.udacity.ecommerce.model.persistence.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import com.udacity.ecommerce.model.persistence.User;

public interface CartRepository extends JpaRepository<Cart, Long> {
	Cart findByUser(User user);
}
