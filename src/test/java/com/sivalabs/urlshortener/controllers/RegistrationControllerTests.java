package com.sivalabs.urlshortener.controllers;

import com.sivalabs.urlshortener.BaseIntegrationTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Sql("/test-data.sql")
class RegistrationControllerTests extends BaseIntegrationTest {

    @Test
    void shouldRegisterUserSuccessfully() {
        String requestBody = """
            {
                "email": "test@example.com",
                "password": "password123",
                "name": "Test User"
            }
            """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
        .when()
                .post("/api/users")
        .then()
                .statusCode(201)
                .body("email", equalTo("test@example.com"))
                .body("name", equalTo("Test User"))
                .body("role", equalTo("ROLE_USER"))
                .body("$", not(hasKey("password")));
    }

    @Test
    void shouldReturnConflictWhenEmailAlreadyExists() {
        // First registration
        String requestBody = """
            {
                "email": "duplicate@example.com",
                "password": "password123",
                "name": "First User"
            }
            """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
        .when()
                .post("/api/users")
        .then()
                .statusCode(201);

        // Second registration with same email
        String duplicateRequestBody = """
            {
                "email": "duplicate@example.com",
                "password": "differentPassword",
                "name": "Second User"
            }
            """;

        given()
                .contentType(ContentType.JSON)
                .body(duplicateRequestBody)
        .when()
                .post("/api/users")
        .then()
                .statusCode(400)
                .body("type", equalTo("about:blank"))
                .body("title", equalTo("Bad Request"))
                .body("status", equalTo(400))
                .body("detail", equalTo("Email already exists"))
                .body("instance", equalTo("/api/users"));
    }

    @Test
    void shouldReturnBadRequestForInvalidEmail() {
        String requestBody = """
            {
                "email": "invalid-email",
                "password": "password123",
                "name": "Test User"
            }
            """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
        .when()
                .post("/api/users")
        .then()
                .statusCode(400)
                .body("type", equalTo("about:blank"))
                .body("title", equalTo("Bad Request"))
                .body("status", equalTo(400))
                .body("detail", equalTo("Validation failed"))
                .body("errors.email", equalTo("Email should be valid"))
                .body("instance", equalTo("/api/users"));
    }

    @Test
    void shouldReturnBadRequestForMissingFields() {
        String requestBody = """
            {
                "email": "test@example.com"
            }
            """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
        .when()
                .post("/api/users")
        .then()
                .statusCode(400)
                .body("type", equalTo("about:blank"))
                .body("title", equalTo("Bad Request"))
                .body("status", equalTo(400))
                .body("detail", equalTo("Validation failed"))
                .body("errors", hasKey("password"))
                .body("errors", hasKey("name"))
                .body("instance", equalTo("/api/users"));
    }

    @Test
    void shouldReturnBadRequestForShortPassword() {
        String requestBody = """
            {
                "email": "test@example.com",
                "password": "123",
                "name": "Test User"
            }
            """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
        .when()
                .post("/api/users")
        .then()
                .statusCode(400)
                .body("type", equalTo("about:blank"))
                .body("title", equalTo("Bad Request"))
                .body("status", equalTo(400))
                .body("detail", equalTo("Validation failed"))
                .body("errors.password", equalTo("Password must be at least 6 characters"))
                .body("instance", equalTo("/api/users"));
    }

    @Test
    void shouldReturnBadRequestForEmptyRequestBody() {
        given()
                .contentType(ContentType.JSON)
                .body("{}")
        .when()
                .post("/api/users")
        .then()
                .statusCode(400)
                .body("type", equalTo("about:blank"))
                .body("title", equalTo("Bad Request"))
                .body("status", equalTo(400))
                .body("detail", equalTo("Validation failed"))
                .body("errors", hasKey("email"))
                .body("errors", hasKey("password"))
                .body("errors", hasKey("name"))
                .body("instance", equalTo("/api/users"));
    }
}