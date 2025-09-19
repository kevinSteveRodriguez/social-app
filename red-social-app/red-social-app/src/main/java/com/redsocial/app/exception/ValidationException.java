package com.redsocial.app.exception;

/**
 * Excepción lanzada cuando los datos de entrada no son válidos.
 */
public class ValidationException extends RuntimeException {
    
    public ValidationException(String message) {
        super(message);
    }
    
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
