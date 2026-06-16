package com.ecomerce.sb_ecom.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
@Table(name = "addresses")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Long addressId;
    @NotBlank
    @Size(min = 4, message = "Building name must be at least 3 characters")
    private String buildingName;
    @NotBlank
    @Size(min = 3, message = "City name must be at least 3 characters")
    private String city;

    @NotBlank
    @Size(min = 3, message = "Country name must be at least 3 characters")
    private String country;

    @NotBlank
    @Size(min = 2, message = "Country name must be at least 2 characters")
    private String state;

    @NotBlank
    @Size(min = 5, message = "Country name must be at least 5 characters")
    private String pinCode;

    @NotBlank
    @Size(min = 5, message = "Street name must be at least 5 characters")
    private String street;

    @ToString.Exclude
    @ManyToMany(mappedBy = "addresses")
    private List<User> user = new ArrayList<>();

    public Address(String street, String pinCode, String state, String country, String city, String buildingName) {
        this.street = street;
        this.pinCode = pinCode;
        this.state = state;
        this.country = country;
        this.city = city;
        this.buildingName = buildingName;
    }
}
