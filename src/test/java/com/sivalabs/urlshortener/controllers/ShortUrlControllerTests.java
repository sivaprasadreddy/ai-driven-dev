package com.sivalabs.urlshortener.controllers;

import com.sivalabs.urlshortener.BaseIntegrationTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Sql("/test-data.sql")
class ShortUrlControllerTests extends BaseIntegrationTest {

    @Test
    void shouldGetPublicShortUrlsWithDefaultPagination() {
        given()
                .accept(ContentType.JSON)
        .when()
                .get("/api/short-urls")
        .then()
                .statusCode(200)
                .body("data.size()", equalTo(10)) // 20 public short URLs from test data
                .body("pageNumber", equalTo(1))
                .body("totalElements", equalTo(20))
                .body("isFirst", equalTo(true))
                .body("isLast", equalTo(false))
                .body("hasNext", equalTo(true))
                .body("hasPrevious", equalTo(false))
                .body("totalPages", equalTo(2));
    }

    @Test
    void shouldGetPublicShortUrlsWithExplicitPageParameter() {
        given()
                .accept(ContentType.JSON)
        .when()
                .get("/api/short-urls?page=1")
        .then()
                .statusCode(200)
                .body("data.size()", equalTo(10))
                .body("pageNumber", equalTo(1));
    }

    @Test
    void shouldReturnEmptyDataForNonExistentPage() {
        given()
                .accept(ContentType.JSON)
        .when()
                .get("/api/short-urls?page=999")
        .then()
                .statusCode(400)
                .body("type", equalTo("about:blank"))
                .body("title", equalTo("Bad Request"))
                .body("status", equalTo(400))
                .body("detail", equalTo("Invalid page number. Max valid page number is 2"))
                .body("instance", equalTo("/api/short-urls"));
    }

    @Test
    void shouldHandleInvalidPageParameter() {
        // Page 0 should be treated as page 1
        given()
                .accept(ContentType.JSON)
        .when()
                .get("/api/short-urls?page=0")
        .then()
                .statusCode(200)
                .body("data.size()", equalTo(10))
                .body("pageNumber", equalTo(1));
    }

    @Test
    void shouldHandleNegativePageParameter() {
        // Negative page should be treated as page 1
        given()
                .accept(ContentType.JSON)
        .when()
                .get("/api/short-urls?page=-5")
        .then()
                .statusCode(200)
                .body("data.size()", equalTo(10))
                .body("pageNumber", equalTo(1));
    }

    @Test
    void shouldReturnOnlyPublicShortUrls() {
        given()
                .accept(ContentType.JSON)
        .when()
                .get("/api/short-urls")
        .then()
                .statusCode(200)
                .body("data", hasSize(10))
                // No private URLs should be returned
                .body("data.findAll { it.isPrivate == true }", hasSize(0))
                // All returned URLs should be public
                .body("data.findAll { it.isPrivate == false }", hasSize(10));
    }

    @Test
    void shouldReturnShortUrlsInDescendingOrderByCreatedAt() {
        given()
                .accept(ContentType.JSON)
        .when()
                .get("/api/short-urls")
        .then()
                .statusCode(200)
                .body("data.size()", equalTo(10))
                .body("data[0].shortKey", equalTo("ajj234"))
                .body("data[1].shortKey", equalTo("aii901"))
                .body("data[2].shortKey", equalTo("agg345"))
                .body("data[3].shortKey", equalTo("aff012"))
                .body("data[4].shortKey", equalTo("add456"))
                .body("data[5].shortKey", equalTo("acc123"))
                .body("data[6].shortKey", equalTo("zaa567"))
                .body("data[7].shortKey", equalTo("wxy234"))
                .body("data[8].shortKey", equalTo("tuv901"))
                .body("data[9].shortKey", equalTo("nop345"));
    }

    @Test
    void shouldReturnCorrectShortUrlStructure() {
        given()
                .accept(ContentType.JSON)
        .when()
                .get("/api/short-urls")
        .then()
                .statusCode(200)
                .body("data[0].id", notNullValue())
                .body("data[0].shortKey", notNullValue())
                .body("data[0].originalUrl", notNullValue())
                .body("data[0].isPrivate", equalTo(false))
                .body("data[0].clickCount", notNullValue())
                .body("data[0].createdAt", notNullValue())
                .body("data[0].expiresAt", anyOf(nullValue(), notNullValue()));
    }

    @Test
    void shouldIncludeCreatedByUserInformation() {
        given()
                .accept(ContentType.JSON)
        .when()
                .get("/api/short-urls")
        .then()
                .statusCode(200)
                .body("data.findAll { it.createdBy != null }", hasSize(greaterThan(0)))
                .body("data.find { it.shortKey == 'ajj234' }.createdBy.id", equalTo(2))
                .body("data.find { it.shortKey == 'ajj234' }.createdBy.name", equalTo("Auth User"))
                .body("data.find { it.shortKey == 'aii901' }.createdBy.id", equalTo(4))
                .body("data.find { it.shortKey == 'aii901' }.createdBy.name", equalTo("Second User"));
    }

    @Test
    void shouldHandleShortUrlsWithoutCreatedBy() {
        given()
                .accept(ContentType.JSON)
        .when()
                .get("/api/short-urls")
        .then()
                .statusCode(200)
                .body("data.findAll { it.createdBy == null }", hasSize(greaterThan(0)))
                .body("data.find { it.shortKey == 'jkl012' }.createdBy", nullValue())
                .body("data.find { it.shortKey == 'vwx234' }.createdBy", nullValue());
    }

    @Test
    void shouldIncludeCorrectPaginationMetadata() {
        given()
                .accept(ContentType.JSON)
        .when()
                .get("/api/short-urls")
        .then()
                .statusCode(200)
                .body("pageNumber", equalTo(1))
                .body("totalPages", equalTo(2))
                .body("totalElements", equalTo(20))
                .body("isFirst", equalTo(true))
                .body("isLast", equalTo(false))
                .body("hasNext", equalTo(true))
                .body("hasPrevious", equalTo(false));
    }
}