package lk.ijse.eca.iamservice.service;

import lk.ijse.eca.iamservice.dto.CustomerJsonRequestDTO;
import lk.ijse.eca.iamservice.dto.CustomerRequestDTO;
import lk.ijse.eca.iamservice.dto.CustomerResponseDTO;
import lk.ijse.eca.iamservice.entity.Customer;

import java.util.List;

public interface CustomerService {

    CustomerResponseDTO createCustomer(CustomerRequestDTO dto);

    CustomerResponseDTO createCustomerFromJson(CustomerJsonRequestDTO dto);

    CustomerResponseDTO updateCustomer(String customerId, CustomerRequestDTO dto);

    CustomerResponseDTO updateCustomerFromJson(String customerId, CustomerJsonRequestDTO dto);

    void deleteCustomer(String customerId);

    CustomerResponseDTO getCustomer(String customerId);

    List<CustomerResponseDTO> getAllCustomers();

    List<CustomerResponseDTO> getCustomersByType(Customer.CustomerType customerType);

    List<CustomerResponseDTO> getTopLoyaltyCustomers(Integer minPoints);

    byte[] getCustomerPicture(String customerId);

    void updateLoyaltyPoints(String customerId, Integer pointsToAdd);

    void updateTotalPurchases(String customerId, Double purchaseAmount);
}
