package com.ecomerce.sb_ecom.interfaces;

import com.ecomerce.sb_ecom.payload.address.AddressDto;

import java.util.List;

public interface IAddressService {
    AddressDto AddAddress(AddressDto addressDto);

    List<AddressDto> getAllAddresses();

    AddressDto getAddressById(Long addressId);

    List<AddressDto> getAddressesByUser();

    AddressDto updateAddress(Long addressId, AddressDto addressDto);

    String deleteAddress(Long addressId);
}
