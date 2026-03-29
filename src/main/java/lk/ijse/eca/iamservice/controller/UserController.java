package lk.ijse.eca.iamservice.controller;

import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import lk.ijse.eca.iamservice.dto.AdminLoginRequestDTO;
import lk.ijse.eca.iamservice.dto.UserRequestDTO;
import lk.ijse.eca.iamservice.dto.UserResponseDTO;
import lk.ijse.eca.iamservice.entity.User;
import lk.ijse.eca.iamservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserService userService;

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UserResponseDTO> createUser(
            @Validated({Default.class, UserRequestDTO.OnCreate.class}) @RequestBody UserRequestDTO dto) {
        log.info("POST /api/v1/users - username: {}", dto.getUsername());
        UserResponseDTO response = userService.createUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping(
            value = "/admin/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UserResponseDTO> adminLogin(@Valid @RequestBody AdminLoginRequestDTO dto) {
        log.info("POST /api/v1/users/admin/login - username: {}", dto.getUsername());
        UserResponseDTO response = userService.authenticateAdmin(dto.getUsername(), dto.getPassword());
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponseDTO> getUser(
            @PathVariable String id) {
        log.info("GET /api/v1/users/{}", id);
        
        // Validate that ID is numeric
        try {
            Long userId = Long.parseLong(id);
            UserResponseDTO response = userService.getUser(userId);
            return ResponseEntity.ok(response);
        } catch (NumberFormatException e) {
            log.warn("Invalid user ID format: {}", id);
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }
    }

    @GetMapping(value = "/username/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponseDTO> getUserByUsername(
            @PathVariable String username) {
        log.info("GET /api/v1/users/username/{}", username);
        UserResponseDTO response = userService.getUserByUsername(username);
        return ResponseEntity.ok(response);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<java.util.List<UserResponseDTO>> getAllUsers() {
        log.info("GET /api/v1/users - retrieving all users");
        java.util.List<UserResponseDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping(
            value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable String id,
            @Valid @RequestBody UserRequestDTO dto) {
        log.info("PUT /api/v1/users/{}", id);
        
        // Validate that ID is numeric
        try {
            Long userId = Long.parseLong(id);
            UserResponseDTO response = userService.updateUser(userId, dto);
            return ResponseEntity.ok(response);
        } catch (NumberFormatException e) {
            log.warn("Invalid user ID format: {}", id);
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(
            @PathVariable String id) {
        log.info("DELETE /api/v1/users/{}", id);
        
        // Validate that ID is numeric
        try {
            Long userId = Long.parseLong(id);
            userService.deleteUser(userId);
            return ResponseEntity.ok("Delete success");
        } catch (NumberFormatException e) {
            log.warn("Invalid user ID format: {}", id);
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Invalid user ID format");
        }
    }

    @GetMapping(value = "/type/{userType}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<java.util.List<UserResponseDTO>> getUsersByType(
            @PathVariable User.UserType userType) {
        log.info("GET /api/v1/users/type/{}", userType);
        java.util.List<UserResponseDTO> users = userService.getUsersByType(userType);
        return ResponseEntity.ok(users);
    }

    @GetMapping(value = "/status/{status}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<java.util.List<UserResponseDTO>> getUsersByStatus(
            @PathVariable User.UserStatus status) {
        log.info("GET /api/v1/users/status/{}", status);
        java.util.List<UserResponseDTO> users = userService.getUsersByStatus(status);
        return ResponseEntity.ok(users);
    }

    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<java.util.List<UserResponseDTO>> searchUsersByName(
            @RequestParam String name) {
        log.info("GET /api/v1/users/search?name={}", name);
        java.util.List<UserResponseDTO> users = userService.searchUsersByName(name);
        return ResponseEntity.ok(users);
    }

    @GetMapping(value = "/active-since", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<java.util.List<UserResponseDTO>> getActiveUsersSince(@RequestParam(required = false) LocalDateTime startDate) {
        if (startDate == null) {
            startDate = LocalDateTime.now().minusDays(30); // Default to last 30 days
        }
        log.info("GET /api/v1/users/active-since?startDate={}", startDate);
        java.util.List<UserResponseDTO> users = userService.getActiveUsersSince(startDate);
        return ResponseEntity.ok(users);
    }

    @GetMapping(value = "/paginated/type/{userType}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<UserResponseDTO>> getUsersByTypePaginated(
            @PathVariable User.UserType userType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        log.info("GET /api/v1/users/paginated/type/{}?page={}&size={}", userType, page, size);
        
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<UserResponseDTO> users = userService.getUsersByTypePaginated(userType, pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping(value = "/search/type/{userType}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<UserResponseDTO>> searchUsersByTypeAndKeyword(
            @PathVariable User.UserType userType,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("GET /api/v1/users/search/type/{}?keyword={}&page={}&size={}", userType, keyword, page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<UserResponseDTO> users = userService.searchUsersByTypeAndKeyword(userType, keyword, pageable);
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<String> updateUserStatus(
            @PathVariable String id,
            @RequestParam User.UserStatus status) {
        log.info("PUT /api/v1/users/{}/status?status={}", id, status);
        
        // Validate that ID is numeric
        try {
            Long userId = Long.parseLong(id);
            userService.updateUserStatus(userId, status);
            return ResponseEntity.ok("User status updated successfully");
        } catch (NumberFormatException e) {
            log.warn("Invalid user ID format: {}", id);
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Invalid user ID format");
        }
    }

    @PutMapping("/{id}/login")
    public ResponseEntity<String> recordUserLogin(
            @PathVariable String id) {
        log.info("PUT /api/v1/users/{}/login", id);
        
        // Validate that ID is numeric
        try {
            Long userId = Long.parseLong(id);
            userService.recordUserLogin(userId);
            return ResponseEntity.ok("User login recorded successfully");
        } catch (NumberFormatException e) {
            log.warn("Invalid user ID format: {}", id);
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Invalid user ID format");
        }
    }

    @GetMapping(value = "/stats/type/{userType}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> getUserCountByType(
            @PathVariable User.UserType userType) {
        log.info("GET /api/v1/users/stats/type/{}", userType);
        Long count = userService.getUserCountByType(userType);
        return ResponseEntity.ok(count);
    }

    @GetMapping(value = "/stats/status/{status}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> getUserCountByStatus(
            @PathVariable User.UserStatus status) {
        log.info("GET /api/v1/users/stats/status/{}", status);
        Long count = userService.getUserCountByStatus(status);
        return ResponseEntity.ok(count);
    }

    @GetMapping(value = "/exists/username/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> existsByUsername(
            @PathVariable String username) {
        log.info("GET /api/v1/users/exists/username/{}", username);
        boolean exists = userService.existsByUsername(username);
        return ResponseEntity.ok(exists);
    }

    @GetMapping(value = "/exists/email/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> existsByEmail(
            @PathVariable String email) {
        log.info("GET /api/v1/users/exists/email/{}", email);
        boolean exists = userService.existsByEmail(email);
        return ResponseEntity.ok(exists);
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<String> changePassword(
            @PathVariable String id,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        log.info("PUT /api/v1/users/{}/password", id);
        
        // Validate that ID is numeric
        try {
            Long userId = Long.parseLong(id);
            userService.changePassword(userId, oldPassword, newPassword);
            return ResponseEntity.ok("Password changed successfully");
        } catch (NumberFormatException e) {
            log.warn("Invalid user ID format: {}", id);
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Invalid user ID format");
        }
    }
}
