package com.bridgelabz.scheduleservice.exception;

public class SlotNotFoundException extends RuntimeException {
    public SlotNotFoundException(String message) {
        super(message);
    }
}