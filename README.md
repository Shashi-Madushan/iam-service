# Identity and Access Management (IAM) Service

A comprehensive Spring Boot microservice for managing identities, authentication, and access control in a retail POS system. This service handles user accounts, roles, permissions, and provides secure RESTful APIs for integration with other system components.

## 📋 Project Description

The IAM Service is a core component of a microservices-based retail Point of Sale (POS) system. It provides robust functionality for managing user identities, authentication, authorization, and access control. The service is built using Spring Boot with Spring Cloud for microservice architecture, featuring JPA for data persistence, Spring Security for authentication, and comprehensive validation mechanisms.

### Key Features

- **User Management**: Comprehensive user account management with role-based access control
- **Authentication**: Secure user authentication with password management
- **Authorization**: Role-based access control (RBAC) for system resources
- **User Profiles**: Complete user profile management with contact information
- **Account Lifecycle**: User account creation, activation, suspension, and deletion
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
- **MySQL**: Primary database for user and identity data
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
   cd Project-Services/iam-service
   ```

2. **Database Setup**
   ```sql
   CREATE DATABASE db_iam;
   CREATE USER 'iam_user'@'localhost' IDENTIFIED BY 'password';
   GRANT ALL PRIVILEGES ON db_iam.* TO 'iam_user'@'localhost';
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
   - The service will start on a dynamic port (registered with Eureka)
   - Health check endpoint: `http://localhost:{port}/actuator/health`

### Docker Setup (Optional)

```bash
# Build Docker image
docker build -t iam-service .

# Run with Docker
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=dev \
  -e SPRING_CLOUD_CONFIG_URI=http://config-server:9000 \
  iam-service
```

## 📚 API Endpoints

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

1. **User Controller Tests**
   - User creation and management
   - Authentication operations
   - Search and filtering
   - Pagination
   - Statistics and reporting

2. **Edge Case Tests**
   - Invalid ID formats
   - Not found scenarios
   - Validation errors

3. **Health Checks**
   - Service connectivity
   - API Gateway accessibility

#### Test Configuration

- **API Gateway URL**: `http://localhost:7001`
- **Test Data**: Automatically generated unique test data
- **Results**: Color-coded output with pass/fail summary

### Manual Testing Examples

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
iam-service/
├── src/main/java/lk/ijse/eca/iamservice/
│   ├── controller/          # REST API Controllers
│   │   └── UserController.java
│   ├── dto/                 # Data Transfer Objects
│   │   ├── UserRequestDTO.java
│   │   └── UserResponseDTO.java
│   ├── entity/              # JPA Entities
│   │   └── User.java
│   ├── exception/           # Custom Exceptions
│   │   ├── UserNotFoundException.java
│   │   └── DuplicateUserException.java
│   ├── repository/          # JPA Repositories
│   │   └── UserRepository.java
│   ├── service/             # Service Layer
│   │   ├── UserService.java
│   │   └── impl/
│   │       └── UserServiceImpl.java
│   ├── mapper/              # MapStruct Mappers
│   │   └── UserMapper.java
│   ├── config/              # Configuration Classes
│   │   └── SecurityConfig.java
│   ├── validation/          # Custom Validators
│   ├── handler/             # Exception Handlers
│   │   └── GlobalExceptionHandler.java
│   └── IamServiceApplication.java
├── src/main/resources/
│   ├── application.yaml
│   ├── application-dev.yaml
│   └── uploads/             # File storage directory
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
    name: iam-service
  profiles:
    active: dev
  config:
    import: "configserver:"
  cloud:
    config:
      uri: http://localhost:9000

app:
  storage:
    path: ./uploads
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
- **Project**: IAM Service for Retail POS System
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
