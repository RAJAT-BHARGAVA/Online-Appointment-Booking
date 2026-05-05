package com.bridgelabz.recordservice.exception;

public class DuplicateMedicalRecordException extends RuntimeException {
    public DuplicateMedicalRecordException(String message) {
        super(message);
    }
}