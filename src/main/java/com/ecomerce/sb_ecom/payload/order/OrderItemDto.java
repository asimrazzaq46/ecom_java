package com.ecomerce.sb_ecom.payload.order;

import com.ecomerce.sb_ecom.payload.product.ProductDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDto {
    private Long orderItemId;
    private ProductDto productDto;
    private Integer quantity;
    private double discount;
    private double orderProductPrice;
    

}
