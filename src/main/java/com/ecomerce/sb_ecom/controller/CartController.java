package com.ecomerce.sb_ecom.controller;

import com.ecomerce.sb_ecom.exceptions.ResourceNotFoundException;
import com.ecomerce.sb_ecom.interfaces.ICartService;
import com.ecomerce.sb_ecom.model.Cart;
import com.ecomerce.sb_ecom.payload.cart.CartDto;
import com.ecomerce.sb_ecom.repositories.ICartRepository;
import com.ecomerce.sb_ecom.util.AuthUtils;
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

    @Autowired
    private AuthUtils authUtils;

    @Autowired
    private ICartRepository cartRepo;


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

    @GetMapping("/carts/users/cart")
    public ResponseEntity<CartDto> getCartById() {
        String emailId = authUtils.loggedInEmail();
        Cart cart = cartRepo.findCartByEmail(emailId);
        if (cart == null) throw new ResourceNotFoundException("cart not found.");
        CartDto cartDto = cartService.getCartByEmailAndId(emailId, cart.getCartId());
        return new ResponseEntity<>(cartDto, HttpStatus.OK);
    }

    @PutMapping("/cart/products/{productId}/quantity/{operation}")
    public ResponseEntity<CartDto> updateCartProductQuantity(@PathVariable Long productId, @PathVariable String operation) {
        CartDto cartDto = cartService.updateCartProductQuantity(productId, operation.equalsIgnoreCase("delete") ? -1 : 1);
        return new ResponseEntity<>(cartDto, HttpStatus.OK);
    }

    @DeleteMapping("/carts/{cartId}/product/{productId}")
    public ResponseEntity<String> deleteProductFromCart(@PathVariable Long cartId, @PathVariable Long productId) {
        String status = cartService.deleteProductFromCart(cartId, productId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }


}
