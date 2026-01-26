package be.lpbconsult.befintax.exception;

import org.springframework.stereotype.Service;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
