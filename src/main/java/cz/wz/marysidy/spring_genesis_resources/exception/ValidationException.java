package cz.wz.marysidy.spring_genesis_resources.exception;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
