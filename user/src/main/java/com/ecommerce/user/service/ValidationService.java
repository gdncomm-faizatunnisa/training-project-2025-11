package com.ecommerce.user.service;

import com.ecommerce.user.model.LoginUserRequest;
import com.ecommerce.user.model.RegisterUserRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.stereotype.Service;

import java.util.Set;

//@Service
//public class ValidationService {
//    private Validator validator;
//    public void validate(Object request) {
//        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(request);
//        if (constraintViolations.size() != 0) {
//            //error
//            throw new ConstraintViolationException(constraintViolations);
//        }
//    }
//}

@Service
public class ValidationService {

    private final Validator validator;

    public ValidationService(Validator validator) {
        this.validator = validator;
    }

    public void validate(Object request) {
        var violations = validator.validate(request);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}

