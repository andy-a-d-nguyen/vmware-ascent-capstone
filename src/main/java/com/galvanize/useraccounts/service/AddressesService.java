package com.galvanize.useraccounts.service;

import com.galvanize.useraccounts.model.Address;
import com.galvanize.useraccounts.repository.AddressRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AddressesService {
    private final AddressRepository addressRepository;

    public AddressesService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public Address addAddress(Long userId, Address address) {
        address.setUserId(userId);
        return addressRepository.save(address);
    }

    public List<Address> getAllAddresses(Long userId) {
        return null;
    }

    public Address updateAddress(Long userId, Address updatedAddress) {
        return null;
    }

    public void deleteAddress(Long userId, Long addressId) {
    }
}
