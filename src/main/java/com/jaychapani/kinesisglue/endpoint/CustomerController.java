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

@RestController
public class CustomerController {

    @Autowired
    private CustomerStreamConfiguration customerStreamConfiguration;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Boolean> add(@RequestBody Customer customer) {

        customerStreamConfiguration.addCustomer(customer);

        return new ResponseEntity<Boolean>(true, HttpStatus.OK);
    }
}
