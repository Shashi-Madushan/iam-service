package lk.ijse.eca.iamservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Min;
import lk.ijse.eca.iamservice.entity.Customer;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerJsonRequestDTO {

    public interface OnCreate {}

    private String customerId;

    @NotBlank(message = "Name is required")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z ]*$", message = "Name can only contain letters and spaces")
    private String name;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Mobile is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile must be 10 digits")
    private String mobile;

    @Email(message = "Invalid email format")
    private String email;

    private String picture;

    @NotNull(message = "Customer type is required")
    private Customer.CustomerType customerType;

    @Min(value = 0, message = "Loyalty points cannot be negative")
    private Integer loyaltyPoints = 0;

    private String preferredPaymentMethod;

    @Min(value = 0, message = "Total purchases cannot be negative")
    private Double totalPurchases = 0.0;
}
