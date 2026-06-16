package com.ecomerce.sb_ecom.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "products")
@ToString
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotBlank
    @Size(min = 3, max = 100, message = "Product name must be between 3 to 100 characters.")
    private String productName;
    @NotBlank
    @Size(min = 6, max = 500, message = "Product name must be between 6 to 500 characters.")
    private String description;
    private String Image;
    @NotNull
    @Positive(message = "Price must be greater than 0.")
    private Double price;
    private Integer quantity;
    private Double specialPrice;

    @DecimalMin(value = "0.0", message = "Discount cannot be less than 0.")
    @DecimalMax(value = "99.99", message = "Discount cannot be 100 or greater.")
    private Double discount;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User user;

    @OneToMany(mappedBy = "product", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    private List<CartItem> cartItems = new ArrayList<>();

}
