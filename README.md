# A Microservice implementation to alleviate the covid-19 crisis

This project was borne as a result of trying to figure out how to distribute goods and service to customers' requests due to the restrictions and isolation that was experienced during the covid-19 pandemic. It was also an opportunity to see how some of the components that were implemented here stack up to achieve this requirement using a microservice architecture.

# Technologies and Components
- Java 11
- JPA
- Spring Suite (SpringBoot, Spring Security)
- Redis
- Kafka
- Eureka
- Maven
- RDBMS
- JUnit5
- Docker

# Core Features
- Application layering
- Service discovery and Microservice intercommunication
- Messaging
- Repository pattern
- JUnit5 testing
- Security
- Containerization

## Implementations
Java 11 was the core language used while the SpringSuite was used for component wiring, dependency injection and security (JWT). Redis was used as a repository for fast searches of items, while Kafka was used for messaging for microservice intercommunication. In some scenarios where HTTP was needed for microservice intercommunication, Eureka was used for service discovery. Maven is used as the build tool. The Application layering was implemented by splitting the appication into the Controller, Service, Repository and Domain model layers. RDBMS(MySql) was used as the main repository for data persistence. JUnit5 was the testing framework used for unit testing, layered testing and integration testing. And finally, Docker is used as the containerization tool.

# .env db config
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/db?useSSL=false&serverTimezone=UTC&useLegacyDatetimeCode=false
SPRING_DATASOURCE_USERNAME=
SPRING_DATASOURCE_PASSWORD=