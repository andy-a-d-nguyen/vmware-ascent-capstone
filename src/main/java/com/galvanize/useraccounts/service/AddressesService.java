package com.galvanize.useraccounts.service;

import com.galvanize.useraccounts.exception.AddressNotFoundException;
import com.galvanize.useraccounts.exception.UserNotFoundException;
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

    public AddressesService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;

    }
}
