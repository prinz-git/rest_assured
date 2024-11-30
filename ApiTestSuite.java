package com.restassured;

import static io.restassured.RestAssured.*;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static org.hamcrest.Matchers.*;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;

import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Epic("API Testing with Rest Assured")
@Feature("Comprehensive API Tests Using Rest-Assured")
@Story("Numerical and String Assertions with Rest-Assured")
public class ApiTestSuite {

    private static RequestSpecBuilder requestSpecBuilder;
    private static ResponseSpecBuilder responseSpecBuilder;
    private static ResponseSpecification responseSpecification;
    private static RequestSpecification requestSpecification;

    private static final String BASE_URL = "https://reqres.in/api/users/";

    @BeforeClass
    public void setUp() {
        requestSpecBuilder = new RequestSpecBuilder()
            .setBaseUri(BASE_URL)
            .addQueryParam("page", 2)
            .addFilter(new RequestLoggingFilter())
            .addFilter(new ResponseLoggingFilter());

        responseSpecBuilder = new ResponseSpecBuilder()
            .expectStatusCode(200)
            .expectBody("page", equalTo(2));

        responseSpecification = responseSpecBuilder.build();
        requestSpecification = requestSpecBuilder.build();
    }

    @DataProvider
    public Iterator<Object[]> authenticationData() {
        List<Object[]> data = new ArrayList<>();
        data.add(new Object[]{"eve.holt@reqres.in", "pistol"});
        return data.iterator();
    }

    @DataProvider(name = "deleteUser")
    public Iterator<Object[]> deleteUserData() {
        List<Object[]> data = new ArrayList<>();
        data.add(new Object[]{2});
        return data.iterator();
    }

    @Test
    @Description("Test for validating numerical assertions using Rest-Assured")
    @Severity(SeverityLevel.NORMAL)
    public void validateNumberAssertions() {
        given()
            .queryParam("page", 2)
            .get(BASE_URL)
        .then()
            .statusCode(200)
            .body("page", equalTo(2))
            .body("per_page", greaterThan(4))
            .body("per_page", greaterThanOrEqualTo(6))
            .body("total", lessThan(14))
            .body("total_pages", lessThanOrEqualTo(3));
    }

    @Test
    @Description("Test for validating 'Greater Than' assertions with Rest-Assured")
    @Severity(SeverityLevel.NORMAL)
    public void validateGreaterThanAssertions() {
        given()
            .queryParam("page", 2)
            .get(BASE_URL)
        .then()
            .statusCode(200)
            .body("per_page", greaterThan(4))
            .body("per_page", greaterThanOrEqualTo(6));
    }

    @Test
    @Description("Test for validating 'Less Than' assertions with Rest-Assured")
    @Severity(SeverityLevel.NORMAL)
    public void validateLessThanAssertions() {
        given()
            .queryParam("page", 2)
            .get(BASE_URL)
        .then()
            .statusCode(200)
            .body("total", lessThan(14))
            .body("total_pages", lessThanOrEqualTo(3));
    }

    @Test
    @Description("Test for validating String-related assertions with Rest-Assured")
    @Severity(SeverityLevel.NORMAL)
    public void validateStringAssertions() {
        given().spec(requestSpecification)
            .get()
        .then()
            .spec(responseSpecification)
            .body("data[0].first_name", equalTo("Michael"))
            .body("data[0].first_name", equalToIgnoringCase("MICHael"))
            .body("data[0].email", containsString("michael.lawson"))
            .body("data[0].last_name", startsWith("L"))
            .body("data[0].last_name", endsWith("n"))
            .body("data[1].first_name", equalToCompressingWhiteSpace("    Lindsay "));
    }

    @Test(dataProvider = "authenticationData")
    @Description("Test for registering a user and validating authentication token")
    @Severity(SeverityLevel.NORMAL)
    public void validateAuthenticationToken(String email, String password) {
        AuthenticationPojo requestBody = new AuthenticationPojo(email, password);

        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .post(BASE_URL + "/api/register")
        .then()
            .statusCode(200)
            .body("id", notNullValue())
            .body("token", notNullValue());
    }

    @Test(dataProvider = "authenticationData")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test to print authentication token")
    public void printAuthToken(String email, String password) {
        // LOG.info("Token: " + getAuthToken(email, password).get("token"));
    }

    @Test(dataProvider = "deleteUser")
    @Description("Test for sending DELETE request and validating response")
    @Severity(SeverityLevel.NORMAL)
    @Story("Execute Delete requests using Rest-Assured")
    public void deleteUserTest(int userId) {
        given()
            .delete(BASE_URL + userId)
        .then()
            .statusCode(204);
    }

    @Test
    public void validateExpect() {
        expect()
            .when()
            .get(BASE_URL + "2")
        .then()
            .statusCode(200);
    }

    @Test
    public void validateBodyUsingExpect() {
        expect()
            .body("data.email", equalTo("janet.weaver@reqres.in"))
            .body("data.id", equalTo(2))
            .when()
            .get(BASE_URL + "2")
        .then()
            .statusCode(200);
    }

    @Test
    @Description("Test for executing GET request with Rest-Assured configurations")
    @Severity(SeverityLevel.CRITICAL)
    @Story("API Test Execution with Rest-Assured Configurations")
    public void getRequestWithConfigTest() {
        given()
            .get(BASE_URL + "2")
        .then()
            .statusCode(200)
            .body("data.first_name", equalTo("Janet"));
    }

    @AfterMethod
    public void logExecutionTime(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        long executionTime = result.getEndMillis() - result.getStartMillis();
        System.out.println("Execution time for " + methodName + ": " + executionTime + "ms");
    }

}