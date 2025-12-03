package com.ecommerce.user.service;

import com.ecommerce.user.entity.User;
import com.ecommerce.user.model.RegisterUserRequest;
import com.ecommerce.user.model.UpdateUserRequest;
import com.ecommerce.user.model.UserResponse;
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

        if (userRepository.existsById(request.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(BCrypt.hash(request.getPassword()));
        user.setName(request.getName());

        userRepository.save(user);
    }

    public UserResponse get(User user) {
        return UserResponse.builder()
                .username(user.getUsername())
                .name(user.getName())
                .build();
    }

    @Transactional
    public UserResponse update(User user, UpdateUserRequest request) {
        validationService.validate(request);

        if (java.util.Objects.nonNull(request.getName())) {
            user.setName(request.getName());
        }

        if (java.util.Objects.nonNull(request.getPassword())) {
            user.setPassword(BCrypt.hash(request.getPassword()));
        }

        userRepository.save(user);

        return UserResponse.builder()
                .name(user.getName())
                .username(user.getUsername())
                .build();
    }
}
