package lk.ijse.eca.iamservice.service;

import lk.ijse.eca.iamservice.dto.UserRequestDTO;
import lk.ijse.eca.iamservice.dto.UserResponseDTO;
import lk.ijse.eca.iamservice.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    UserResponseDTO createUser(UserRequestDTO dto);

    UserResponseDTO getUser(Long id);

    UserResponseDTO getUserByUsername(String username);

    UserResponseDTO updateUser(Long id, UserRequestDTO dto);

    void deleteUser(Long id);

    List<UserResponseDTO> getAllUsers();

    List<UserResponseDTO> getUsersByType(User.UserType userType);

    List<UserResponseDTO> getUsersByStatus(User.UserStatus status);

    List<UserResponseDTO> searchUsersByName(String name);

    List<UserResponseDTO> getActiveUsersSince(java.time.LocalDateTime startDate);

    Page<UserResponseDTO> getUsersByTypePaginated(User.UserType userType, Pageable pageable);

    Page<UserResponseDTO> searchUsersByTypeAndKeyword(User.UserType userType, String keyword, Pageable pageable);

    void updateUserStatus(Long id, User.UserStatus status);

    void recordUserLogin(Long id);

    Long getUserCountByType(User.UserType userType);

    Long getUserCountByStatus(User.UserStatus status);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    void changePassword(Long id, String oldPassword, String newPassword);
}
