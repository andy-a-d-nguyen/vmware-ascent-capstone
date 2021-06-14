package com.galvanize.useraccounts.service;

import com.galvanize.useraccounts.model.Address;
import com.galvanize.useraccounts.model.User;
import com.galvanize.useraccounts.repository.AddressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AddressesServiceTests {
    private AddressesService addressesService;
    private User user;

    @Mock
    AddressRepository addressRepository;

    @BeforeEach
    void setup() {
        addressesService = new AddressesService(addressRepository);
        user = new User("bakerBob", "password123", "bob","baker", "bakerBob@gmail.com");
        user.setId(1L);
    }

    @DisplayName("It should save address belonging to a user")
    @Test
    public void saveAddress() {
        Address address = new Address("street", "city", "state", "00000");
        address.setUserId(user.getId());
        when(addressRepository.save(address)).thenReturn(address);

        Address newAddress = addressesService.addAddress(user.getId(), address);

        assertNotNull(newAddress);
        assertEquals(newAddress.getCity(), address.getCity());
        assertEquals(newAddress.getState(), address.getState());
        assertEquals(newAddress.getZipcode(), address.getZipcode());
        assertEquals(newAddress.getUserId(), user.getId());
    }


}
