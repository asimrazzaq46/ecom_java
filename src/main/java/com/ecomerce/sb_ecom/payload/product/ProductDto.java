package com.ecomerce.sb_ecom.payload.product;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private Long productId;
    @NotBlank
    @Size(min = 3, max = 100, message = "Product name must be between 3 to 100 characters.")
    private String productName;
    @NotBlank
    @Size(min = 6, max = 500, message = "Product name must be between 6 to 500 characters.")
    private String description;
    private String image;
    private Integer quantity;
    @NotNull
    @Positive(message = "Price must be greater than 0.")
    private Double price;
    private Double specialPrice;

    @DecimalMin(value = "0.0", message = "Discount cannot be less than 0.")
    @DecimalMax(value = "99.99", message = "Discount cannot be 100 or greater.")
    private Double discount;
}
