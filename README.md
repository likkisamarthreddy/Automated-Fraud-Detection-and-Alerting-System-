# Automated Fraud Detection and Alerting System

Automated Fraud Detection and Alerting System with FraudGuard Console
 Project Overview

The Automated Fraud Detection and Alerting System is designed to detect fraudulent transactions in real time using rule-based checks and machine learning techniques. The system processes high-volume transactions with low latency and generates alerts for suspicious activities, enabling quick response and manual review when required.

## Architecture

- **Frontend**: FraudGuard Console (React + Vite + TypeScript)
- **Backend**: Microservices architecture (Java Spring Boot)
  - API Gateway (Port 8080)
  - Auth Service (Port 8081)
  - Transaction Service (Port 8082)
  - Fraud Engine Service (Port 8083)
  - Alert Service (Port 8084)
- **Infrastructure**: MySQL, Redis, Kafka

 Objectives

Detect fraudulent transactions in real time

Reduce false positives using intelligent fraud scoring

Provide instant alerts to operations teams

Ensure secure, scalable, and reliable transaction processing

## Getting Started

### Development Mode

1. **Start Backend Services**:
   ```bash
   docker-compose up -d mysql redis kafka zookeeper
   # Then start individual services or use docker-compose for all
   ```

2. **Start FraudGuard Console**:
   ```bash
   cd fraudguard-console-main
   npm install
   npm run dev
   ```
   Frontend will be available at: http://localhost:3000
   Backend API Gateway at: http://localhost:8080

### Production Mode

```bash
docker-compose up -d
```
FraudGuard Console will be available at: http://localhost:3001

 System Features:-

Real-time transaction ingestion

## System Features

- Rule-based fraud detection
- Risk score calculation
- Fraud decision pipeline (Allow / Alert)
- Real-time dashboard
- Alert management
- Audit logging support


 System Requirements:-
1.Technical Requirements

Real-time processing with low latency

High throughput support (10k+ TPS)

Secure data handling

Horizontal scalability

2.Non-Technical Requirements

99.99% availability

GDPR & PCI-DSS compliance

High reliability and fault tolerance

Easy maintenance and monitoring

3.Software Requirements

Operating System: Linux

Programming Languages: Java / Python

Message Streaming: Apache Kafka

Database: PostgreSQL / MySQL

Containerization: Docker, Kubernetes

4.Hardware Requirements

Multi-core servers

Minimum 16–32 GB RAM

SSD storage

High-speed network connectivity



## Future Enhancements

- ML-based fraud scoring
- Behavioral anomaly detection
- Email and dashboard alerts
- Horizontal scaling with containers
