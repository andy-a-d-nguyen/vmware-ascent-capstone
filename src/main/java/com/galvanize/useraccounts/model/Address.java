package com.galvanize.useraccounts.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.Hibernate;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Table(name = "address")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="user_id", nullable = false)
    @JsonIgnore
    private User user;
    // private Long userId;
    @NotBlank(message="Street cannot be null and trimmed length must be greater than zero.")
    private String street;
    @NotBlank(message="City cannot be null and trimmed length must be greater than zero.")
    private String city;
    @NotBlank (message="State cannot be null and trimmed length must be greater than zero.")
    private String state;
    @NotBlank(message="Zipcode cannot be null and trimmed length must be greater than zero.")
    private String zipcode;
    private String apartment;
    private String label;

    public Address() {
    }

    public Address( String street, String city, String state, String zipcode, String apartment, String label) {
        this.street = street;
        this.city = city;
        this.state = state;
        this.zipcode = zipcode;
        this.apartment = apartment;
        this.label = label;
    }

    public long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getApartment() {
        return apartment;
    }

    public void setApartment(String apartment) {
        this.apartment = apartment;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

}