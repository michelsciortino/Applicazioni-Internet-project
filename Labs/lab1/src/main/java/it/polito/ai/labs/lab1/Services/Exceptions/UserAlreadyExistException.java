package it.polito.ai.labs.lab1.Services.Exceptions;

public class UserAlreadyExistException extends ServiceException {
    @Override
    public String getMessage() {
        return "The user already exists.";
    }
}
