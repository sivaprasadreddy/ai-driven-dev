# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview
This is a Spring Boot 3.5.4 URL Shortener REST API application built with Java 24. The project demonstrates AI-driven development using various AI agents like Junie, Claude Code, and Gemini CLI.

## Architecture
- **Framework**: Spring Boot with Spring Web, Spring Data JPA, and Spring Actuator
- **Database**: MariaDB with Flyway migrations 
- **Testing**: TestContainers with MariaDB for integration tests
- **Package Structure**: `com.sivalabs.urlshortener`
- **API Specifications**: Detailed specs in `/specs/` folder with 8 endpoints covering user authentication, URL management, and admin functions

## Common Development Commands

### Build and Run
```bash
./mvnw clean compile                    # Compile the project
./mvnw spring-boot:run                 # Run the application (port 8080)
./mvnw clean package                   # Build JAR file
```

### Testing
```bash
./mvnw test                            # Run all tests
./mvnw test -Dtest=ClassNameTest       # Run specific test class
./mvnw test -Dtest=ClassNameTest#methodName  # Run specific test method
```

### Database
- Flyway migrations are located in `src/main/resources/db/migration/`
- Application uses MariaDB in production, TestContainers for testing
- Connection configured via `application.properties`

## Key Features to Implement
Based on the specifications in `/specs/`, the API should support:

1. User authentication with JWT tokens (`/api/login`)
2. User registration (`/api/register`)
3. URL shortening with optional privacy and expiration (`/api/short-urls`)
4. URL access tracking and redirection
5. User-specific URL management
6. Admin functionality for URL oversight
7. Paginated responses following standard patterns

## API Design Patterns
- RESTful endpoints with `/api` prefix
- JWT-based authentication for protected endpoints
- RFC 9457 compliant ProblemDetail error responses
- Standard HTTP status codes (200, 201, 400, 401, etc.)
- JSON request/response format with proper Content-Type headers

## Development Notes
- Java 24 with Spring Boot 3.5.4
- Maven wrapper (`mvnw`) for consistent builds
- No database migrations exist yet in `/db/migration/`
- Application name configured as "ai-driven-dev" in properties