package com.redsocial.app.exception;

/**
 * Excepción específica para errores relacionados con perfiles de usuario.
 */
public class UserProfileException extends RuntimeException {
    
    public UserProfileException(String message) {
        super(message);
    }
    
    public UserProfileException(String message, Throwable cause) {
        super(message, cause);
    }
}
