# NexusStore 🚀

A microservices e-commerce platform built for learning modern Java and Spring Boot architecture.

## 🎯 What is this?

This is a learning project to understand microservices architecture by building a real e-commerce platform. We'll implement user management, product catalog, orders, payments, and notifications using industry-standard technologies.

## 🏗️ Planned Architecture

```
┌─────────────┐  ┌──────────────┐  ┌─────────────┐
│User Service │  │Product Service│  │Order Service│
│(Auth/Users) │  │  (Catalog)   │  │ (Purchases) │
└─────────────┘  └──────────────┘  └─────────────┘

┌──────────────┐  ┌─────────────┐  ┌─────────────┐
│Payment Service│  │Notification │  │API Gateway  │
│(Transactions)│  │  Service    │  │(GraphQL+    │
│              │  │             │  │ Routing)    │
└──────────────┘  └─────────────┘  └─────────────┘
```

## 🛠️ Technology Stack

- **Java 21** + **Spring Boot 3.4**
- **PostgreSQL** + **MongoDB** + **Redis**
- **gRPC** + **REST API** + **GraphQL**
- **Apache Kafka** for messaging
- **Docker** for containerization

## 📁 Project Structure

```
microcommerce-academy/
├── user-service/          # User authentication and management
├── product-service/       # Product catalog and search
├── order-service/         # Order processing
├── payment-service/       # Payment handling
├── notification-service/  # Email/SMS notifications
├── api-gateway/          # API Gateway with GraphQL
└── docker-compose.yml    # Local development setup
```

## 🚀 Getting Started

### Prerequisites
- Java 21+
- Docker & Docker Compose
- Maven 3.8+

### Setup
1. Clone the repository
   ```bash
   git clone https://github.com/Habib-hs/NexusStore.git
   ```

2. Start databases
   ```bash
   docker-compose up -d
   ```

3. Run services (as they're completed)
   ```bash
   cd user-service
   ./mvnw spring-boot:run
   ```

## 🎯 Learning Goals

- Build microservices with Spring Boot
- Implement gRPC and REST communication
- Use GraphQL for API aggregation
- Handle distributed data with multiple databases
- Learn Docker containerization
- Implement event-driven architecture with Kafka

## 📚 Development Progress

- [ ] User Service - Authentication & User Management
- [ ] Product Service - Product Catalog & Search
- [ ] Order Service - Order Processing
- [ ] Payment Service - Payment Handling
- [ ] Notification Service - Email/SMS Alerts
- [ ] API Gateway - GraphQL Federation
- [ ] Docker Setup - Containerization
- [ ] Monitoring - Observability Stack

## 🤝 Contributing

This is a learning project! Feel free to:
- Report issues
- Suggest improvements
- Submit pull requests
- Share learning resources

## 📄 License

MIT License - feel free to use this for learning!

---

**Status: 🚧 In Development**

*We're just getting started! Each service will be built step by step.*
