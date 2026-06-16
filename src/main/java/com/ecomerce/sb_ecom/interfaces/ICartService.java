package com.ecomerce.sb_ecom.interfaces;

import com.ecomerce.sb_ecom.payload.cart.CartDto;

import java.util.List;

public interface ICartService {
    CartDto addProductToCart(Long productId, Integer quantity);
//    List<CartDto> getAllCarts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    List<CartDto> getAllCarts();

}
