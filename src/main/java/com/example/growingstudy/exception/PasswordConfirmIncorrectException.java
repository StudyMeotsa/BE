package com.example.growingstudy.exception;

public class PasswordConfirmIncorrectException extends RegisterFailException {

    public PasswordConfirmIncorrectException() {
        super();
    }

    public PasswordConfirmIncorrectException(String message) {
        super(message);
    }
}
