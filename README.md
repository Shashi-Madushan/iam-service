# Customer Management Service

A comprehensive Spring Boot microservice for managing customers and users in a retail POS system. This service handles customer registration, user management, loyalty programs, and provides RESTful APIs for integration with other system components.

## 📋 Project Description

The Customer Management Service is a core component of a microservices-based retail Point of Sale (POS) system. It provides robust functionality for managing customer profiles, user accounts, and loyalty programs. The service is built using Spring Boot with Spring Cloud for microservice architecture, featuring JPA for data persistence, Spring Security for authentication, and comprehensive validation mechanisms.

### Key Features

- **Customer Management**: Complete CRUD operations for customer profiles with image upload support
- **User Management**: Comprehensive user account management with role-based access control
- **Lalty Program**: Built-in loyalty points system and purchase tracking
- **File Management**: Customer profile picture upload and retrieval
- **Advanced Search**: Multiple search and filtering capabilities
- **Pagination**: Efficient data retrieval with pagination support
- **Validation**: Comprehensive input validation and error handling
- **Security**: Spring Security integration with customizable configurations

## 🛠 Technology Stack

### Core Framework
- **Spring Boot**: 4.0.3 - Main application framework
- **Spring Cloud**: 2025.1.0 - Microservice architecture support
- **Spring Data JPA**: Database operations and ORM
- **Spring Security**: Authentication and authorization
- **Spring Validation**: Input validation framework

### Database & Persistence
- **MySQL**: Primary database for customer and user data
- **Spring Boot Data JPA**: ORM and database operations
- **Hibernate**: JPA implementation

### Development & Build Tools
- **Java**: 25 - Programming language
- **Maven**: Dependency management and build tool
- **Lombok**: Code generation for boilerplate reduction
- **MapStruct**: 1.6.3 - Bean mapping framework

### Additional Libraries
- **Spring Boot Actuator**: Application monitoring and management
- **Spring Boot DevTools**: Development-time tools
- **Netflix Eureka Client**: Service discovery
- **Spring Cloud Config**: Centralized configuration management
- **Spring Boot Starter Validation**: Bean validation support

## 🚀 Setup / Getting Started Instructions

### Prerequisites

- **Java 25** or higher
- **Maven 3.8+**
- **MySQL 8.0+**
- **Spring Cloud Config Server** (running on port 9000)
- **Netflix Eureka Service Registry** (optional, for service discovery)

### Installation Steps

1. **Clone the Repository**
   ```bash
   git clone <repository-url>
   cd Project-Services/customer-service
   ```

2. **Database Setup**
   ```sql
   CREATE DATABASE customer_management_db;
   CREATE USER 'customer_user'@'localhost' IDENTIFIED BY 'password';
   GRANT ALL PRIVILEGES ON customer_management_db.* TO 'customer_user'@'localhost';
   FLUSH PRIVILEGES;
   ```

3. **Configuration**
   - Ensure Spring Cloud Config Server is running on `http://localhost:9000`
   - Configuration files are located in `src/main/resources/`
   - Default profile: `dev`

4. **Build the Application**
   ```bash
   ./mvnw clean install
   ```

5. **Run the Application**
   ```bash
   ./mvnw spring-boot:run
   ```
   
   Or using the Maven wrapper:
   ```bash
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
   ```

6. **Verify Service**
   - The service will start on port 8080 (or as configured)
   - Health check endpoint: `http://localhost:8080/actuator/health`

### Docker Setup (Optional)

```bash
# Build Docker image
docker build -t customer-service .

# Run with Docker
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=dev \
  -e SPRING_CLOUD_CONFIG_URI=http://config-server:9000 \
  customer-service
```

## 📚 API Endpoints

### Customer Management Endpoints

| Method | Endpoint | Description | Request Type |
|--------|----------|-------------|--------------|
| POST | `/api/v1/customers` | Create new customer | `multipart/form-data` |
| GET | `/api/v1/customers` | Get all customers | - |
| GET | `/api/v1/customers/{customerId}` | Get customer by ID | - |
| PUT | `/api/v1/customers/{customerId}` | Update customer | `multipart/form-data` |
| DELETE | `/api/v1/customers/{customerId}` | Delete customer | - |
| GET | `/api/v1/customers/type/{customerType}` | Get customers by type | - |
| GET | `/api/v1/customers/loyalty/top` | Get top loyalty customers | - |
| GET | `/api/v1/customers/{customerId}/picture` | Get customer picture | - |
| PUT | `/api/v1/customers/{customerId}/loyalty` | Update loyalty points | - |
| PUT | `/api/v1/customers/{customerId}/purchases` | Update total purchases | - |

### User Management Endpoints

| Method | Endpoint | Description | Request Type |
|--------|----------|-------------|--------------|
| POST | `/api/v1/users` | Create new user | `application/json` |
| GET | `/api/v1/users` | Get all users | - |
| GET | `/api/v1/users/{id}` | Get user by ID | - |
| GET | `/api/v1/users/username/{username}` | Get user by username | - |
| PUT | `/api/v1/users/{id}` | Update user | `application/json` |
| DELETE | `/api/v1/users/{id}` | Delete user | - |
| GET | `/api/v1/users/type/{userType}` | Get users by type | - |
| GET | `/api/v1/users/status/{status}` | Get users by status | - |
| GET | `/api/v1/users/search` | Search users by name | - |
| GET | `/api/v1/users/active-since` | Get active users since date | - |
| GET | `/api/v1/users/paginated/type/{userType}` | Get paginated users by type | - |
| GET | `/api/v1/users/search/type/{userType}` | Search users by type and keyword | - |
| PUT | `/api/v1/users/{id}/status` | Update user status | - |
| PUT | `/api/v1/users/{id}/login` | Record user login | - |
| PUT | `/api/v1/users/{id}/password` | Change user password | - |
| GET | `/api/v1/users/stats/type/{userType}` | Get user count by type | - |
| GET | `/api/v1/users/stats/status/{status}` | Get user count by status | - |
| GET | `/api/v1/users/exists/username/{username}` | Check if username exists | - |
| GET | `/api/v1/users/exists/email/{email}` | Check if email exists | - |

### Customer Types
- `REGULAR` - Standard customers
- `PREMIUM` - Premium customers with additional benefits

### User Types
- `EMPLOYEE` - System employees
- `ADMIN` - System administrators
- `MANAGER` - Store managers

### User Status
- `ACTIVE` - Active user accounts
- `INACTIVE` - Inactive user accounts
- `SUSPENDED` - Suspended user accounts

## 🧪 Test Scripts

### Comprehensive Test Suite

The project includes a comprehensive test script (`test-endpoints.sh`) that tests all API endpoints through the API Gateway.

#### Running Tests

```bash
# Make the script executable
chmod +x test-endpoints.sh

# Run all tests
./test-endpoints.sh
```

#### Test Coverage

The test script covers:

1. **Customer Controller Tests**
   - Customer creation with image upload
   - Customer retrieval operations
   - Customer updates and deletions
   - Loyalty points and purchase tracking
   - Picture upload and retrieval

2. **User Controller Tests**
   - User creation and management
   - Authentication operations
   - Search and filtering
   - Pagination
   - Statistics and reporting

3. **Edge Case Tests**
   - Invalid ID formats
   - Not found scenarios
   - Validation errors

4. **Health Checks**
   - Service connectivity
   - API Gateway accessibility

#### Test Configuration

- **API Gateway URL**: `http://localhost:7001`
- **Test Data**: Automatically generated unique test data
- **Results**: Color-coded output with pass/fail summary

### Manual Testing Examples

#### Create Customer
```bash
curl -X POST "http://localhost:7001/api/v1/customers" \
  -F "customerId=CUST123456" \
  -F "name=John Doe" \
  -F "address=123 Main St" \
  -F "mobile=1234567890" \
  -F "email=john@example.com" \
  -F "customerType=REGULAR" \
  -F "picture=@customer_photo.jpg"
```

#### Create User
```bash
curl -X POST "http://localhost:7001/api/v1/users" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "password": "password123",
    "email": "john@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "userType": "EMPLOYEE",
    "status": "ACTIVE",
    "phone": "1234567890",
    "address": "123 Main St"
  }'
```

## 🏗 Project Structure

```
customer-service/
├── src/main/java/lk/ijse/eca/customerservice/
│   ├── controller/          # REST API Controllers
│   │   ├── CustomerController.java
│   │   └── UserController.java
│   ├── dto/                 # Data Transfer Objects
│   │   ├── CustomerRequestDTO.java
│   │   ├── CustomerResponseDTO.java
│   │   ├── UserRequestDTO.java
│   │   └── UserResponseDTO.java
│   ├── entity/              # JPA Entities
│   │   ├── Customer.java
│   │   └── User.java
│   ├── exception/           # Custom Exceptions
│   │   ├── CustomerNotFoundException.java
│   │   ├── DuplicateCustomerException.java
│   │   ├── UserNotFoundException.java
│   │   └── DuplicateUserException.java
│   ├── repository/          # JPA Repositories
│   │   ├── CustomerRepository.java
│   │   └── UserRepository.java
│   ├── service/             # Service Layer
│   │   ├── CustomerService.java
│   │   ├── UserService.java
│   │   ├── impl/
│   │   │   ├── CustomerServiceImpl.java
│   │   │   └── UserServiceImpl.java
│   ├── mapper/              # MapStruct Mappers
│   │   ├── CustomerMapper.java
│   │   └── UserMapper.java
│   ├── config/              # Configuration Classes
│   │   └── SecurityConfig.java
│   ├── validation/          # Custom Validators
│   │   ├── ValidImage.java
│   │   └── ValidImageValidator.java
│   ├── handler/             # Exception Handlers
│   │   └── GlobalExceptionHandler.java
│   └── CustomerManagementServiceApplication.java
├── src/main/resources/
│   ├── application.yaml
│   ├── application-dev.yaml
│   └── uploads/customers/   # File storage directory
├── test-endpoints.sh        # Comprehensive test script
├── pom.xml                  # Maven configuration
└── README.md               # This file
```

## 🔧 Configuration

### Application Properties

Key configuration options:

```yaml
spring:
  application:
    name: customer-service
  profiles:
    active: dev
  config:
    import: "configserver:"
  cloud:
    config:
      uri: http://localhost:9000

app:
  storage:
    path: ./uploads/customers
```

### Environment Variables

- `SPRING_PROFILES_ACTIVE`: Active profile (dev/prod)
- `SPRING_CLOUD_CONFIG_URI`: Config server URI
- `DB_HOST`: Database host (overrides default)
- `DB_PORT`: Database port (overrides default)
- `DB_NAME`: Database name (overrides default)

## 📊 Monitoring & Health

### Actuator Endpoints

- `/actuator/health` - Application health status
- `/actuator/info` - Application information
- `/actuator/metrics` - Application metrics

### Logging

- **Log Level**: Configurable per profile
- **Log Format**: Structured logging with request tracking
- **File Logging**: Configured for production environments

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Run the test suite
6. Submit a pull request

## 📝 Student Information

- **Student Name**: Shashi Madushan
- **Student Number**: 2301691002
- **Project**: Customer Management Service for Retail POS System
- **Course**: Enterprise Computing Architecture

## 📄 License

This project is part of an academic assignment for the Enterprise Computing Architecture course.

## 📞 Support

For any issues or questions regarding this service:

1. Check the test script output for common issues
2. Review the application logs
3. Verify all prerequisite services are running
4. Check the configuration files

---

**Note**: This service is designed to be part of a larger microservices architecture and should be used in conjunction with other services such as API Gateway, Config Server, and Service Registry for full functionality.
