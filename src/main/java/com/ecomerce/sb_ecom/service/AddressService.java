package com.ecomerce.sb_ecom.service;

import com.ecomerce.sb_ecom.exceptions.ResourceNotFoundException;
import com.ecomerce.sb_ecom.interfaces.IAddressService;
import com.ecomerce.sb_ecom.model.Address;
import com.ecomerce.sb_ecom.model.User;
import com.ecomerce.sb_ecom.payload.address.AddressDto;
import com.ecomerce.sb_ecom.repositories.IAddressRepository;
import com.ecomerce.sb_ecom.repositories.IUserRepository;
import com.ecomerce.sb_ecom.util.AuthUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressService implements IAddressService {


    @Autowired
    private AuthUtils authUtils;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private IAddressRepository addressRepo;

    @Autowired
    private IUserRepository userRepo;

    @Override
    public AddressDto AddAddress(AddressDto addressDto) {

        User user = authUtils.loggedInUser();
        Address address = modelMapper.map(addressDto, Address.class);

        List<Address> addressList = user.getAddresses();

        addressList.add(address);
        user.setAddresses(addressList);

        address.setUser(user);

        Address savedAddress = addressRepo.save(address);

        return modelMapper.map(savedAddress, AddressDto.class);

    }

    @Override
    public List<AddressDto> getAllAddresses() {
        List<Address> addresses = addressRepo.findAll();
        return addresses.stream().map(address -> modelMapper.map(address, AddressDto.class)).toList();
    }

    @Override
    public AddressDto getAddressById(Long addressId) {
        Address address = addressRepo.findById(addressId).orElseThrow(() -> new ResourceNotFoundException("address", "addressId", addressId));
        return modelMapper.map(address, AddressDto.class);
    }

    @Override
    public List<AddressDto> getAddressesByUser() {
//        Long userId = authUtils.loggedInUserId();
//        List<Address> userAddresses = addressRepo.findAddressByUserId(userId);
        User user = authUtils.loggedInUser();
        List<Address> addresses = user.getAddresses();
        return addresses.stream()
                .map(address -> modelMapper.map(address, AddressDto.class))
                .toList();
    }


    @Override
    public AddressDto updateAddress(Long addressId, AddressDto addressDto) {
        Address address = addressRepo.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("address", "addressId", addressId));

        if (addressDto.getCity() != null) {
            address.setCity(addressDto.getCity());
        }

        if (addressDto.getCountry() != null) {
            address.setCountry(addressDto.getCountry());
        }

        if (addressDto.getStreet() != null) {
            address.setStreet(addressDto.getStreet());
        }

        if (addressDto.getState() != null) {
            address.setState(addressDto.getState());
        }

        if (addressDto.getBuildingName() != null) {
            address.setBuildingName(addressDto.getBuildingName());
        }

        if (addressDto.getPinCode() != null) {
            address.setPinCode(addressDto.getPinCode());
        }

        Address savedAddress = addressRepo.save(address);


        User user = address.getUser();
        user.getAddresses().removeIf(a -> address.getAddressId().equals(a));

        user.getAddresses().add(savedAddress);

        userRepo.save(user);


        return modelMapper.map(savedAddress, AddressDto.class);

    }

    @Override
    public String deleteAddress(Long addressId) {
        Address address = addressRepo.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("address", "addressId", addressId));

        User user = address.getUser();
        user.getAddresses().removeIf(a -> address.getAddressId().equals(a));
        userRepo.save(user);
        addressRepo.delete(address);

        return "Address deleted successfully with id: " + addressId;
    }

}
