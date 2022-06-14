package com.jaychapani.kinesisglue.endpoint;

import com.jaychapani.kinesisglue.model.User;
import com.jaychapani.kinesisglue.stream.UserStreamConfiguration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("users")
public class UserController {

    @Autowired
    private UserStreamConfiguration userStreamConfiguration;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Boolean> addUser(@RequestBody User user) throws IOException {

        userStreamConfiguration.addUser(user);

        return new ResponseEntity<Boolean>(true, HttpStatus.OK);
    }
}
