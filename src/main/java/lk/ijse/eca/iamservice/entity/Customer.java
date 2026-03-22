package lk.ijse.eca.iamservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id
    @Column(name = "customer_id", nullable = false, length = 10, unique = true)
    private String customerId;

    @PrePersist
    public void prePersist() {
        if (this.customerId == null || this.customerId.isEmpty()) {
            this.customerId = generateCustomerId();
        }
    }

    private String generateCustomerId() {
        // Generate 8-character alphanumeric uppercase ID
        String uuid = UUID.randomUUID().toString().toUpperCase().replaceAll("[^A-Z0-9]", "");
        return uuid.substring(0, 8);
    }

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "mobile", nullable = false)
    private String mobile;

    @Column(name = "email")
    private String email;

    @Column(name = "picture", nullable = false)
    private String picture;

    @Enumerated(EnumType.STRING)
    @Column(name = "customer_type", nullable = false)
    private CustomerType customerType;

    @Column(name = "loyalty_points", nullable = false)
    private Integer loyaltyPoints;

    @Column(name = "preferred_payment_method")
    private String preferredPaymentMethod;

    @Column(name = "total_purchases", nullable = false)
    private Double totalPurchases;

    public enum CustomerType {
        REGULAR,
        PREMIUM,
        VIP
    }
}
