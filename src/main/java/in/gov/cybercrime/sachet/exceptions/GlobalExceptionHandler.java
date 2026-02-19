package in.gov.cybercrime.sachet.exceptions;

import in.gov.cybercrime.sachet.dto.GenericResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Purpose: Handle custom not found exceptions
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<GenericResponse<Object>> handleNotFound(ResourceNotFoundException ex) {
        return new ResponseEntity<>(
                GenericResponse.fail(ex.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }

    // Purpose: Handle bad request exceptions
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<GenericResponse<Object>> handleBadRequest(IllegalArgumentException ex) {
        return new ResponseEntity<>(
                GenericResponse.fail(ex.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    // Purpose: Handle validation errors (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GenericResponse<Object>> handleValidation(MethodArgumentNotValidException ex) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + " : " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return new ResponseEntity<>(
                GenericResponse.fail(message),
                HttpStatus.BAD_REQUEST
        );
    }

    // Purpose: Handle constraint violations
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<GenericResponse<Object>> handleConstraintViolation(ConstraintViolationException ex) {

        String message = ex.getConstraintViolations()
                .stream()
                .map(v -> v.getPropertyPath() + " : " + v.getMessage())
                .collect(Collectors.joining(", "));

        return new ResponseEntity<>(
                GenericResponse.fail(message),
                HttpStatus.BAD_REQUEST
        );
    }

    // Purpose: Handle database integrity violations
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<GenericResponse<Object>> handleDataIntegrity(DataIntegrityViolationException ex) {
        return new ResponseEntity<>(
                GenericResponse.fail("Database constraint violation"),
                HttpStatus.BAD_REQUEST
        );
    }

    // Purpose: Handle access denied
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<GenericResponse<Object>> handleAccessDenied(AccessDeniedException ex) {
        return new ResponseEntity<>(
                GenericResponse.fail("Access denied"),
                HttpStatus.FORBIDDEN
        );
    }

    // Purpose: Handle unexpected errors
    @ExceptionHandler(Exception.class)
    public ResponseEntity<GenericResponse<Object>> handleGlobal(Exception ex) {

        ex.printStackTrace();

        return new ResponseEntity<>(
                GenericResponse.fail("Internal server error"),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
