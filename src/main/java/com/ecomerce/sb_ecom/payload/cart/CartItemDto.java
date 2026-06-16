package com.ecomerce.sb_ecom.payload.cart;

import com.ecomerce.sb_ecom.payload.product.ProductDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDto {
    private Long cartItemId;
    private CartDto cart;
    private ProductDto product;
    private Integer quantity;
    private Double productPrice;
    private Double discount;
}
