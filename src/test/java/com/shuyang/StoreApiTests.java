package com.shuyang;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.shuyang.TestUtils.PET_STORE_URL;
import static com.shuyang.TestUtils.loadTestDate;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasKey;

public class StoreApiTests {
    @Before
    public void init() {
        RestAssured.baseURI = PET_STORE_URL;
    }

    @Test
    public void testInventory() {
        Response response = given().basePath("/store").get("inventory");
        response.then().assertThat().statusCode(200);

        response.then().assertThat().body("$", hasKey("sold"));
        response.then().assertThat().body("$", hasKey("pending"));
        response.then().assertThat().body("$", hasKey("available"));
    }

    @Test
    public void testOrder() throws IOException {
        Response response = given().basePath("store").header("Content-Type", "application/json")
                .body(loadTestDate("create-order.json")).post("order");

        response.then().assertThat().statusCode(200);

        Long id = response.jsonPath().getLong("id");

        // test get with id
        response = given().basePath("/store").get("/order/" + id);
        response.then().assertThat().statusCode(200);

        // delete order
        response = given().basePath("/store").delete("/order/" + id);
        response.then().assertThat().statusCode(200);

        // verify delete is successful
        response = given().basePath("/store").get("/order/" + id);
        response.then().assertThat().statusCode(404);
    }
}
