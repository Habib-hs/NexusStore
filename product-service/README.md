# Product Service

A RESTful microservice for managing products in an e-commerce platform, built with Spring Boot and MongoDB with comprehensive observability and monitoring.

## 📋 Table of Contents

- [Overview](#-overview)
- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Prerequisites](#-prerequisites)
- [Installation](#-installation)
- [Configuration](#️-configuration)
- [API Documentation](#-api-documentation)
- [Running the Application](#️♂️-running-the-application)
- [Monitoring & Observability](#-monitoring--observability)
- [Testing](#-testing)
- [Contributing](#-contributing)

## 🔍 Overview

The Product Service is a microservice designed to handle all product-related operations in an e-commerce system. It provides a complete CRUD API for managing products with features like search, filtering, soft delete functionality, and comprehensive monitoring through Prometheus, Grafana, and Loki.

## ✨ Features

- **Complete CRUD Operations** - Create, Read, Update, Delete products
- **Advanced Search** - Search products by name, category, and brand
- **Soft Delete** - Mark products as inactive instead of permanent deletion
- **RESTful API** - Clean and intuitive REST endpoints
- **MongoDB Integration** - NoSQL database with Spring Data MongoDB
- **Auto Index Creation** - Automatic database indexing for performance
- **JSON Formatting** - Pretty-printed JSON responses
- **Comprehensive Monitoring** - Prometheus metrics, Grafana dashboards, and Loki logging
- **Health Check** - Service health monitoring endpoint with actuator
- **Structured Logging** - Advanced logging with Logback and Loki integration

## 🛠 Tech Stack

### Core Application
- **Java 17** - Programming language
- **Spring Boot 3.5.3** - Framework
- **Spring Data MongoDB** - Database integration
- **MongoDB 7.0.5** - NoSQL database
- **Lombok** - Code generation and boilerplate reduction
- **Maven** - Dependency management and build tool

### Monitoring & Observability
- **Prometheus** - Metrics collection and monitoring
- **Grafana** - Visualization and dashboards
- **Loki** - Log aggregation system
- **Promtail** - Log shipping agent
- **Micrometer** - Application metrics facade
- **Spring Boot Actuator** - Production-ready features

### Testing & DevOps
- **Testcontainers** - Integration testing with real databases
- **Docker Compose** - Container orchestration
- **JUnit 5** - Unit testing framework
- **Mockito** - Mocking framework
- **AssertJ** - Fluent assertions

## 📋 Prerequisites

Before running this application, make sure you have the following installed:

- Java 17 or higher
- Maven 3.6+
- Docker and Docker Compose
- Git

## 🚀 Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd product-service
   ```

2. **Start the complete infrastructure using Docker Compose**
   ```bash
   docker-compose up -d
   ```
   This will start:
   - MongoDB (port 27017)
   - Prometheus (port 9090)
   - Grafana (port 3000)
   - Loki (port 3100)
   - Promtail (log shipper)

3. **Build the application**
   ```bash
   ./mvnw clean compile
   ```

4. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```

The service will be available at `http://localhost:8080`

## ⚙️ Configuration

### Application Properties

```properties
# Application Configuration
spring.application.name=product-service
server.port=8080

# MongoDB Configuration
spring.data.mongodb.uri=mongodb://root:password@localhost:27017/product-service?authSource=admin
spring.data.mongodb.auto-index-creation=true

# Logging Configuration
logging.config=classpath:logback-spring.xml
logging.level.com.nexus.productservice=DEBUG
logging.level.org.springframework.data.mongodb=DEBUG
logging.level.com.github.loki4j=DEBUG
logging.file.path=logs

# JSON Formatting
spring.jackson.serialization.indent-output=true

# Actuator Configuration (Monitoring)
management.endpoints.web.exposure.include=health,info,prometheus
management.endpoint.prometheus.enabled=true
management.endpoints.web.base-path=/actuator
```

### Docker Compose Services

The application includes a complete observability stack:

```yaml
version: '4'
services:
  mongodb:
    image: mongo:7.0.5
    ports: ["27017:27017"]
    
  prometheus:
    image: prom/prometheus:latest
    ports: ["9090:9090"]
    
  grafana:
    image: grafana/grafana:latest
    ports: ["3000:3000"]
    
  loki:
    image: grafana/loki:latest
    ports: ["3100:3100"]
    
  promtail:
    image: grafana/promtail:latest
    # Ships logs from ./logs to Loki
```

## 📚 API Documentation

### Base URL
```
http://localhost:8080/api
```

### Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/products` | Create a new product |
| `GET` | `/products` | Get all active products |
| `GET` | `/products/{id}` | Get product by ID |
| `GET` | `/products/category/{category}` | Get products by category |
| `GET` | `/products/brand/{brand}` | Get products by brand |
| `GET` | `/products/search?name={name}` | Search products by name |
| `PUT` | `/products/{id}` | Update an existing product |
| `DELETE` | `/products/{id}` | Soft delete a product |
| `DELETE` | `/products/{id}/hard` | Hard delete a product |

### Monitoring Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/actuator/health` | Application health status |
| `GET` | `/actuator/info` | Application information |
| `GET` | `/actuator/prometheus` | Prometheus metrics |

### Sample Requests

#### Create Product
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "iPhone 15",
    "description": "Latest iPhone model with advanced features",
    "price": 999.99,
    "quantity": 50,
    "category": "Electronics",
    "brand": "Apple"
  }'
```

#### Get All Products
```bash
curl -X GET http://localhost:8080/api/products
```

#### Health Check
```bash
curl -X GET http://localhost:8080/actuator/health
```

#### Prometheus Metrics
```bash
curl -X GET http://localhost:8080/actuator/prometheus
```

### Response Format

#### Success Response
```json
{
  "id": "60f1b2b3c8d4a5b6c7d8e9f0",
  "name": "iPhone 15",
  "description": "Latest iPhone model",
  "price": 999.99,
  "quantity": 50,
  "category": "Electronics",
  "brand": "Apple",
  "active": true,
  "createdAt": "2025-07-23T10:30:00",
  "updatedAt": "2025-07-23T10:30:00"
}
```

## 🏃‍♂️ Running the Application

### Development Mode
```bash
# Start complete infrastructure
docker-compose up -d

# Run application with hot reload
./mvnw spring-boot:run
```

### Production Mode
```bash
# Build JAR
./mvnw clean package

# Run JAR
java -jar target/product-service-0.0.1-SNAPSHOT.jar
```

### Using Docker
```bash
# Build Docker image
docker build -t product-service .

# Run container
docker run -p 8080:8080 --network host product-service
```

## 📊 Monitoring & Observability

### Access Monitoring Tools

| Service | URL | Credentials |
|---------|-----|-------------|
| **Grafana** | http://localhost:3000 | admin/admin |
| **Prometheus** | http://localhost:9090 | - |
| **Application** | http://localhost:8080 | - |

### Logging Architecture

The application uses a sophisticated logging setup:

1. **Console Logging** - Development and debugging
2. **File Logging** - Structured logs in `logs/product-service.log`
3. **Loki Integration** - Centralized log aggregation

#### Log Configuration Features
- **Rolling File Appender** - 10MB max file size, 30 days retention
- **Structured Logging** - Consistent log format across all appenders
- **Service Labels** - Automatic labeling for log filtering
- **Debug Logging** - Enhanced logging for development

#### Sample Log Entries
```
2025-01-15 10:30:00.123  INFO c.n.p.service.ProductService : Creating new product with name: iPhone 15 and category: Electronics
2025-01-15 10:30:00.125 DEBUG c.n.p.service.ProductService : Product creation request details - Name: iPhone 15, Price: 999.99, Quantity: 50, Brand: Apple
2025-01-15 10:30:00.150  INFO c.n.p.service.ProductService : Successfully created product with ID: 507f1f77bcf86cd799439011 and name: iPhone 15
```

### Metrics Collection

The application exposes comprehensive metrics via **Spring Boot Actuator** and **Micrometer**:

- **HTTP Request Metrics** - Request count, duration, status codes
- **JVM Metrics** - Memory usage, garbage collection, thread counts
- **Database Metrics** - MongoDB connection pool, query performance
- **Custom Business Metrics** - Product operations, search performance

### Setting Up Grafana Dashboards

1. **Access Grafana** at http://localhost:3000 (admin/admin)
2. **Add Prometheus Data Source**:
   - URL: `http://prometheus:9090`
3. **Add Loki Data Source**:
   - URL: `http://loki:3100`
4. **Import Dashboards** for:
   - Spring Boot application metrics
   - JVM performance monitoring
   - Log analysis and visualization

### Prometheus Configuration

The [`prometheus.yml`](prometheus.yml) is configured to scrape metrics from:
```yaml
scrape_configs:
  - job_name: 'spring-boot'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['host.docker.internal:8080']
```

## 🧪 Testing

### Test Architecture

The project includes comprehensive testing at multiple levels:

#### Unit Tests
- **Service Layer Tests** - [`ProductServiceTest.java`](src/test/java/com/nexus/productservice/service/ProductServiceTest.java)
- **Mockito Integration** - Mock dependencies for isolated testing
- **Complete Coverage** - All CRUD operations and edge cases

#### Integration Tests
- **Repository Tests** - [`ProductRepositoryTest.java`](src/test/java/com/nexus/productservice/repository/ProductRepositoryTest.java)
- **Testcontainers** - Real MongoDB instances for testing
- **Data Layer Validation** - Custom query methods testing

#### Web Layer Tests
- **Controller Tests** - [`ProductControllerTest.java`](src/test/java/com/nexus/productservice/controller/ProductControllerTest.java)
- **MockMvc** - HTTP endpoint testing without server startup
- **Request/Response Validation** - JSON serialization and HTTP status codes

### Running Tests

```bash
# Run all tests
./mvnw test

# Run only unit tests
./mvnw test -Dtest="*Test"

# Run only integration tests
./mvnw test -Dtest="*IT"

# Run with coverage
./mvnw test jacoco:report

# Run specific test class
./mvnw test -Dtest="ProductServiceTest"
```

### Test Configuration

Tests use [`TestcontainersConfiguration.java`](src/test/java/com/nexus/productservice/TestcontainersConfiguration.java) for:
- **Real MongoDB Container** - Latest MongoDB version
- **Automatic Cleanup** - Container lifecycle management
- **Service Connection** - Spring Boot auto-configuration

### Manual Testing with curl
```bash
# Health check
curl http://localhost:8080/actuator/health

# Create and test a product
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Product",
    "description": "A test product",
    "price": 29.99,
    "quantity": 100,
    "category": "Test",
    "brand": "TestBrand"
  }'

# Check Prometheus metrics
curl http://localhost:8080/actuator/prometheus | grep http_server_requests
```

## 🔧 Development Setup

1. **IDE Setup**
   - Install Lombok plugin for your IDE
   - Configure Java 17 as project SDK
   - Enable annotation processing

2. **Code Style**
   - Follow Java naming conventions
   - Use Lombok annotations to reduce boilerplate
   - Maintain consistent formatting
   - Add comprehensive logging for debugging

3. **Local Development Workflow**
   ```bash
   # Start infrastructure
   docker-compose up -d mongodb
   
   # Run application in development mode
   ./mvnw spring-boot:run
   
   # Run tests in watch mode
   ./mvnw test -Dspring.profiles.active=test
   ```

4. **Git Workflow**
   ```bash
   # Create feature branch
   git checkout -b feature/new-feature
   
   # Make changes and commit
   git add .
   git commit -m "feat: add new feature description"
   
   # Push changes
   git push origin feature/new-feature
   ```

## 🚀 Production Deployment

### Environment Configuration

```bash
# Production environment variables
export SPRING_PROFILES_ACTIVE=prod
export MONGODB_URI=mongodb://prod-user:password@prod-mongodb:27017/product-service
export LOKI_URL=http://prod-loki:3100/loki/api/v1/push
export PROMETHEUS_ENABLED=true
```

### Docker Production Build

```bash
# Build optimized JAR
./mvnw clean package -Pprod

# Build production Docker image
docker build -t product-service:latest .

# Run with production configuration
docker run -d \
  --name product-service \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e MONGODB_URI=$MONGODB_URI \
  product-service:latest
```

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Write tests for your changes
4. Ensure all tests pass (`./mvnw test`)
5. Commit your changes (`git commit -m 'feat: add amazing feature'`)
6. Push to the branch (`git push origin feature/amazing-feature`)
7. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 📞 Support

For support and questions:
- Create an issue in the repository
- Contact the development team
- Check the documentation and API examples
- Review logs in Grafana dashboards

---

**Built with ❤️ using Spring Boot, MongoDB, and comprehensive observability stack**