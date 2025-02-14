package cz.wz.marysidy.spring_genesis_resources.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String entityName, String identifier) {
        super(entityName + " with " + identifier + " not found.");
    }
}
