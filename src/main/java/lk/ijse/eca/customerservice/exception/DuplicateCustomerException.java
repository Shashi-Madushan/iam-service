package lk.ijse.eca.customerservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateCustomerException extends RuntimeException {
    
    public DuplicateCustomerException(String customerId) {
        super("Customer already exists with ID: " + customerId);
    }
}
