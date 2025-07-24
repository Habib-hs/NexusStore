# Product Service

A RESTful microservice for managing products in an e-commerce platform, built with Spring Boot and MongoDB.

## 📋 Table of Contents

- [Overview](#-overview)
- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Prerequisites](#-prerequisites)
- [Installation](#-installation)
- [Configuration](#️-configuration)
- [API Documentation](#-api-documentation)
- [Running the Application](#️♂️-running-the-application)
- [Testing](#-testing)
- [Project Structure](#-project-structure)
- [Contributing](#-contributing)

## 🔍 Overview

The Product Service is a microservice designed to handle all product-related operations in an e-commerce system. It provides a complete CRUD API for managing products with features like search, filtering, and soft delete functionality.

## ✨ Features

- **Complete CRUD Operations** - Create, Read, Update, Delete products
- **Advanced Search** - Search products by name, category, and brand
- **Soft Delete** - Mark products as inactive instead of permanent deletion
- **RESTful API** - Clean and intuitive REST endpoints
- **MongoDB Integration** - NoSQL database with Spring Data MongoDB
- **Auto Index Creation** - Automatic database indexing for performance
- **JSON Formatting** - Pretty-printed JSON responses
- **Health Check** - Service health monitoring endpoint

## 🛠 Tech Stack

- **Java 17** - Programming language
- **Spring Boot 3.5.3** - Framework
- **Spring Data MongoDB** - Database integration
- **MongoDB 7.0.5** - NoSQL database
- **Lombok** - Code generation and boilerplate reduction
- **Maven** - Dependency management and build tool
- **Docker Compose** - Container orchestration
- **Testcontainers** - Integration testing

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

2. **Start MongoDB using Docker Compose**
   ```bash
   docker-compose up -d
   ```

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

# JSON Formatting
spring.jackson.serialization.indent-output=true

# Logging
logging.level.com.nexus.productservice=DEBUG
```

### MongoDB Setup

The application uses MongoDB with Docker Compose:

```yaml
version: '4'
services:
  mongodb:
    image: mongo:7.0.5
    container_name: mongodb
    ports:
      - "27017:27017"
    environment:
      MONGO_INITDB_ROOT_USERNAME: root
      MONGO_INITDB_ROOT_PASSWORD: password
      MONGO_INITDB_DATABASE: product-service
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
| `GET` | `/health` | Health check endpoint |

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

#### Update Product
```bash
curl -X PUT http://localhost:8080/api/products/{id} \
  -H "Content-Type: application/json" \
  -d '{
    "name": "iPhone 15 Pro",
    "description": "Updated iPhone model",
    "price": 1099.99,
    "quantity": 30,
    "category": "Electronics",
    "brand": "Apple"
  }'
```

#### Search Products
```bash
curl -X GET "http://localhost:8080/api/products/search?name=iPhone"
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

#### Error Response
```json
{
  "timestamp": "2025-07-23T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Product not found",
  "path": "/api/products/invalid-id"
}
```

## 🏃‍♂️ Running the Application

### Development Mode
```bash
# Start MongoDB
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

## 🧪 Testing

### Run Unit Tests
```bash
./mvnw test
```

### Run Integration Tests
```bash
./mvnw verify
```

### Manual Testing with curl
```bash
# Health check
curl http://localhost:8080/api/health

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
```

## 📁 Project Structure

```
src/
├── main/
│   ├── java/com/nexus/productservice/
│   │   ├── controller/          # REST Controllers
│   │   │   └── ProductController.java
│   │   ├── dto/                 # Data Transfer Objects
│   │   │   ├── ProductRequestDto.java
│   │   │   └── ProductResponseDto.java
│   │   ├── model/               # Entity Classes
│   │   │   └── Product.java
│   │   ├── repository/          # Repository Interfaces
│   │   │   └── ProductRepository.java
│   │   ├── service/             # Business Logic
│   │   │   └── ProductService.java
│   │   └── ProductServiceApplication.java
│   └── resources/
│       ├── application.properties
│       └── static/
└── test/                        # Test Classes
    └── java/
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

3. **Git Workflow**
   ```bash
   # Create feature branch
   git checkout -b feature/new-feature
   
   # Make changes and commit
   git add .
   git commit -m "feat: add new feature description"
   
   # Push changes
   git push origin feature/new-feature
   ```

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'feat: add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 📞 Support

For support and questions:
- Create an issue in the repository
- Contact the development team
- Check the documentation and API examples

---

**Built with ❤️ using Spring Boot and MongoDB**
