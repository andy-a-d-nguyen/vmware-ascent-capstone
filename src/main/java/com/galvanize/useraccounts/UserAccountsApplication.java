package com.galvanize.useraccounts;

import com.galvanize.useraccounts.security.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class UserAccountsApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserAccountsApplication.class, args);
    }

    @Bean
    public JwtProperties getJwtProperties(){
        return new JwtProperties();
    }
}
