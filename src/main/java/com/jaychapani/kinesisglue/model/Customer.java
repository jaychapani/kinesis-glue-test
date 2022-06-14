package com.jaychapani.kinesisglue.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Customer{

    int id;
    String firstName;
    String lastName;
    String address;

    public Customer() {
    }

    public Customer(int id, String firstName, String lastName, String address) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
    }
}