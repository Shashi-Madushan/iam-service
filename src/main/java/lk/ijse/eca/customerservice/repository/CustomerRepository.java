package lk.ijse.eca.customerservice.repository;

import lk.ijse.eca.customerservice.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {

    List<Customer> findByCustomerType(Customer.CustomerType customerType);

    @Query("SELECT c FROM Customer c WHERE c.loyaltyPoints > :minPoints ORDER BY c.loyaltyPoints DESC")
    List<Customer> findTopLoyaltyCustomers(@Param("minPoints") Integer minPoints);

    @Query("SELECT COUNT(c) FROM Customer c WHERE c.customerType = :customerType")
    Long countByCustomerType(@Param("customerType") Customer.CustomerType customerType);
}
