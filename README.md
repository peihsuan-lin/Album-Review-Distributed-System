# Scalable Album Review Distributed System

## Introduction

This project is the coursework for CS6650 at Northeastern University. It is designed to simulate a high-load, scalable environment for handling album reviews using modern software architecture and technologies. It includes a detailed setup of both server-side and client-side components, employing AWS services like EC2 and DynamoDB, along with Apache Kafka for message processing.

## System Architecture

### Components

- **Swagger API**: Facilitates the interface for client-server communication. 
- **Server**:
  - **Album Servlet**: Handles all album-related requests.
  - **Review Servlet**: Manages review data (likes/dislikes).

### Technologies

- **AWS EC2**: Hosts the server and client applications ensuring scalability and flexibility.
- **Apache Kafka**: Used for message queuing, replacing RabbitMQ for enhanced performance and reliability.
- **AWS DynamoDB**: Provides a NoSQL database solution for storing and retrieving data efficiently.

## Setup and Installation

### Prerequisites

- Java JDK 11
- Apache Maven
- AWS Account

### Configuration

1. Set up AWS EC2 instances for deploying the application.
2. Install Apache Kafka on EC2 and configure it for high availability.
3. Set up DynamoDB tables with necessary schemas.

### Running the Application

```bash
# Start the server
java -jar server.jar

# Execute the client
java -jar client.jar
```

## Usage

### Adding an Album

```bash
curl -X POST http://localhost:8080/albums -d '{"name":"New Album"}'
```

### Retrieving Reviews

```bash
curl -X GET http://localhost:8080/reviews/{albumId}
```

### Posting a Review

```bash
curl -X POST http://localhost:8080/reviews -d '{"albumId":"123", "like":true}'
```

## Performance Metrics
For detailed metric performance please check report for each files.


