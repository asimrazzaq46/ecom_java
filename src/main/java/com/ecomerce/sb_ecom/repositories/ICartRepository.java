package com.ecomerce.sb_ecom.repositories;

import com.ecomerce.sb_ecom.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ICartRepository extends JpaRepository<Cart, Long> {

    @Query("SELECT c FROM Cart c Where c.user.email = ?1")
    Cart findCartByEmail(String email);
}
