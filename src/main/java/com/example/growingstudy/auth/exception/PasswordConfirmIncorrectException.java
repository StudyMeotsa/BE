package com.example.growingstudy.auth.exception;

public class PasswordConfirmIncorrectException extends RegisterFailException {

    public PasswordConfirmIncorrectException() {
        super();
    }

    public PasswordConfirmIncorrectException(String message) {
        super(message);
    }
}
