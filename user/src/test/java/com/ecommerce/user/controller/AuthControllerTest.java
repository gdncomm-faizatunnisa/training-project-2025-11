package com.ecommerce.user.controller;

import com.ecommerce.user.model.LoginUserRequest;
import com.ecommerce.user.model.TokenResponse;
import com.ecommerce.user.model.WebResponse;
import com.ecommerce.user.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import jakarta.validation.Validator;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.MockMvcBuilder.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {
        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private UserRepository userRepository;

        @BeforeEach
        void setUp() {
                userRepository.deleteAll();
        }

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        void loginSuccess() throws Exception {
                com.ecommerce.user.entity.User user = new com.ecommerce.user.entity.User();
                user.setName("Test");
                user.setUsername("test");
                user.setPassword(org.springframework.security.crypto.bcrypt.BCrypt.hashpw("test",
                                org.springframework.security.crypto.bcrypt.BCrypt.gensalt()));
                userRepository.save(user);

                LoginUserRequest request = new LoginUserRequest();
                request.setUsername("test");
                request.setPassword("test");

                mockMvc.perform(
                                post("/api/auth/login")
                                                .accept(MediaType.APPLICATION_JSON)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request)))
                                .andExpectAll(
                                                status().isOk())
                                .andDo(result -> {
                                        WebResponse<TokenResponse> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<>() {
                                                        });
                                        assertNull(response.getErrors());
                                        assertNotNull(response.getData().getToken());
                                        assertNotNull(response.getData().getExpiredAt());
                                });
        }

        @Test
        void loginFailedWrongPassword() throws Exception {
                com.ecommerce.user.entity.User user = new com.ecommerce.user.entity.User();
                user.setName("Test");
                user.setUsername("test");
                user.setPassword(org.springframework.security.crypto.bcrypt.BCrypt.hashpw("test",
                                org.springframework.security.crypto.bcrypt.BCrypt.gensalt()));
                userRepository.save(user);

                LoginUserRequest request = new LoginUserRequest();
                request.setUsername("test");
                request.setPassword("wrong");

                mockMvc.perform(
                                post("/api/auth/login")
                                                .accept(MediaType.APPLICATION_JSON)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request)))
                                .andExpectAll(
                                                status().isUnauthorized())
                                .andDo(result -> {
                                        WebResponse<String> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<>() {
                                                        });
                                        assertNotNull(response.getErrors());
                                        assertEquals("Wrong Password", response.getErrors());
                                });
        }

        @Test
        void loginFailedUserNotFound() throws Exception {
                LoginUserRequest request = new LoginUserRequest();
                request.setUsername("test");
                request.setPassword("test");

                mockMvc.perform(
                                post("/api/auth/login")
                                                .accept(MediaType.APPLICATION_JSON)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request)))
                                .andExpectAll(
                                                status().isUnauthorized())
                                .andDo(result -> {
                                        WebResponse<String> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<>() {
                                                        });
                                        assertNotNull(response.getErrors());
                                });
        }

}