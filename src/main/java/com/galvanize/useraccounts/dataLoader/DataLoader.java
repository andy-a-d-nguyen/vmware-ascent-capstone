package com.galvanize.useraccounts.dataLoader;

import com.galvanize.useraccounts.model.Address;
import com.galvanize.useraccounts.model.User;
import com.galvanize.useraccounts.repository.AddressRepository;
import com.galvanize.useraccounts.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.boot.CommandLineRunner;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.print.attribute.standard.Media;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.aspectj.runtime.internal.Conversions.intValue;

@Profile("!test")
@Component
public class DataLoader implements CommandLineRunner {
    @Autowired
    UsersRepository usersRepository;

    @Autowired
    AddressRepository addressRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public void run(String... args) throws Exception {
        seedUserData();
    }

    private void seedUserData() {

        //IF USERS TABLE ALREADY HAS 10 RECORDS, DON'T FILL IT UP
        List l = entityManager.createNativeQuery("SELECT COUNT(id) FROM users").getResultList();
        for (Object c : l) {
            int count = intValue(c);
            if (count >= 10) {
                return;
            }
        }

        List<Address> addresses = new ArrayList<>();

        // CREATE ADDRESSES
        String[] streets = {"123 Baker Ave", "12356 Croissant Lane", "54326 Cookie Street", "233 Sesame Street", "22 Jump Street"};
        String[] cities = {"San Francisco", "San Jose", "Austin", "New York", "Denver", "Boston", "Mountain View", "Palo Alto", "Seattle", "Atlanta"};
        String[] states = {"CA", "TX", "NY", "GA", "CO"};
        String[] labels = {"home", "work", "mom's place", "pop's place"};

        Random random = new Random();

        for (int i = 0; i < streets.length; i++) {
            String zipcode = null;

            for (int j = 0; j < 5; j++) {
                zipcode.concat(String.valueOf(random.nextInt(9)));
            }

            Address address = new Address(streets[random.nextInt(streets.length)], cities[random.nextInt(cities.length)], states[random.nextInt(states.length)], zipcode, String.valueOf(random.nextInt(50) + 10), labels[random.nextInt(labels.length)]);
            addresses.add(address);
        }

        // CREATE USER
        List<String> names = new ArrayList<>();
        names.add("Bob Smith");
        names.add("Jane Doe");
        names.add("Andy Li");
        names.add("Rafael Parker");
        names.add("Rob Snyder");
        names.add("Yvonne Spears");
        names.add("Monica Croissant");
        names.add("Peter Isaguy");
        names.add("Elizabeth Swan");
        names.add("Jack Sparrow");

        for (int i = 0; i < names.size(); i++) {
            User userToAdd;

            if (random.nextInt(10) % 2 == 0) {
                userToAdd = new User(Long.valueOf(i),
                        names.get(i).split(" ")[0] + i + "user",
                        names.get(i).split(" ")[0],
                        names.get(i).split(" ")[1],
                        names.get(i).split(" ")[0] + "@email.com"
                );
            } else {
                userToAdd = new User(Long.valueOf(i),
                        names.get(i).split(" ")[0] + i + "user",
                        names.get(i).split(" ")[0],
                        names.get(i).split(" ")[1],
                        names.get(i).split(" ")[0] + "@email.com",
                        Arrays.asList(addresses.get(random.nextInt(addresses.size())))
                );
            }

            usersRepository.save(userToAdd);
        }
    }
}
