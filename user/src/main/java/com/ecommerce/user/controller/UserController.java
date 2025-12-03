package com.ecommerce.user.controller;

import com.ecommerce.user.model.RegisterUserRequest;
import com.ecommerce.user.model.UpdateUserRequest;
import com.ecommerce.user.model.WebResponse;
import com.ecommerce.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping(path = "/api/users", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<String> register(@RequestBody RegisterUserRequest request) {
        userService.register(request);
        return WebResponse.<String>builder().data("success").build();
    }

    @org.springframework.web.bind.annotation.GetMapping(path = "/api/users/current", produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<com.ecommerce.user.model.UserResponse> get(com.ecommerce.user.entity.User user) {
        if (user == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        com.ecommerce.user.model.UserResponse userResponse = userService.get(user);
        return WebResponse.<com.ecommerce.user.model.UserResponse>builder().data(userResponse).build();
    }

    @org.springframework.web.bind.annotation.PatchMapping(path = "/api/users/current", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<com.ecommerce.user.model.UserResponse> update(com.ecommerce.user.entity.User user,
            @RequestBody UpdateUserRequest request) {
        if (user == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        com.ecommerce.user.model.UserResponse userResponse = userService.update(user, request);
        return WebResponse.<com.ecommerce.user.model.UserResponse>builder().data(userResponse).build();
    }
}
