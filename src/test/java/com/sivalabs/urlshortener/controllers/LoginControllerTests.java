package com.sivalabs.urlshortener.controllers;

import com.sivalabs.urlshortener.BaseIntegrationTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Sql("/test-data.sql")
class LoginControllerTests extends BaseIntegrationTest {

    @Test
    void shouldLoginSuccessfullyWithValidCredentials() {
        String loginRequest = """
            {
                "email": "user@example.com",
                "password": "password123"
            }
            """;

        given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
        .when()
                .post("/api/login")
        .then()
                .statusCode(200)
                .body("token", notNullValue())
                .body("token", not(emptyString()))
                .body("expiresAt", notNullValue())
                .body("email", equalTo("user@example.com"))
                .body("name", equalTo("Test User"))
                .body("role", equalTo("ROLE_USER"));
    }

    @Test
    void shouldReturnUnauthorizedForInvalidCredentials() {
        String loginRequest = """
            {
                "email": "user@example.com",
                "password": "wrongpassword"
            }
            """;

        given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
        .when()
                .post("/api/login")
        .then()
                .statusCode(401);
    }

    @Test
    void shouldReturnUnauthorizedForNonExistentUser() {
        String loginRequest = """
            {
                "email": "nonexistent@example.com",
                "password": "password123"
            }
            """;

        given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
        .when()
                .post("/api/login")
        .then()
                .statusCode(401);
    }

    @Test
    void shouldReturnBadRequestForInvalidEmailFormat() {
        String loginRequest = """
            {
                "email": "invalid-email",
                "password": "password123"
            }
            """;

        given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
        .when()
                .post("/api/login")
        .then()
                .statusCode(400)
                .body("type", equalTo("about:blank"))
                .body("title", equalTo("Bad Request"))
                .body("status", equalTo(400))
                .body("detail", equalTo("Validation failed"))
                .body("errors.email", equalTo("Email should be valid"))
                .body("instance", equalTo("/api/login"));
    }

    @Test
    void shouldReturnBadRequestForMissingFields() {
        String loginRequest = """
            {
                "email": "user@example.com"
            }
            """;

        given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
        .when()
                .post("/api/login")
        .then()
                .statusCode(400)
                .body("type", equalTo("about:blank"))
                .body("title", equalTo("Bad Request"))
                .body("status", equalTo(400))
                .body("detail", equalTo("Validation failed"))
                .body("errors.password", equalTo("Password is required"))
                .body("instance", equalTo("/api/login"));
    }

    @Test
    void shouldReturnBadRequestForEmptyRequestBody() {
        given()
                .contentType(ContentType.JSON)
                .body("{}")
        .when()
                .post("/api/login")
        .then()
                .statusCode(400)
                .body("type", equalTo("about:blank"))
                .body("title", equalTo("Bad Request"))
                .body("status", equalTo(400))
                .body("detail", equalTo("Validation failed"))
                .body("errors", hasKey("email"))
                .body("errors", hasKey("password"))
                .body("instance", equalTo("/api/login"));
    }

    @Test
    void shouldReturnBadRequestForBlankPassword() {
        String loginRequest = """
            {
                "email": "user@example.com",
                "password": ""
            }
            """;

        given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
        .when()
                .post("/api/login")
        .then()
                .statusCode(400)
                .body("type", equalTo("about:blank"))
                .body("title", equalTo("Bad Request"))
                .body("status", equalTo(400))
                .body("detail", equalTo("Validation failed"))
                .body("errors.password", equalTo("Password is required"))
                .body("instance", equalTo("/api/login"));
    }

    @Test
    void shouldReturnBadRequestForBlankEmail() {
        String loginRequest = """
            {
                "email": "",
                "password": "password123"
            }
            """;

        given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
        .when()
                .post("/api/login")
        .then()
                .statusCode(400)
                .body("type", equalTo("about:blank"))
                .body("title", equalTo("Bad Request"))
                .body("status", equalTo(400))
                .body("detail", equalTo("Validation failed"))
                .body("errors.email", equalTo("Email is required"))
                .body("instance", equalTo("/api/login"));
    }
}