package lk.ijse.eca.customerservice.repository;

import lk.ijse.eca.customerservice.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    List<User> findByUserType(User.UserType userType);

    List<User> findByStatus(User.UserStatus status);

    List<User> findByUserTypeAndStatus(User.UserType userType, User.UserStatus status);

    @Query("SELECT u FROM User u WHERE u.firstName LIKE %:name% OR u.lastName LIKE %:name%")
    List<User> findByNameContaining(@Param("name") String name);

    @Query("SELECT u FROM User u WHERE u.lastLogin >= :startDate")
    List<User> findActiveUsersSince(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT COUNT(u) FROM User u WHERE u.userType = :userType")
    Long countByUserType(@Param("userType") User.UserType userType);

    @Query("SELECT COUNT(u) FROM User u WHERE u.status = :status")
    Long countByStatus(@Param("status") User.UserStatus status);

    Page<User> findByUserType(User.UserType userType, Pageable pageable);

    Page<User> findByStatus(User.UserStatus status, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.userType = :userType AND (u.firstName LIKE %:search% OR u.lastName LIKE %:search% OR u.email LIKE %:search%)")
    Page<User> findUsersByTypeAndSearch(@Param("userType") User.UserType userType, @Param("search") String search, Pageable pageable);
}
