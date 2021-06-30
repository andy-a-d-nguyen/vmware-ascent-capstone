//package com.galvanize.useraccounts.dataLoader;
//
//import com.galvanize.useraccounts.repository.UsersRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.TestPropertySource;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//@TestPropertySource(locations = "classpath:application-test.properties")
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//public class dataLoaderTests {
//    @Autowired
//    DataLoader dataLoader;
//
//    @Autowired
//    UsersRepository usersRepository;
//
////    @BeforeEach
////    void setup() {
////        dataLoader = new DataLoader();
////    }
//
//    @Test
//    void test() {
////        String[] strings = {"", ""};
//////        dataLoader.seedUserData();
////        this.dataLoader.run(strings);
//
//        assertEquals(10, usersRepository.findAll().size());
//    }
//}
