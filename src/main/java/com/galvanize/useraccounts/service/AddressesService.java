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
        return null;
    }

    public List<Address> getAllAddresses(Long userId) {
        return null;
    }
}
