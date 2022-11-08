package com.osa.stocky.util;

/**
 * Exception for Stocky project with error codes.
 * 
 * @author oleksii
 * @since 6 Nov 2022
 */
public class StockyException extends RuntimeException {
    
    public StockyException() {
        super();
    }

    public StockyException(String message) {
        super(message);
    }
    
    public StockyException(Throwable throwable) {
        super(throwable);
    }
    
    public StockyException(String message, Throwable throwable) {
        super(message, throwable);
    }
    
    
}
