package cz.wz.marysidy.spring_genesis_resources.exception;

import java.time.LocalDateTime;
import java.util.Map;

public class ErrorResponse {
    private final String message;
    private final String errorCode;
    private final LocalDateTime timestamp;
    private final String path;
    private final Map<String, Object> details;

    private ErrorResponse(Builder builder) {
        this.message = builder.message;
        this.errorCode = builder.errorCode;
        this.timestamp = builder.timestamp;
        this.path = builder.path;
        this.details = builder.details;
    }

    public String getMessage() { return message; }
    public String getErrorCode() { return errorCode; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getPath() { return path; }
    public Map<String, Object> getDetails() { return details; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String message;
        private String errorCode;
        private LocalDateTime timestamp;
        private String path;
        private Map<String, Object> details;

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder errorCode(String errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public Builder details(Map<String, Object> details) {
            this.details = details;
            return this;
        }

        public ErrorResponse build() {
            return new ErrorResponse(this);
        }
    }
}
