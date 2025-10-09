package com.example.growingstudy.exception;

public class UserAlreadyExistsException extends RegisterFailException {

    public UserAlreadyExistsException() {
        super();
    }

    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
