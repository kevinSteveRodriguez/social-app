package com.redsocial.app.exception;

/**
 * Excepción específica para errores relacionados con posts.
 */
public class PostException extends RuntimeException {
    
    public PostException(String message) {
        super(message);
    }
    
    public PostException(String message, Throwable cause) {
        super(message, cause);
    }
}
