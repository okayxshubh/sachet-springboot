package in.gov.cybercrime.sachet.exceptions;

// Purpose: Used when entity is not found
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
