package com.shuyang;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.google.common.io.Files;
import io.restassured.response.Response;

import static com.github.fge.jsonschema.SchemaVersion.DRAFTV4;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class TestUtils {
    private static final String SWAGGER_SCHEMA = "swagger.json";

    public static final String PET_STORE_URL = "https://petstore.swagger.io/v2";
    private static final JsonSchemaFactory jsonSchemaFactory = JsonSchemaFactory.newBuilder()
            .setValidationConfiguration(ValidationConfiguration.newBuilder().setDefaultVersion(DRAFTV4).freeze())
            .freeze();

    public static String loadFile(String filename) throws IOException {
            File addPet = new File(filename);
            return Files.toString(addPet, Charset.defaultCharset());
    }

    public static String loadTestDate(String filename) throws IOException {
        return loadFile("src/test/resources/" + filename);
    }

    public static void checkSchema(Response response) {
        response.then().assertThat().body(matchesJsonSchemaInClasspath(SWAGGER_SCHEMA).using(jsonSchemaFactory));
    }
}
