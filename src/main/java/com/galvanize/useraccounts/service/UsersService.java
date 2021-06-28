package com.galvanize.useraccounts.service;

import com.galvanize.useraccounts.UsersList;
import com.galvanize.useraccounts.exception.*;
import com.galvanize.useraccounts.model.Address;
import com.galvanize.useraccounts.model.User;

import com.galvanize.useraccounts.model.UserCondensed;
import com.galvanize.useraccounts.repository.AddressRepository;
import com.galvanize.useraccounts.repository.UsersRepository;
import com.galvanize.useraccounts.request.UserRequest;
import org.springframework.stereotype.Service;


import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.IntStream;

@Service
public class UsersService {
    private final UsersRepository usersRepository;
    private final AddressRepository addressRepository;

    public UsersService(UsersRepository usersRepository, AddressRepository addressRepository) {
        this.usersRepository = usersRepository;
        this.addressRepository = addressRepository;
    }

    public User createUser(User user) {
        Optional<User> foundUser = usersRepository.findByUsernameExactMatch(user.getUsername());
        Optional<User> foundUserEmail = usersRepository.findByEmailExactMatch(user.getEmail());

        if (foundUser.isPresent() || foundUserEmail.isPresent()) {
            throw new DuplicateUserException();
        }
        //this sets up the one to many relationship between user and addresses
        user.getAddresses().forEach(address -> address.setUser(user));

        return usersRepository.save(user);
    }

    public void deleteUser(Long guid) {
        Optional<User> user = usersRepository.findByGuid(guid);

        if (user.isPresent()) {
            usersRepository.delete(user.get());
        } else {
            throw new UserNotFoundException();
        }
    }

    public User updateUser(Long guid, UserRequest updatedUser) {
        User user = getUser(guid);

        if (user != null) {
            user.setFirstName(updatedUser.getFirstName());
            user.setLastName(updatedUser.getLastName());
            user.setEmail(updatedUser.getEmail());
            user.setBio(updatedUser.getBio());
            user.setVerified(updatedUser.isVerified());
            user.setAvatar(updatedUser.getAvatar());
            return usersRepository.save(user);
        }
        return null;
    }

    public User getUser(Long guid) {
        return usersRepository.findByGuid(guid).orElse(null);
    }


    public UsersList searchUsers(String username) {
        if (username == null) username = "";

        UsersList users = new UsersList(usersRepository.findByUsername("%" + username + "%"));

        return users.isEmpty() ? null : users;
    }

    public User addAddress(Long userGuid, Address address) {
        Optional<User> user = usersRepository.findByGuid(userGuid);

        user.ifPresent(u -> u.addAddress(address));

        if (user.isPresent()) {
            return usersRepository.save(user.get());

        } else {
            throw new UserNotFoundException();
        }
    }

    public User updateAddress(Long userGuid, Long addressId, Address address) {
        Optional<User> oUser = usersRepository.findByGuid(userGuid);
        Optional<Address> oAddress = addressRepository.findById(addressId);
        if (oUser.isPresent()) {
            //int doesNotWork = oUser.get().getAddresses().indexOf(oAddress);
            int oAddressIndex = IntStream.range(0, oUser.get().getAddresses().size())
                    .filter(i -> oUser.get().getAddresses().get(i).getId() == addressId)
                    .findFirst().orElse(-1);
            if (oAddressIndex != -1) {
                Address updatedAddress = oUser.get().getAddresses().get(oAddressIndex);
                updatedAddress.setStreet(address.getStreet());
                updatedAddress.setCity(address.getCity());
                updatedAddress.setState(address.getState());
                updatedAddress.setZipcode(address.getZipcode());
                updatedAddress.setApartment(address.getApartment());
                oUser.get().setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
                return usersRepository.save(oUser.get());
            } else {
                throw new AddressNotFoundException();
            }
        } else {
            throw new UserNotFoundException();
        }
    }

    public void deleteAddress(Long userGuid, Long addressId) {
        Optional<User> oUser = usersRepository.findByGuid(userGuid);
        Optional<Address> oAddress = addressRepository.findById(addressId);
        if (oUser.isPresent()) {
            int oAddressIndex = IntStream.range(0, oUser.get().getAddresses().size())
                    .filter(i -> oUser.get().getAddresses().get(i).getId() == addressId)
                    .findFirst().orElse(-1);
            if (oAddressIndex != -1) {

                oUser.get().getAddresses().remove(oAddressIndex);
                oAddress.get().setUser(null);
                oUser.get().setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
                oUser.ifPresent(usersRepository::save);
                oAddress.ifPresent(addressRepository::delete);
            } else {
                throw new AddressNotFoundException();
            }
        } else {
            throw new UserNotFoundException();
        }
    }

    public UserCondensed getUserCondensed(Long guid) {
        Optional<User> oUser = usersRepository.findByGuid(guid);
        UserCondensed user;

        if (oUser.isPresent()) {
            user = new UserCondensed(oUser.get().getGuid(), oUser.get().getUsername(), oUser.get().getAvatar(), oUser.get().getEmail());
        } else {
            throw new UserNotFoundException();
        }

        return user;
    }
}
