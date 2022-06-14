package com.jaychapani.kinesisglue.endpoint;

import com.jaychapani.kinesisglue.model.Customer;
import com.jaychapani.kinesisglue.stream.CustomerStreamConfiguration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class CustomerController {

    @Autowired
    private CustomerStreamConfiguration customerStreamConfiguration;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Boolean> addCustomer(@RequestBody Customer customer) throws IOException {

        customerStreamConfiguration.addCustomer(customer);

        return new ResponseEntity<Boolean>(true, HttpStatus.OK);
    }
}
