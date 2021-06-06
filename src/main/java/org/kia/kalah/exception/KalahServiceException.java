package org.kia.kalah.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Handling Kalah Service business exceptions
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class KalahServiceException extends Exception{
    public KalahServiceException(String message) {
        super(message);
    }
}
