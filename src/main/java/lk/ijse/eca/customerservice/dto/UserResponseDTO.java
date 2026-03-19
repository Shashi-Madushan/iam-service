package lk.ijse.eca.customerservice.dto;

import lk.ijse.eca.customerservice.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {

    private String userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private User.UserType userType;
    private User.UserStatus status;
    private String phone;
    private String address;
    private String profilePicture;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
