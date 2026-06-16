package com.ecomerce.sb_ecom.controller;

import com.ecomerce.sb_ecom.interfaces.ICartService;
import com.ecomerce.sb_ecom.payload.cart.CartDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CartController {


    @Autowired
    private ICartService cartService;

    @PostMapping("/carts/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDto> addProductToCart(@PathVariable Long productId, @PathVariable Integer quantity) {
        CartDto cartDto = cartService.addProductToCart(productId, quantity);
        return new ResponseEntity<>(cartDto, HttpStatus.CREATED);
    }

//    @GetMapping("/carts")
//    public ResponseEntity<List<CartDto>> getAllCarts(
//            @RequestParam(defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
//            @RequestParam(defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
//            @RequestParam(required = false) String sortBy,
//            @RequestParam(defaultValue = AppConstants.SORT_ORDER, required = false) String sortOrder
//    ) {
//        List<CartDto> cartDtos = cartService.getAllCarts(pageNumber, pageSize, sortBy, sortOrder);
//
//        return new ResponseEntity<>(cartDtos, HttpStatus.OK);
//    }

    @GetMapping("/carts")
    public ResponseEntity<List<CartDto>> getAllCarts() {
        List<CartDto> cartDtos = cartService.getAllCarts();

        return new ResponseEntity<>(cartDtos, HttpStatus.OK);
    }
}
