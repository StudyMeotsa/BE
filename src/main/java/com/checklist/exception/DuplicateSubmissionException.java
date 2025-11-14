package com.checklist.exception;

public class DuplicateSubmissionException extends IllegalStateException {
    public DuplicateSubmissionException(String message) {
        super(message);
    }
}