package lk.ijse.eca.customerservice.service;

import lk.ijse.eca.customerservice.dto.CustomerRequestDTO;
import lk.ijse.eca.customerservice.dto.CustomerResponseDTO;
import lk.ijse.eca.customerservice.entity.Customer;

import java.util.List;

public interface CustomerService {

    CustomerResponseDTO createCustomer(CustomerRequestDTO dto);

    CustomerResponseDTO updateCustomer(String customerId, CustomerRequestDTO dto);

    void deleteCustomer(String customerId);

    CustomerResponseDTO getCustomer(String customerId);

    List<CustomerResponseDTO> getAllCustomers();

    List<CustomerResponseDTO> getCustomersByType(Customer.CustomerType customerType);

    List<CustomerResponseDTO> getTopLoyaltyCustomers(Integer minPoints);

    byte[] getCustomerPicture(String customerId);

    void updateLoyaltyPoints(String customerId, Integer pointsToAdd);

    void updateTotalPurchases(String customerId, Double purchaseAmount);
}
