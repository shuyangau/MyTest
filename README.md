### Automation Test over Swagger Pet Store API
---
This test suite were developed using Java, RestAssured framework, and Maven.

To run the tests:
`mvn test`

The source code were divided into 3 Java file:
1. PetApiTests
2. StoreApiTests
3. UserApiTests

Test resources are under `src/test/resource`

The main idea we design the tests
1. execute api access
2. collect the result
3. verify the result follows the swagger schema by a util function `checkSchema`
4. verify the fields are carrying correct value
