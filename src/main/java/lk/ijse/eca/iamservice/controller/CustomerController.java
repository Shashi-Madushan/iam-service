package lk.ijse.eca.iamservice.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Min;
import jakarta.validation.groups.Default;
import lk.ijse.eca.iamservice.dto.CustomerRequestDTO;
import lk.ijse.eca.iamservice.dto.CustomerResponseDTO;
import lk.ijse.eca.iamservice.entity.Customer;
import lk.ijse.eca.iamservice.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import lk.ijse.eca.iamservice.dto.CustomerJsonRequestDTO;


@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CustomerController {

    private final CustomerService customerService;

    private static final String CUSTOMER_ID_REGEXP = "^[A-Z0-9]{6,10}$";

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CustomerResponseDTO> createCustomerWithPicture(
            @Validated({Default.class, CustomerRequestDTO.OnCreate.class}) @ModelAttribute CustomerRequestDTO dto) {
        log.info("POST /api/v1/customers (multipart) - Customer ID: {}", dto.getCustomerId());
        CustomerResponseDTO response = customerService.createCustomer(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomerResponseDTO> createCustomer(@Valid @RequestBody CustomerJsonRequestDTO dto) {
        log.info("POST /api/v1/customers (json) - Customer ID: {}", dto.getCustomerId());
        CustomerResponseDTO response = customerService.createCustomerFromJson(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping(value = "/{customerId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CustomerResponseDTO> updateCustomerWithPicture(
            @PathVariable @Pattern(regexp = CUSTOMER_ID_REGEXP, message = "Customer ID must be 6-10 alphanumeric characters") String customerId,
            @Valid @ModelAttribute CustomerRequestDTO dto) {
        log.info("PUT /api/v1/customers/{} (multipart)", customerId);
        CustomerResponseDTO response = customerService.updateCustomer(customerId, dto);
        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/{customerId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomerResponseDTO> updateCustomer(
            @PathVariable @Pattern(regexp = CUSTOMER_ID_REGEXP, message = "Customer ID must be 6-10 alphanumeric characters") String customerId,
            @Valid @RequestBody CustomerJsonRequestDTO dto) {
        log.info("PUT /api/v1/customers/{} (json)", customerId);
        CustomerResponseDTO response = customerService.updateCustomerFromJson(customerId, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<String> deleteCustomer(
            @PathVariable @Pattern(regexp = CUSTOMER_ID_REGEXP, message = "Customer ID must be 6-10 alphanumeric characters") String customerId) {
        log.info("DELETE /api/v1/customers/{}", customerId);
        customerService.deleteCustomer(customerId);
        return ResponseEntity.ok("Delete success");
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerResponseDTO> getCustomer(
            @PathVariable @Pattern(regexp = CUSTOMER_ID_REGEXP, message = "Customer ID must be 6-10 alphanumeric characters") String customerId) {
        log.info("GET /api/v1/customers/{}", customerId);
        CustomerResponseDTO response = customerService.getCustomer(customerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponseDTO>> getAllCustomers() {
        log.info("GET /api/v1/customers");
        List<CustomerResponseDTO> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/type/{customerType}")
    public ResponseEntity<List<CustomerResponseDTO>> getCustomersByType(
            @PathVariable Customer.CustomerType customerType) {
        log.info("GET /api/v1/customers/type/{}", customerType);
        List<CustomerResponseDTO> customers = customerService.getCustomersByType(customerType);
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/loyalty/top")
    public ResponseEntity<List<CustomerResponseDTO>> getTopLoyaltyCustomers(
            @RequestParam(defaultValue = "100") @Min(value = 0, message = "Minimum points cannot be negative") Integer minPoints) {
        log.info("GET /api/v1/customers/loyalty/top?minPoints={}", minPoints);
        List<CustomerResponseDTO> customers = customerService.getTopLoyaltyCustomers(minPoints);
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/{customerId}/picture")
    public ResponseEntity<byte[]> getCustomerPicture(
            @PathVariable @Pattern(regexp = CUSTOMER_ID_REGEXP, message = "Customer ID must be 6-10 alphanumeric characters") String customerId) {
        log.info("GET /api/v1/customers/{}/picture", customerId);
        byte[] picture = customerService.getCustomerPicture(customerId);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(picture);
    }

    @PutMapping("/{customerId}/loyalty")
    public ResponseEntity<String> updateLoyaltyPoints(
            @PathVariable @Pattern(regexp = CUSTOMER_ID_REGEXP, message = "Customer ID must be 6-10 alphanumeric characters") String customerId,
            @RequestParam @Min(value = 1, message = "Points to add must be positive") Integer pointsToAdd) {
        log.info("PUT /api/v1/customers/{}/loyalty?pointsToAdd={}", customerId, pointsToAdd);
        customerService.updateLoyaltyPoints(customerId, pointsToAdd);
        return ResponseEntity.ok("Loyalty points updated successfully");
    }

    @PutMapping("/{customerId}/purchases")
    public ResponseEntity<String> updateTotalPurchases(
            @PathVariable @Pattern(regexp = CUSTOMER_ID_REGEXP, message = "Customer ID must be 6-10 alphanumeric characters") String customerId,
            @RequestParam @Min(value = 1, message = "Purchase amount must be positive") Double purchaseAmount) {
        log.info("PUT /api/v1/customers/{}/purchases?purchaseAmount={}", customerId, purchaseAmount);
        customerService.updateTotalPurchases(customerId, purchaseAmount);
        return ResponseEntity.ok("Total purchases updated successfully");
    }
}
