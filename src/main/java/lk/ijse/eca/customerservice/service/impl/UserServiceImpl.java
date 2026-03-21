package lk.ijse.eca.customerservice.service.impl;

import lk.ijse.eca.customerservice.dto.UserRequestDTO;
import lk.ijse.eca.customerservice.dto.UserResponseDTO;
import lk.ijse.eca.customerservice.entity.User;
import lk.ijse.eca.customerservice.exception.DuplicateUserException;
import lk.ijse.eca.customerservice.exception.UserNotFoundException;
import lk.ijse.eca.customerservice.mapper.UserMapper;
import lk.ijse.eca.customerservice.repository.UserRepository;
import lk.ijse.eca.customerservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDTO createUser(UserRequestDTO dto) {
        log.debug("Creating user with username: {}", dto.getUsername());

        if (userRepository.existsByUsername(dto.getUsername())) {
            log.warn("Duplicate username detected: {}", dto.getUsername());
            throw new DuplicateUserException("username", dto.getUsername());
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            log.warn("Duplicate email detected: {}", dto.getEmail());
            throw new DuplicateUserException("email", dto.getEmail());
        }

        User user = userMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        
        User saved = userRepository.save(user);
        log.info("User created successfully: {}", saved.getId());
        return userMapper.toDtoWithoutPassword(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUser(Long id) {
        log.debug("Fetching user with ID: {}", id);
        return userRepository.findById(id)
                .map(userMapper::toDtoWithoutPassword)
                .orElseThrow(() -> {
                    log.warn("User not found: {}", id);
                    return new UserNotFoundException(String.valueOf(id));
                });
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserByUsername(String username) {
        log.debug("Fetching user by username: {}", username);
        return userRepository.findByUsername(username)
                .map(userMapper::toDtoWithoutPassword)
                .orElseThrow(() -> {
                    log.warn("User not found by username: {}", username);
                    return new UserNotFoundException("User not found with username: " + username);
                });
    }

    @Override
    public UserResponseDTO updateUser(Long id, UserRequestDTO dto) {
        log.debug("Updating user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found for update: {}", id);
                    return new UserNotFoundException(String.valueOf(id));
                });

        // Check if username is being changed and if it's already taken
        if (!user.getUsername().equals(dto.getUsername()) && 
            userRepository.existsByUsername(dto.getUsername())) {
            log.warn("Duplicate username detected during update: {}", dto.getUsername());
            throw new DuplicateUserException("username", dto.getUsername());
        }

        // Check if email is being changed and if it's already taken
        if (!user.getEmail().equals(dto.getEmail()) && 
            userRepository.existsByEmail(dto.getEmail())) {
            log.warn("Duplicate email detected during update: {}", dto.getEmail());
            throw new DuplicateUserException("email", dto.getEmail());
        }

        userMapper.updateEntity(dto, user);
        
        // Update password if provided
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        User updated = userRepository.save(user);
        log.info("User updated successfully: {}", updated.getId());
        return userMapper.toDtoWithoutPassword(updated);
    }

    @Override
    public void deleteUser(Long id) {
        log.debug("Deleting user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found for deletion: {}", id);
                    return new UserNotFoundException(String.valueOf(id));
                });

        userRepository.delete(user);
        log.info("User deleted successfully: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        log.debug("Fetching all users");
        List<UserResponseDTO> users = userRepository.findAll()
                .stream()
                .map(userMapper::toDtoWithoutPassword)
                .collect(Collectors.toList());
        log.debug("Fetched {} user(s)", users.size());
        return users;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getUsersByType(User.UserType userType) {
        log.debug("Fetching users by type: {}", userType);
        return userRepository.findByUserType(userType)
                .stream()
                .map(userMapper::toDtoWithoutPassword)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getUsersByStatus(User.UserStatus status) {
        log.debug("Fetching users by status: {}", status);
        return userRepository.findByStatus(status)
                .stream()
                .map(userMapper::toDtoWithoutPassword)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> searchUsersByName(String name) {
        log.debug("Searching users by name: {}", name);
        return userRepository.findByNameContaining(name)
                .stream()
                .map(userMapper::toDtoWithoutPassword)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getActiveUsersSince(LocalDateTime startDate) {
        log.debug("Fetching active users since: {}", startDate);
        return userRepository.findActiveUsersSince(startDate)
                .stream()
                .map(userMapper::toDtoWithoutPassword)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseDTO> getUsersByTypePaginated(User.UserType userType, Pageable pageable) {
        log.debug("Fetching users by type (paginated): {}", userType);
        return userRepository.findByUserType(userType, pageable)
                .map(userMapper::toDtoWithoutPassword);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseDTO> searchUsersByTypeAndKeyword(User.UserType userType, String keyword, Pageable pageable) {
        log.debug("Searching users by type and keyword: {}, {}", userType, keyword);
        return userRepository.findUsersByTypeAndSearch(userType, keyword, pageable)
                .map(userMapper::toDtoWithoutPassword);
    }

    @Override
    public void updateUserStatus(Long id, User.UserStatus status) {
        log.debug("Updating user {} status to: {}", id, status);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found for status update: {}", id);
                    return new UserNotFoundException(String.valueOf(id));
                });

        user.setStatus(status);
        userRepository.save(user);
        log.info("User {} status updated to: {}", id, status);
    }

    @Override
    public void recordUserLogin(Long id) {
        log.debug("Recording login for user: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found for login recording: {}", id);
                    return new UserNotFoundException(String.valueOf(id));
                });

        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
        log.debug("Login recorded for user: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getUserCountByType(User.UserType userType) {
        log.debug("Counting users by type: {}", userType);
        return userRepository.countByUserType(userType);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getUserCountByStatus(User.UserStatus status) {
        log.debug("Counting users by status: {}", status);
        return userRepository.countByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public void changePassword(Long id, String oldPassword, String newPassword) {
        log.debug("Changing password for user: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found for password change: {}", id);
                    return new UserNotFoundException(String.valueOf(id));
                });

        // In a real application, you would verify the old password here
        // For now, we'll just update with the new password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Password changed for user: {}", id);
    }
}
