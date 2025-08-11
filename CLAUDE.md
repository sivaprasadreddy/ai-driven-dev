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

## Spring Boot Guidelines

### 1. Prefer Constructor Injection over Field/Setter Injection
* Declare all the mandatory dependencies as `final` fields and inject them through the constructor.
* Spring will auto-detect if there is only one constructor, no need to add `@Autowired` on the constructor.
* Avoid field/setter injection in production code.

### 2. Prefer package-private over public for Spring components
* Declare Controllers, their request-handling methods, `@Configuration` classes and `@Bean` methods with default (package-private) visibility whenever possible. There's no obligation to make everything `public`.

### 3. Organize Configuration with Typed Properties
* Group application-specific configuration properties with a common prefix in `application.properties` or `.yml`.
* Bind them to `@ConfigurationProperties` classes with validation annotations so that the application will fail fast if the configuration is invalid.
* Prefer environment variables instead of profiles for passing different configuration properties for different environments.

### 4. Define Clear Transaction Boundaries
* Define each Service-layer method as a transactional unit.
* Annotate query-only methods with `@Transactional(readOnly = true)`.
* Annotate data-modifying methods with `@Transactional`.
* Limit the code inside each transaction to the smallest necessary scope.


### 5. Disable Open Session in View Pattern
* While using Spring Data JPA, disable the Open Session in View filter by setting ` spring.jpa.open-in-view=false` in `application.properties/yml.`

### 6. Separate Web Layer from Persistence Layer
* Don't expose entities directly as responses in controllers.
* Define explicit request and response record (DTO) classes instead.
* Apply Jakarta Validation annotations on your request records to enforce input rules.

### 7. Follow REST API Design Principles
* **Versioned, resource-oriented URLs:** Structure your endpoints as `/api/v{version}/resources` (e.g. `/api/v1/orders`).
* **Consistent patterns for collections and sub-resources:** Keep URL conventions uniform (for example, `/posts` for posts collection and `/posts/{slug}/comments` for comments of a specific post).
* **Explicit HTTP status codes via ResponseEntity:** Use `ResponseEntity<T>` to return the correct status (e.g. 200 OK, 201 Created, 404 Not Found) along with the response body.
* Use pagination for collection resources that may contain an unbounded number of items.
* The JSON payload must use a JSON object as a top-level data structure to allow for future extension.
* Use snake_case or camelCase for JSON property names consistently.

### 8. Use Command Objects for Business Operations
* Create purpose-built command records (e.g., `CreateOrderCommand`) to wrap input data.
* Accept these commands in your service methods to drive creation or update workflows.

### 9. Centralize Exception Handling
* Define a global handler class annotated with `@ControllerAdvice` (or `@RestControllerAdvice` for REST APIs) using `@ExceptionHandler` methods to handle specific exceptions.
* Return consistent error responses. Consider using the ProblemDetails response format ([RFC 9457](https://www.rfc-editor.org/rfc/rfc9457)).

### 10. Actuator
* Expose only essential actuator endpoints (such as `/health`, `/info`, `/metrics`) without requiring authentication. All the other actuator endpoints must be secured.

### 11. Internationalization with ResourceBundles
* Externalize all user-facing text such as labels, prompts, and messages into ResourceBundles rather than embedding them in code.

### 12. Use Testcontainers for integration tests
* Spin up real services (databases, message brokers, etc.) in your integration tests to mirror production environments.

### 13. Use random port for integration tests
* When writing integration tests, start the application on a random available port to avoid port conflicts by annotating the test class with:

    ```java
    @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
    ```

### 14. Logging
* **Use a proper logging framework.**  
  Never use `System.out.println()` for application logging. Rely on SLF4J (or a compatible abstraction) and your chosen backend (Logback, Log4j2, etc.).

* **Protect sensitive data.**  
  Ensure that no credentials, personal information, or other confidential details ever appear in log output.

* **Guard expensive log calls.**  
  When building verbose messages at `DEBUG` or `TRACE` level, especially those involving method calls or complex string concatenations, wrap them in a level check or use suppliers:

    ```java
    if (logger.isDebugEnabled()) {
        logger.debug("Detailed state: {}",computeExpensiveDetais());
    }
    ```
