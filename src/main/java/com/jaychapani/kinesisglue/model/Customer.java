package com.jaychapani.kinesisglue.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaDescription;
import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaInject;
import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaString;
import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaTitle;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonSchemaDescription("Customer schema")
@JsonSchemaTitle("Customer")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
// Fully qualified class name to be added to an additionally injected property
// called className for deserializer to determine which class to deserialize
// the bytes into
@JsonSchemaInject(
    strings = {@JsonSchemaString(path = "className",
        value = "com.jaychapani.kinesisglue.model.Customer")}
)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Customer{

    @JsonProperty
    @JsonPropertyDescription("The unique identifier for a customer")
    int id;

    @JsonProperty
    @JsonPropertyDescription("First Name of the customer")
    String firstName;

    @JsonProperty
    @JsonPropertyDescription("Last Name of the customer")
    String lastName;

//    @JsonProperty
//    @JsonPropertyDescription("Phone of the customer")
//    String phone;

    @JsonProperty
    @JsonPropertyDescription("Address of the customer")
    String address;

}