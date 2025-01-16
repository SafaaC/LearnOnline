# Online Learning Platform

## Overview

The **Online Learning Platform** is a microservices-based web application that allows users to manage and enroll in courses, authenticate users, and make payments. The application uses **Spring Boot** for the backend and **React.js** for the frontend, leveraging **microservices architecture** for scalability and flexibility.

### Features:
- User authentication and profile management.
- CRUD operations for courses.
- Payment integration (Paytm/Stripe).
- API Gateway for centralized routing and JWT-based security.
- Eureka server for service discovery.
  
---

## Architecture

### Backend Microservices:

1. **Eureka Server**:
   - Acts as the service registry for all microservices.
   - Built using **Spring Cloud Eureka**.

2. **API Gateway**:
   - Routes client requests to the appropriate microservice.
   - Built using **Spring Cloud Gateway**.
   - Handles JWT authentication and centralized routing.

3. **User Service**:
   - Manages user registration, login, and profile data.
   - Built with **Spring Boot** and **JPA** for database interaction.
   
4. **Course Service**:
   - Manages course details: adding, fetching, updating, and deleting courses.
   - Built with **Spring Boot** and **JPA**.

5. **Payment Service**:
   - Handles course payment processing using external payment gateways.
   - Integrates with services like **Paytm/Stripe**.

---

### Frontend:

- **React.js** application for the user interface.
- Fetches data from the backend through the **API Gateway**.
- Implements **Axios** for API calls and manages **CORS** issues between frontend and backend.

---

## Technologies Used

- **Frontend**: 
  - React.js, Axios, CORS configuration for API communication.
  
- **Backend**:
  - Spring Boot, Spring Cloud (Eureka Server, API Gateway), JPA/Hibernate (for database), JWT Authentication, Payment Gateway (Paytm or Stripe).
  
- **Database**: 
  - MySQL or PostgreSQL for storing user and course data.

---

## Project Structure

