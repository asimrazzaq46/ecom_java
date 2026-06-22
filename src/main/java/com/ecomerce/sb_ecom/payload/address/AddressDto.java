package com.ecomerce.sb_ecom.payload.address;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddressDto {

    private Long addressId;

    private String buildingName;

    private String city;

    private String country;

    private String state;

    private String pinCode;
    
    private String street;

}
