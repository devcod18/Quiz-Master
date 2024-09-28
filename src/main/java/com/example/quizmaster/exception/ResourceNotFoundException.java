package com.example.quizmaster.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException (String message) {
        super(message);
    }

}
