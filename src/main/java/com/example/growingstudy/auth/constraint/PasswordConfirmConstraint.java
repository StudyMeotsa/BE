package com.example.growingstudy.auth.constraint;

import com.example.growingstudy.auth.validator.PasswordConfirmConstraintValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

// 비밀번호 확인이 비밀번호와 일치해야 함
@Constraint(validatedBy = {PasswordConfirmConstraintValidator.class})
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordConfirmConstraint {

    String message () default "비밀번호와 비밀번호 확인이 일치하지 않습니다";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
