package com.ecomerce.sb_ecom.payload.cart;

import com.ecomerce.sb_ecom.payload.product.ProductDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartDto {
    private Long id;
    private Double totalPrice = 0.0;
    private List<ProductDto> products = new ArrayList<>();

}
