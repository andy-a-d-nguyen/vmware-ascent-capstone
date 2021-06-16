package com.galvanize.useraccounts.service;

import com.galvanize.useraccounts.UsersList;
import com.galvanize.useraccounts.exception.*;
import com.galvanize.useraccounts.model.Address;
import com.galvanize.useraccounts.model.User;

import com.galvanize.useraccounts.repository.AddressRepository;
import com.galvanize.useraccounts.repository.UsersRepository;
import com.galvanize.useraccounts.request.UserRequest;
import org.springframework.stereotype.Service;


import javax.swing.text.html.Option;
import java.util.List;
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
        user.getAddresses().forEach( address -> address.setUser(user));
        return usersRepository.save(user);
    }

    public void deleteUser(Long id) {
        Optional<User> user = usersRepository.findById(id);

        if (user.isPresent()) {
            usersRepository.delete(user.get());
        } else {
            throw new UserNotFoundException();
        }
    }

    public User updateUser(Long id, UserRequest updatedUser) {
        User user = getUser(id);

        if (user != null) {
            user.setFirstName(updatedUser.getFirstName());
            user.setLastName(updatedUser.getLastName());
            user.setEmail(updatedUser.getEmail());
            user.setCreditCard(updatedUser.getCreditCard());
            user.setVerified(updatedUser.isVerified());
            return usersRepository.save(user);
        }
        return null;
    }

    public User getUser(Long id) {
        return usersRepository.findById(id).orElse(null);
    }

    public Boolean updateUserPassword(Long id, String oldPassword, String newPassword) {
        User user = getUser(id);

        if (user != null && user.getPassword().equals(oldPassword)) {
            user.setPassword(newPassword);
            usersRepository.save(user);
            return true;
        }

        return false;
    }

    public User setAvatar(Long id, String url) {
        User user = getUser(id);

        if (user != null) {
            user.setAvatar(url);
            return usersRepository.save(user);
        }

        return null;
    }

    public UsersList searchUsers(String username) {
        if (username == null) username = "";

        UsersList users = new UsersList(usersRepository.findByUsername("%" + username + "%"));

        return users.isEmpty() ? null : users;
    }

    public User addAddress(Long userId, List<Address> addresses) {
        Optional<User> user = usersRepository.findById(userId);

        user.ifPresent(value -> addresses.forEach(address -> address.setUser(user.get())));

        if (user.isPresent()) {
            return usersRepository.save(user.get());

        } else {
            throw new UserNotFoundException();
        }
    }

    public User updateAddress(Long userId, Long addressId, Address address) {
        Optional<User> oUser = usersRepository.findById(userId);
        Optional<Address> oAddress = addressRepository.findById(addressId);
        if (oUser.isPresent()) {
            //int doesNotWork = oUser.get().getAddresses().indexOf(oAddress);
            int oAddressIndex = IntStream.range(0, oUser.get().getAddresses().size())
                    .filter(i -> oUser.get().getAddresses().get(i).getId() == addressId)
                    .findFirst().orElse(-1);
            if (oAddressIndex != -1) {
                oUser.get().getAddresses().set(oAddressIndex, address);
                return usersRepository.save(oUser.get());
            } else {
                throw new AddressNotFoundException();
            }
        } else {
            throw new UserNotFoundException();
        }
    }

    public void deleteAddress(Long userId, Long addressId) {
        Optional<User> oUser = usersRepository.findById(userId);
        Optional<Address> oAddress = addressRepository.findById(addressId);
        if (oUser.isPresent()) {
            int oAddressIndex = IntStream.range(0, oUser.get().getAddresses().size())
                    .filter(i -> oUser.get().getAddresses().get(i).getId() == addressId)
                    .findFirst().orElse(-1);
            if (oAddressIndex != -1) {
                oUser.get().getAddresses().remove(oAddressIndex);
                oAddress.ifPresent(addressRepository::delete);
            } else {
                throw new AddressNotFoundException();
            }
        } else {
            throw new UserNotFoundException();
        }
    }

}
