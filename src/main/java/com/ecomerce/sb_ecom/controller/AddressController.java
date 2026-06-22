package com.ecomerce.sb_ecom.controller;

import com.ecomerce.sb_ecom.interfaces.IAddressService;
import com.ecomerce.sb_ecom.payload.address.AddressDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AddressController {

    @Autowired
    private IAddressService addressService;

    @PostMapping("/addresses")
    public ResponseEntity<AddressDto> addAddress(@Valid @RequestBody AddressDto addressDto) {

        AddressDto address = addressService.AddAddress(addressDto);
        return new ResponseEntity<>(address, HttpStatus.CREATED);
    }

    @GetMapping("/addresses")
    public ResponseEntity<List<AddressDto>> getAllAddresses() {

        List<AddressDto> addresses = addressService.getAllAddresses();
        return new ResponseEntity<>(addresses, HttpStatus.CREATED);
    }


    @GetMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDto> getAddressById(@PathVariable("addressId") Long addressId) {
        AddressDto address = addressService.getAddressById(addressId);
        return new ResponseEntity<>(address, HttpStatus.OK);
    }

    @GetMapping("/user/addresses")
    public ResponseEntity<List<AddressDto>> getAddressById() {
        List<AddressDto> addresses = addressService.getAddressesByUser();
        return new ResponseEntity<>(addresses, HttpStatus.OK);
    }

    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDto> updateAddress(@PathVariable("addressId") Long addressId, @RequestBody AddressDto addressDto) {
        AddressDto address = addressService.updateAddress(addressId, addressDto);
        return new ResponseEntity<>(address, HttpStatus.OK);
    }

    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<String> deleteAddress(@PathVariable("addressId") Long addressId) {
        String status = addressService.deleteAddress(addressId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }


}
