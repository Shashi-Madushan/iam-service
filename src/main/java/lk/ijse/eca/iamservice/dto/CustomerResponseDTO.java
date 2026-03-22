package lk.ijse.eca.iamservice.dto;

import lk.ijse.eca.iamservice.entity.Customer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class CustomerResponseDTO {

    private String customerId;
    private String name;
    private String address;
    private String mobile;
    private String email;
    private String picture;
    private Customer.CustomerType customerType;
    private Integer loyaltyPoints;
    private String preferredPaymentMethod;
    private Double totalPurchases;
}
