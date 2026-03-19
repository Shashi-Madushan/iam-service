package lk.ijse.eca.customerservice.service.impl;

import lk.ijse.eca.customerservice.dto.CustomerRequestDTO;
import lk.ijse.eca.customerservice.dto.CustomerResponseDTO;
import lk.ijse.eca.customerservice.entity.Customer;
import lk.ijse.eca.customerservice.exception.DuplicateCustomerException;
import lk.ijse.eca.customerservice.exception.FileOperationException;
import lk.ijse.eca.customerservice.exception.CustomerNotFoundException;
import lk.ijse.eca.customerservice.mapper.CustomerMapper;
import lk.ijse.eca.customerservice.repository.CustomerRepository;
import lk.ijse.eca.customerservice.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Value("${app.storage.path}")
    private String storagePathStr;

    private Path storagePath;

    @Override
    @Transactional
    public CustomerResponseDTO createCustomer(CustomerRequestDTO dto) {
        log.debug("Creating customer with ID: {}", dto.getCustomerId());

        if (customerRepository.existsById(dto.getCustomerId())) {
            log.warn("Duplicate customer ID detected: {}", dto.getCustomerId());
            throw new DuplicateCustomerException(dto.getCustomerId());
        }

        String pictureId = UUID.randomUUID().toString();

        Customer customer = customerMapper.toEntity(dto);
        customer.setPicture(pictureId);

        customerRepository.save(customer);
        log.debug("Customer persisted to DB: {}", dto.getCustomerId());

        savePicture(pictureId, dto.getPicture());

        log.info("Customer created successfully: {}", dto.getCustomerId());
        return customerMapper.toResponseDto(customer);
    }

    @Override
    @Transactional
    public CustomerResponseDTO updateCustomer(String customerId, CustomerRequestDTO dto) {
        log.debug("Updating customer with ID: {}", customerId);

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> {
                    log.warn("Customer not found for update: {}", customerId);
                    return new CustomerNotFoundException(customerId);
                });

        String oldPictureId = customer.getPicture();
        boolean pictureChanged = dto.getPicture() != null && !dto.getPicture().isEmpty();
        String newPictureId = pictureChanged ? UUID.randomUUID().toString() : oldPictureId;

        customerMapper.updateEntity(dto, customer);
        customer.setPicture(newPictureId);

        customerRepository.save(customer);
        log.debug("Customer updated in DB: {}", customerId);

        if (pictureChanged) {
            savePicture(newPictureId, dto.getPicture());
            tryDeletePicture(oldPictureId);
        }

        log.info("Customer updated successfully: {}", customerId);
        return customerMapper.toResponseDto(customer);
    }

    @Override
    @Transactional
    public void deleteCustomer(String customerId) {
        log.debug("Deleting customer with ID: {}", customerId);

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> {
                    log.warn("Customer not found for deletion: {}", customerId);
                    return new CustomerNotFoundException(customerId);
                });

        String pictureId = customer.getPicture();

        customerRepository.delete(customer);
        log.debug("Customer marked for deletion in DB: {}", customerId);

        deletePicture(pictureId);

        log.info("Customer deleted successfully: {}", customerId);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponseDTO getCustomer(String customerId) {
        log.debug("Fetching customer with ID: {}", customerId);
        return customerRepository.findById(customerId)
                .map(customerMapper::toResponseDto)
                .orElseThrow(() -> {
                    log.warn("Customer not found: {}", customerId);
                    return new CustomerNotFoundException(customerId);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponseDTO> getAllCustomers() {
        log.debug("Fetching all customers");
        List<CustomerResponseDTO> customers = customerRepository.findAll()
                .stream()
                .map(customerMapper::toResponseDto)
                .collect(Collectors.toList());
        log.debug("Fetched {} customers", customers.size());
        return customers;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponseDTO> getCustomersByType(Customer.CustomerType customerType) {
        log.debug("Fetching customers by type: {}", customerType);
        return customerRepository.findByCustomerType(customerType)
                .stream()
                .map(customerMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponseDTO> getTopLoyaltyCustomers(Integer minPoints) {
        log.debug("Fetching top loyalty customers with minimum points: {}", minPoints);
        return customerRepository.findTopLoyaltyCustomers(minPoints)
                .stream()
                .map(customerMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] getCustomerPicture(String customerId) {
        log.debug("Fetching picture for customer ID: {}", customerId);
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> {
                    log.warn("Customer not found: {}", customerId);
                    return new CustomerNotFoundException(customerId);
                });
        Path filePath = storagePath().resolve(customer.getPicture());
        try {
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            log.error("Failed to read picture for customer: {}", customerId, e);
            throw new FileOperationException("Failed to read picture for customer: " + customerId, e);
        }
    }

    @Override
    @Transactional
    public void updateLoyaltyPoints(String customerId, Integer pointsToAdd) {
        log.debug("Updating loyalty points for customer {}: +{}", customerId, pointsToAdd);
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> {
                    log.warn("Customer not found for loyalty update: {}", customerId);
                    return new CustomerNotFoundException(customerId);
                });

        customer.setLoyaltyPoints(customer.getLoyaltyPoints() + pointsToAdd);
        customerRepository.save(customer);
        log.info("Loyalty points updated for customer {}: {}", customerId, customer.getLoyaltyPoints());
    }

    @Override
    @Transactional
    public void updateTotalPurchases(String customerId, Double purchaseAmount) {
        log.debug("Updating total purchases for customer {}: +{}", customerId, purchaseAmount);
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> {
                    log.warn("Customer not found for purchase update: {}", customerId);
                    return new CustomerNotFoundException(customerId);
                });

        customer.setTotalPurchases(customer.getTotalPurchases() + purchaseAmount);
        customerRepository.save(customer);
        log.info("Total purchases updated for customer {}: {}", customerId, customer.getTotalPurchases());
    }

    private Path storagePath() {
        if (storagePath == null) {
            storagePath = Paths.get(storagePathStr);
        }
        try {
            Files.createDirectories(storagePath);
        } catch (IOException e) {
            throw new FileOperationException(
                    "Failed to create storage directory: " + storagePath.toAbsolutePath(), e);
        }
        return storagePath;
    }

    private void savePicture(String pictureId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileOperationException("Picture file must not be empty");
        }
        Path filePath = storagePath().resolve(pictureId);
        try {
            Files.write(filePath, file.getBytes());
            log.debug("Picture saved: {}", filePath);
        } catch (IOException e) {
            log.error("Failed to save picture: {}", filePath, e);
            throw new FileOperationException("Failed to save picture file: " + pictureId, e);
        }
    }

    private void deletePicture(String pictureId) {
        Path filePath = storagePath().resolve(pictureId);
        try {
            boolean deleted = Files.deleteIfExists(filePath);
            if (deleted) {
                log.debug("Picture deleted: {}", filePath);
            } else {
                log.warn("Picture file not found on disk (already removed?): {}", filePath);
            }
        } catch (IOException e) {
            log.error("Failed to delete picture: {}", filePath, e);
            throw new FileOperationException("Failed to delete picture file: " + pictureId, e);
        }
    }

    private void tryDeletePicture(String pictureId) {
        try {
            deletePicture(pictureId);
        } catch (FileOperationException e) {
            log.warn("Could not delete old picture file '{}'. Manual cleanup may be required.", pictureId);
        }
    }
}
