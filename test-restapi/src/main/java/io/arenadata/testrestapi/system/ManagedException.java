package io.arenadata.testrestapi.system;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ManagedException extends Exception {
    private static final Logger logger = LoggerFactory.getLogger(ManagedException.class);
    private final ManagedExceptionType type;
    private final String message;

    public ManagedException(ManagedExceptionType type, String message) {
        super(message);
        this.type    = type;
        this.message = message;
        logger.warn("Handling the managed exception type of {}, its message: {}", this.type, this.message);
    }
}
