# Muti-Gateway-Ecommerce-API
Muti-Gateway-Ecommerce-API-Spring-Boot-Application

Project Structure and Setup

Overview
The Payment Service implementation facilitates the processing of payment requests, ensuring reliability and fault tolerance. It employs the following key features and design patterns:

Setup Instructions
•	Ensure Java 17 is installed.

Transaction Handling:
The initiate Payment method initiates the payment process by validating if a transaction with the same ID already exists.
If a transaction already exists, an IllegalStateException is thrown to handle this scenario.

Retry Mechanism:
The service attempts to process the payment with the preferred gateway.
If unsuccessful, it retries the payment using an alternative gateway.
The success rate of payment processing is simulated with an 80% success rate.

Logging:
Extensive logging using SLF4J is implemented throughout the service to capture important events, error messages, and transaction details.
Log messages provide insights into the payment processing flow, error handling, and fallback behavior.

Exception Handling:
The service throws a custom PaymentProcessingException to encapsulate errors during payment processing.
This exception is caught in the catch block, logged, and then rethrown with additional context.

Asynchronous Processing:
CompletableFuture is used for asynchronous processing of payment requests.
This allows the service to handle multiple payment requests concurrently.

Alternative Gateway Selection:
The getAlternativeGateway method dynamically selects an alternative gateway based on the preferred gateway.
It uses Enum’s to represent supported gateways and throws an exception for unsupported gateways.

Fallback Behavior:
If payment fails even after retrying, the service logs an error and triggers a fallback to default behavior.
Fallback involves sending notifications to administrators, providing insights into the payment failure.

Persistence:
The service transforms payment request details into a Payment entity and saves it to the repository for persistence.
This ensures that payment transactions are stored for future reference.

Timeout Handling:
The process method uses CompletableFuture to set a timeout for payment processing.
If payment processing exceeds the defined timeout, it returns a failure status.


Design Patterns:
    1. Singleton Pattern
    Description: Ensures a single instance of the PaymentServiceImpl class, managed as a singleton bean throughout the application.

    2. Factory Method Pattern
    Description: Utilizes the getAlternativeGateway method as a factory for selecting an alternative payment gateway.

    3. Dependency Injection
    Description: Implements constructor-based dependency injection, injecting the PaymentRepository dependency into the PaymentServiceImpl class.

    4. Facade Pattern in Repository
    Description: Applies the Facade Pattern with the PaymentRepository interface, providing a simplified interface for data access operations.

    5. Asynchronous Method Invocation
    Description: Implements asynchronous processing using CompletableFuture in the PaymentServiceImpl class for non-blocking operations.

    6. Exception Handling Patterns
    Description: Utilizes a combination of checked and unchecked exceptions, such as PaymentProcessingException, to handle errors during payment processing.

    7. Logger Patterns
    Description: Adopts SLF4J logger in the PaymentServiceImpl class for consistent and flexible logging throughout the service.


Test Case Strategy:
The test cases utilize the Spring MockMvc framework to perform HTTP requests and validate responses. They cover both positive and negative scenarios, ensuring that the payment service handles valid requests correctly and responds appropriately to validation errors. The use of MockMvcResultMatchers allows for detailed assertions on the response, including status codes, JSON paths, and error messages.
