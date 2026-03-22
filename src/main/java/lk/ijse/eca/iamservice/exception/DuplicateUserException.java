package lk.ijse.eca.iamservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateUserException extends RuntimeException {
    
    public DuplicateUserException(String username) {
        super("User already exists with username: " + username);
    }
    
    public DuplicateUserException(String field, String value) {
        super("User already exists with " + field + ": " + value);
    }
}
