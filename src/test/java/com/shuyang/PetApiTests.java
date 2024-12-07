package com.shuyang;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static com.shuyang.TestUtils.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertTrue;

public class PetApiTests {
    @Before
    public void init() {
        RestAssured.baseURI = PET_STORE_URL;
    }

    @Test
    public void testCreatePet() throws IOException {

        // Create a pet, test post
        RequestSpecification request = given().basePath("/pet")
                .header("Content-Type", "application/json")
                .body(loadTestDate("create-pet.json"));

        Response response = request.post("/");
        checkSchema(response);

        response.then().assertThat().statusLine("HTTP/1.1 200 OK");

        response.then().assertThat().body("name", equalTo("Frederick"));

        Long petId = response.jsonPath().get("id");
        System.out.println("id: " + petId);

        // Test get
        // Using the newly generated id to check the result is right
        response = given().basePath("/pet").header("Content-Type", "application/json").get(petId.toString());
        checkSchema(response);
        response.then().assertThat()
                .body("name", equalTo("Frederick"))
                .body("photoUrls", hasSize(1))
                .body("photoUrls", contains("https://www.google.com/url?sa=i&url=https%3A%2F%2Fen.wikipedia.org%2Fwiki%2FGerman_Shepherd&psig=AOvVaw0ki32mYeSR26TmFz1gap_0&ust=1733622025028000&source=images&cd=vfe&opi=89978449&ved=0CBMQjhxqFwoTCNiC54XElIoDFQAAAAAdAAAAABAE"));

        // Update a pet using post
        given().basePath("/pet").formParam("name", "Hans").formParam("status", "pending")
                .post(petId.toString()).then().assertThat().statusCode(200);

        // check name is Hans and status is pending
        response = given().basePath("/pet").header("Content-Type", "application/json").get(petId.toString());
        checkSchema(response);
        response.then().assertThat().body("name", equalTo("Hans"))
                .assertThat().body("status", equalTo("pending"))
                .assertThat().body("photoUrls", hasSize(1))
                .assertThat().body("photoUrls", contains("https://www.google.com/url?sa=i&url=https%3A%2F%2Fen.wikipedia.org%2Fwiki%2FGerman_Shepherd&psig=AOvVaw0ki32mYeSR26TmFz1gap_0&ust=1733622025028000&source=images&cd=vfe&opi=89978449&ved=0CBMQjhxqFwoTCNiC54XElIoDFQAAAAAdAAAAABAE"));

        // Test PUT to update existing pet
        String reqString = loadTestDate("create-pet.json").replaceFirst("0", petId.toString());
        response = given().basePath("/pet").header("Content-Type", "application/json").body(reqString).put();
        response.then().assertThat().statusCode(200);
        checkSchema(response);
        response.then().assertThat().body("name", equalTo("Frederick"))
                .assertThat().body("status", equalTo("available"))
                .assertThat().body("photoUrls", hasSize(1))
                .assertThat().body("photoUrls", contains("https://www.google.com/url?sa=i&url=https%3A%2F%2Fen.wikipedia.org%2Fwiki%2FGerman_Shepherd&psig=AOvVaw0ki32mYeSR26TmFz1gap_0&ust=1733622025028000&source=images&cd=vfe&opi=89978449&ved=0CBMQjhxqFwoTCNiC54XElIoDFQAAAAAdAAAAABAE"));

        // UploadImage test
        response = given().basePath("/pet").param("additionalMetadata", "new photo")
                .param("file", "src/test/resources/Fred.png").post(petId.toString());
        response.then().assertThat().statusCode(200);


        // find by status
        response = given().basePath("/pet").queryParam("status", "available").get("findByStatus");
        response.then().assertThat().body("", not(empty()));
        List<String> ids = response.body().jsonPath().get("id");
        assertTrue(ids.contains(petId));


        // delete the pet
        response = given().basePath("/pet").queryParam("api_key", "special-key").delete(petId.toString());
        response.then().assertThat().statusCode(200);
        response = given().basePath("/pet").header("Content-Type", "application/json").get(petId.toString());
        response.then().assertThat().statusCode(404);
    }
}
