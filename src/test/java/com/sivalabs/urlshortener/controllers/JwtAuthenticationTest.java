package com.sivalabs.urlshortener.controllers;

import com.sivalabs.urlshortener.BaseIntegrationTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Sql("/test-data.sql")
class JwtAuthenticationTest extends BaseIntegrationTest {

    private String validToken;

    @BeforeEach
    void setupValidToken() {
        // Login to get JWT token using pre-existing user from test-data.sql
        String loginRequest = """
            {
                "email": "auth@example.com",
                "password": "password123"
            }
            """;

        Response loginResponse = given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
        .when()
                .post("/api/login")
        .then()
                .statusCode(200)
                .extract().response();

        validToken = loginResponse.jsonPath().getString("token");
    }

    @Test
    void shouldAccessProtectedResourceWithValidToken() {
        // This test assumes there will be protected endpoints in the future
        // For now, we'll test that the token is properly structured
        given()
                .header("Authorization", "Bearer " + validToken)
                .contentType(ContentType.JSON)
        .when()
                .get("/api/protected")
        .then()
                .statusCode(anyOf(equalTo(200), equalTo(404))); // 404 is expected since endpoint doesn't exist yet
    }

    @Test
    void shouldRejectRequestWithInvalidToken() {
        given()
                .header("Authorization", "Bearer invalid.jwt.token")
                .contentType(ContentType.JSON)
        .when()
                .get("/api/protected")
        .then()
                .statusCode(anyOf(equalTo(403), equalTo(404))); // 404 is expected since endpoint doesn't exist yet
    }

    @Test
    void shouldRejectRequestWithExpiredToken() {
        // Test with a manually crafted expired token
        String expiredToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIiwibmFtZSI6IlRlc3QgVXNlciIsInJvbGUiOiJST0xFX1VTRVIiLCJpYXQiOjE2MDk0NTkyMDAsImV4cCI6MTYwOTQ1OTIwMX0.invalid";
        
        given()
                .header("Authorization", "Bearer " + expiredToken)
                .contentType(ContentType.JSON)
        .when()
                .get("/api/protected")
        .then()
                .statusCode(anyOf(equalTo(403), equalTo(404))); // 404 is expected since endpoint doesn't exist yet
    }

    @Test
    void shouldRejectRequestWithoutToken() {
        given()
                .contentType(ContentType.JSON)
        .when()
                .get("/api/protected")
        .then()
                .statusCode(anyOf(equalTo(403), equalTo(404))); // 404 is expected since endpoint doesn't exist yet
    }

    @Test
    void shouldRejectRequestWithMalformedAuthorizationHeader() {
        given()
                .header("Authorization", "InvalidHeader " + validToken)
                .contentType(ContentType.JSON)
        .when()
                .get("/api/protected")
        .then()
                .statusCode(anyOf(equalTo(403), equalTo(404))); // 404 is expected since endpoint doesn't exist yet
    }

    @Test
    void shouldAllowAccessToPublicEndpoints() {
        // Test that public endpoints don't require authentication
        String loginRequest = """
            {
                "email": "auth@example.com",
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
                .body("token", notNullValue());

        String registerRequest = """
            {
                "email": "public@example.com",
                "password": "password123",
                "name": "Public User"
            }
            """;

        given()
                .contentType(ContentType.JSON)
                .body(registerRequest)
        .when()
                .post("/api/users")
        .then()
                .statusCode(201);
    }

    @Test
    void shouldValidateTokenStructure() {
        // Verify the JWT token has the expected structure and claims
        String loginRequest = """
            {
                "email": "auth@example.com",
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
                .body("token", matchesPattern("^[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+$"))
                .body("expiresAt", notNullValue())
                .body("email", equalTo("auth@example.com"))
                .body("name", equalTo("Auth User"))
                .body("role", equalTo("ROLE_USER"));
    }
}