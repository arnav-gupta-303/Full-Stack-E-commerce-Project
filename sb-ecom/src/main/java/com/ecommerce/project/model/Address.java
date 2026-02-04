package com.ecommerce.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@ToString
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    @NotBlank
    @Size(min = 5, message = "Buiding name must be of 5 characters")
    private String building;
    @NotBlank
    @Size(min = 5, message = "Country name must be of 5 characters")
    private String country;
    @NotBlank
    @Size(min = 2, message = "City name must be of 5 characters")
    private String city;
    @NotBlank
    @Size(min = 5, message = "Street name must be of 5 characters")
    private String street;
    @NotBlank
    @Size(min = 5, message = "State name must be of 5 characters")
    private String state;
    @NotBlank
    @Size(min = 6, message = "Pincode name must be of 5 characters")
    private String pincode;
    @ToString.Exclude
    @ManyToMany(mappedBy = "addresses")
    private List<User> users=new ArrayList<>();

    public Address(String building, String country, String city, String street, String state, String pincode, List<User> users) {
        this.building = building;
        this.country = country;
        this.city = city;
        this.street = street;
        this.state = state;
        this.pincode = pincode;
        this.users = users;
    }
}
