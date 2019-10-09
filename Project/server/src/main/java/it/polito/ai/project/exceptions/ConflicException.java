package it.polito.ai.project.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(value = HttpStatus.CONFLICT)
public class ConflicException extends RuntimeException {
    public ConflicException() {
        super();
    }

    public ConflicException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConflicException(String message) {
        super(message);
    }

    public ConflicException(Throwable cause) {
        super(cause);
    }
}

