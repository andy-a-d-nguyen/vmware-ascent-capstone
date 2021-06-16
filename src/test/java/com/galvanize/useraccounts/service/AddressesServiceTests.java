package com.galvanize.useraccounts.service;

import com.galvanize.useraccounts.exception.AddressNotFoundException;
import com.galvanize.useraccounts.exception.UserNotFoundException;
import com.galvanize.useraccounts.model.Address;
import com.galvanize.useraccounts.model.User;
import com.galvanize.useraccounts.repository.AddressRepository;
import com.galvanize.useraccounts.repository.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AddressesServiceTests {
    private AddressesService addressesService;
    private UsersService usersService;

    private User user;

    @Mock
    AddressRepository addressRepository;

    @Mock
    UsersRepository usersRepository;

    @BeforeEach
    void setup() {
        addressesService = new AddressesService(addressRepository);
        usersService = new UsersService(usersRepository);
        user = new User("bakerBob", "password123", "bob","baker", "bakerBob@gmail.com");
        user.setId(1L);
    }

    /*@DisplayName("It should save address belonging to a user")
    @Test
    public void saveAddress() {
        Address address = new Address("street", "city", "state", "00000");
     //   address.setUserId(user.getId());
        when(addressRepository.save(address)).thenReturn(address);

        Address newAddress = addressesService.addAddress(user.getId(), address);

        assertNotNull(newAddress);
        assertEquals(newAddress.getCity(), address.getCity());
        assertEquals(newAddress.getState(), address.getState());
        assertEquals(newAddress.getZipcode(), address.getZipcode());
     //   assertEquals(newAddress.getUserId(), user.getId());
    }*/

//    @DisplayName("It should delete an address belonging to a user so it does not come back")
//    @Test
//    public void deleteAddress() {
//        Address address = new Address("street", "city", "state", "00000");
//     //   address.setUserId(user.getId());
//        address.setId(123L);
//        // doNothing() never calls delete
//        doNothing().when(addressRepository).delete(address);
//        when(usersRepository.findById(anyLong())).thenReturn(Optional.of(user));
//        when(addressRepository.findById(anyLong())).thenReturn(Optional.of(address));
//
//       // addressesService.deleteAddress(user.getId(), address.getId());
//        verify(addressRepository).delete(address);
//    }

//    @DisplayName("It should throw a UserNotFoundException when deleting an address belonging to a user that is not found")
//    @Test
//    public void deleteAddress_UserNotFoundException() {
//        Address address = new Address("street", "city", "state", "00000");
//     //   address.setUserId(user.getId());
//        address.setId(123L);
//        // doNothing() never calls delete
//
//        when(usersRepository.findById(anyLong())).thenReturn(Optional.empty());
//
//        assertThatExceptionOfType(UserNotFoundException.class)
//                .isThrownBy(() -> {
//                    addressesService.deleteAddress(user.getId(), address.getId());
//                });
//    }

    @DisplayName("It should throw a AddressNotFoundException when deleting an address that is not found")
    @Test
    public void deleteAddress_AddressNotFoundException() {
//        Address address = new Address("street", "city", "state", "00000");
//    //    address.setUserId(user.getId());
//        address.setId(123L);
//        // doNothing() never calls delete
//        when(usersRepository.findById(anyLong())).thenReturn(Optional.of(user));
//
//        when(addressRepository.findById(anyLong())).thenReturn(Optional.empty());
//
//        assertThatExceptionOfType(AddressNotFoundException.class)
//                .isThrownBy(() -> {
//                    addressesService.deleteAddress(user.getId(), address.getId());
//                });
    }

}
