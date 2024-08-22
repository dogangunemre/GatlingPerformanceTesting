import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

public class SwaggerSimulation extends Simulation {

    // Feeder setup to read from a CSV file
    FeederBuilder<String> feeder = csv("pet_ids.csv").random();

    // Scenario for getting pet by ID
    ChainBuilder getPetById =
            feed(feeder)
                    .exec(
                            http("Get Pet by ID")
                                    .get("/pet/#{petId}")
                                    .check(status().is(200))
                    )
                    .pause(1);

    // Scenario for finding pets by status
    ChainBuilder findPetsByStatus =
            exec(
                    http("Find Pets by Status")
                            .get("/pet/findByStatus?status=available")
                            .check(status().is(200))
            )
                    .pause(1);

    // Scenario for adding a new pet
    ChainBuilder addNewPet =
            exec(
                    http("Add New Pet")
                            .post("/pet")
                            .body(StringBody("{\"id\": 12345, \"name\": \"New Pet\", \"status\": \"available\"}")).asJson()
                            .check(status().is(200))
            )
                    .pause(1);

    // Scenario for updating an existing pet
    ChainBuilder updatePet =
            exec(
                    http("Update Pet")
                            .put("/pet")
                            .body(StringBody("{\"id\": 12345, \"name\": \"Updated Pet\", \"status\": \"sold\"}")).asJson()
                            .check(status().is(200))
            )
                    .pause(1);

    // Scenario for deleting a pet
    ChainBuilder deletePet =
            exec(
                    http("Delete Pet")
                            .delete("/pet/12345")
                            .check(status().is(200))
            )
                    .pause(1);

    // HTTP Protocol Configuration
    HttpProtocolBuilder httpProtocol = http
            .baseUrl("https://petstore.swagger.io/v2")
            .acceptHeader("application/json")
            .userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3");

    // Scenario Setup
    ScenarioBuilder scn = scenario("Swagger Petstore API Test")
            .exec(getPetById, findPetsByStatus, addNewPet, updatePet, deletePet);

    {
        setUp(
                scn.injectOpen(rampUsers(10).during(10))
        ).protocols(httpProtocol);
    }
}
