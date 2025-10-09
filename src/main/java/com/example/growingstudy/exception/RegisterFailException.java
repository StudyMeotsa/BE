package com.example.growingstudy.exception;

public class RegisterFailException extends RuntimeException {

    public RegisterFailException() {
        super();
    }

    public RegisterFailException(String message) {
        super(message);
    }
}
