package com.bridgelabz.providerservice.exception;

public class ProviderAlreadyExistsException extends RuntimeException {
    public ProviderAlreadyExistsException(String message) {
        super(message);
    }
}