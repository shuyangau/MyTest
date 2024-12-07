package com.shuyang;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.shuyang.TestUtils.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class UserApiTests {
    @Before
    public void init() {
        RestAssured.baseURI = PET_STORE_URL;
    }

    @Test
    public void test() throws IOException {
        // Create a user, test post
        RequestSpecification request = given().basePath("/user")
                .header("Content-Type", "application/json")
                .body(loadTestDate("create-user.json"));

        Response response = request.post("/");
        checkSchema(response);

        response.then().assertThat().statusLine("HTTP/1.1 200 OK");

        // looking for the new user, testing get
        response = given().basePath("/user").get("jackson");
        checkSchema(response);
        response.then().assertThat().statusCode(200);
        response.then().assertThat().body("username", equalTo("jackson"));
        response.then().assertThat().body("firstName", equalTo("jackson"));
        response.then().assertThat().body("lastName", equalTo("flag"));
        response.then().assertThat().body("email", equalTo("jack@abc.com"));
        response.then().assertThat().body("password", equalTo("password-123"));
        response.then().assertThat().body("phone", equalTo("0400123987"));

        Long userId = response.jsonPath().get("id");
        System.out.println("user id: " + userId);

        // login
        response = given().basePath("/user").queryParam("username", "jackson")
                .queryParam("password", "password-123").get("login");
        response.then().assertThat().statusCode(200);
        // Update user, test PUT
        response = given().basePath("/user").header("Content-Type", "application/json")
                .body(loadTestDate("update-user.json")).put(userId.toString());
        response.then().assertThat().statusCode(200);

        // Verify update is successful
        // looking for the new user, testing get
        response = given().basePath("/user").get("jackson");
        checkSchema(response);
        response.then().assertThat().statusCode(200);
        response.then().assertThat().body("username", equalTo("jackson"));
        response.then().assertThat().body("firstName", equalTo("jackson"));
        response.then().assertThat().body("lastName", equalTo("flag"));
        response.then().assertThat().body("email", equalTo("jack@abc.com"));
        response.then().assertThat().body("password", equalTo("password-123"));
        response.then().assertThat().body("phone", equalTo("0400123987"));


        // logout
        response = given().basePath("/user").get("logout");
        response.then().assertThat().statusCode(200);
    }


    @Test
    public void testLoginNonExistUser() {
        Response response = given().basePath("/user").get("xxx");
        response.then().assertThat().statusCode(404);
    }


    @Test
    public void testCreateWithArray() throws IOException {
        Response response = given().basePath("user").header("Content-Type", "application/json")
                .body(loadTestDate("create-users.json")).post("createWithArray");
        response.then().assertThat().statusCode(200);
    }

    @Test
    public void testCreateWithList() throws IOException {
        Response response = given().basePath("user").header("Content-Type", "application/json")
                .body(loadTestDate("create-users.json")).post("createWithList");
        response.then().assertThat().statusCode(200);
    }
}
