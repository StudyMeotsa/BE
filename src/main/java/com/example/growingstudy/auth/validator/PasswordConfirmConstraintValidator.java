package com.example.growingstudy.auth.validator;

import com.example.growingstudy.auth.constraint.PasswordConfirmConstraint;
import com.example.growingstudy.auth.dto.RegisterRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordConfirmConstraintValidator implements ConstraintValidator<PasswordConfirmConstraint, RegisterRequestDto> {

    @Override
    public boolean isValid(RegisterRequestDto value, ConstraintValidatorContext context) {
        return value.getPasswordConfirm() != null && value.getPasswordConfirm().equals(value.getPassword());
    }
}
