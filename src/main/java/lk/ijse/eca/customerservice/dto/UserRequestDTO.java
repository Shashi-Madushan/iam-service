package lk.ijse.eca.customerservice.dto;

import jakarta.validation.constraints.*;
import lk.ijse.eca.customerservice.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestDTO {

    public interface OnCreate {}

    public interface OnUpdate {}

    @NotBlank(groups = OnCreate.class, message = "User ID is required")
    @Pattern(groups = OnCreate.class, regexp = "^[A-Z0-9]{6,10}$", message = "User ID must be 6-10 alphanumeric characters")
    private String userId;

    @NotBlank(groups = OnCreate.class, message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers, and underscores")
    private String username;

    @NotBlank(groups = OnCreate.class, message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;

    @NotNull(message = "User type is required")
    private User.UserType userType;

    @NotNull(message = "User status is required")
    private User.UserStatus status;

    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Phone number must be valid")
    private String phone;

    @Size(max = 200, message = "Address must not exceed 200 characters")
    private String address;

    private String profilePicture;
}
