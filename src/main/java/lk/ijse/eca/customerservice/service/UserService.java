package lk.ijse.eca.customerservice.service;

import lk.ijse.eca.customerservice.dto.UserRequestDTO;
import lk.ijse.eca.customerservice.dto.UserResponseDTO;
import lk.ijse.eca.customerservice.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    UserResponseDTO createUser(UserRequestDTO dto);

    UserResponseDTO getUser(String userId);

    UserResponseDTO getUserByUsername(String username);

    UserResponseDTO updateUser(String userId, UserRequestDTO dto);

    void deleteUser(String userId);

    List<UserResponseDTO> getAllUsers();

    List<UserResponseDTO> getUsersByType(User.UserType userType);

    List<UserResponseDTO> getUsersByStatus(User.UserStatus status);

    List<UserResponseDTO> searchUsersByName(String name);

    List<UserResponseDTO> getActiveUsersSince(java.time.LocalDateTime startDate);

    Page<UserResponseDTO> getUsersByTypePaginated(User.UserType userType, Pageable pageable);

    Page<UserResponseDTO> searchUsersByTypeAndKeyword(User.UserType userType, String keyword, Pageable pageable);

    void updateUserStatus(String userId, User.UserStatus status);

    void recordUserLogin(String userId);

    Long getUserCountByType(User.UserType userType);

    Long getUserCountByStatus(User.UserStatus status);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    void changePassword(String userId, String oldPassword, String newPassword);
}
