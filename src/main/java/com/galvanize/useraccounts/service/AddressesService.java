package com.galvanize.useraccounts.service;

import com.galvanize.useraccounts.model.Address;
import com.galvanize.useraccounts.model.User;
import com.galvanize.useraccounts.repository.AddressRepository;
import com.galvanize.useraccounts.repository.UsersRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AddressesService {
    private final AddressRepository addressRepository;
    private final UsersRepository usersRepository;

    public AddressesService(AddressRepository addressRepository, UsersRepository usersRepository) {
        this.addressRepository = addressRepository;
        this.usersRepository = usersRepository;
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
        //make sure that address belongs to userId
        Optional <User> oUser = usersRepository.findById(userId);
        Optional <Address> oAddress = addressRepository.findById(addressId);

        //refactor this if statement in future :3
        if (oUser.isPresent() && oAddress.isPresent() && oUser.get().getId() == oAddress.get().getUserId()) {
            addressRepository.delete(oAddress.get());
        }
    }
}
