package com.ecommerce.user.service;

import com.ecommerce.user.entity.User;
import com.ecommerce.user.model.RegisterUserRequest;
import com.ecommerce.user.repository.UserRepository;
import com.ecommerce.user.security.BCrypt;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import jakarta.validation.Validator;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ValidationService validationService;

    @Transactional

    public void register(RegisterUserRequest request) {
        validationService.validate(request);

        if(userRepository.existsById(request.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(BCrypt.hash(request.getPassword()));
        user.setName(request.getName());

        userRepository.save(user);
    }
}
