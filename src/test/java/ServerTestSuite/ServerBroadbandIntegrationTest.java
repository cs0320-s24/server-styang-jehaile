package ServerTestSuite;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ServerTestSuite.Mocks.MockBroadbandSource;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.Server.Broadband.BroadbandData;
import edu.brown.cs.student.main.Server.Broadband.BroadbandHandler.BroadbandDataSourceInterface;
import edu.brown.cs.student.main.Server.Broadband.BroadbandHandler.BroadbandHandler;
import edu.brown.cs.student.main.Server.Broadband.BroadbandHandler.CachingBroadbandDataSource;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;
/**
 *
 The ServerBroadbandIntegrationTest tests confirm that the API server works as expected the JUnit integration tests
 */

public class ServerBroadbandIntegrationTest {

  private final Type mapStringObject =
      Types.newParameterizedType(Map.class, String.class, Object.class);
  private JsonAdapter<Map<String, Object>> adapter;

  @BeforeEach
  public void setup() {
    // Re-initialize parser, state, etc. for every test method

    // Use *MOCKED* data when in this test environment.
    BroadbandDataSourceInterface mockedSource =
        new MockBroadbandSource("california", "orange", 90.3);
    Spark.get("/broadband", new BroadbandHandler(new CachingBroadbandDataSource(mockedSource)));
    Spark.awaitInitialization(); // don't continue until the server is listening

    Moshi moshi = new Moshi.Builder().build();
    adapter = moshi.adapter(mapStringObject);
  }

  @AfterEach
  public void tearDown() {
    // Gracefully stop Spark listening on both endpoints
    Spark.unmap("/broadband");
    Spark.awaitStop(); // don't proceed until the server is stopped
  }

  private HttpURLConnection tryRequest(String apiCall) throws IOException {
    // Configure the connection (but don't actually send a request yet)
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/" + apiCall);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();
    // The request body contains a Json object
    clientConnection.setRequestProperty("Content-Type", "application/json");
    // We're expecting a Json object in the response body
    clientConnection.setRequestProperty("Accept", "application/json");

    clientConnection.connect();
    return clientConnection;
  }

  /**
   * This test checks the broadband request and response works successfully by testing the resposne code,
   * and specific county percentage.
   * @throws IOException thrown if there is a failure displaying the json data
   */
  @Test
  public void testBroadbandRequestSuccess() throws IOException {
    /////////// LOAD DATASOURCE ///////////
    // Set up the request, make the request
    HttpURLConnection loadConnection = tryRequest("broadband?state=california&county=orange");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, loadConnection.getResponseCode());
    // Get the expected response: a success
    Map<String, Object> responseBody =
        adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals("Loaded successfully! :)", responseBody.get("responseType"));

    assertEquals(
        new BroadbandData(
            LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString(),
            "california",
            "orange",
            90.3)
            .toString(),
        responseBody.get("responseData").toString());
    // Notice we had to do something strange above, because the map is
    // from String to *Object*. Awkward testing caused by poor API design...

    loadConnection.disconnect();
  }

  /**
   * This test checks that if a broadband request was made without entering a state name the json states error and
   * instructs the user to input a state name.
   * @throws IOException thrown if there is a failure displaying the json data
   */

  @Test
  public void testBroadbandRequestFailureNoState() throws IOException {
    /////////// LOAD DATASOURCE ///////////
    // Set up the request, make the request
    HttpURLConnection loadConnection = tryRequest("broadband?county=orange");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, loadConnection.getResponseCode());
    // Get the expected response: a success
    Map<String, Object> responseBody =
        adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals("Error", responseBody.get("responseType"));

    assertEquals(
        "Missing required query parameter: input state and county.",
        responseBody.get("errorDescription"));

    loadConnection.disconnect();
  }

  /**
   * This test checks that if a broadband request was made without entering a county name the json states error
   * and instructs the user to input a county name.
   * @throws IOException thrown if there is a failure displaying the json data
   */
  @Test
  public void testBroadbandRequestFailureNoCounty() throws IOException {
    /////////// LOAD DATASOURCE ///////////
    // Set up the request, make the request
    HttpURLConnection loadConnection = tryRequest("broadband?state=california");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, loadConnection.getResponseCode());
    // Get the expected response: a success
    Map<String, Object> responseBody =
        adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals("Error", responseBody.get("responseType"));

    assertEquals(
        "Missing required query parameter: input state and county.",
        responseBody.get("errorDescription"));

    loadConnection.disconnect();
  }

  /**
   * This test checks that if a broadband request was made without entering a county or state name the json
   * states error and instructs the user to input a county name and state name
   * @throws IOException thrown if there is a failure displaying the json data
   */

  @Test
  public void testBroadbandRequestFailureNoCountyNoState() throws IOException {
    /////////// LOAD DATASOURCE ///////////
    // Set up the request, make the request
    HttpURLConnection loadConnection = tryRequest("broadband");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, loadConnection.getResponseCode());
    // Get the expected response: a success
    Map<String, Object> responseBody =
        adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals("Error", responseBody.get("responseType"));

    assertEquals(
        "Missing required query parameter: input state and county.",
        responseBody.get("errorDescription"));

    loadConnection.disconnect();
  }

  /**
   * This tests that if a user enters a broadband request without a state but a county, like &state=&county=providence
   * That the user ensures they are putting in a valid state by displaying the associated json message
   * @throws IOException thrown if there is a failure displaying the json data
   */
  @Test
  public void testBroadbandRequestEmptyState() throws IOException {
    /////////// LOAD DATASOURCE ///////////
    // Set up the request, make the request
    HttpURLConnection loadConnection = tryRequest("broadband?state=&county=dekalb");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, loadConnection.getResponseCode());
    // Get the expected response: a success
    Map<String, Object> responseBody =
        adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals("Error", responseBody.get("responseType"));

    assertEquals(
        "County and/or state does not exist. Please"
            + " ensure that state is exists and that only the county name is inputted"
            + " (e.g., \"orange\" for \"Orange County, California\").",
        responseBody.get("errorDescription"));

    loadConnection.disconnect();
  }

  /**
   * This tests that if a user enters a broadband request without a county but a state, like &state=georgia&county=
   * That the user ensures they are putting in a valid county by displaying the associated json message
   * @throws IOException thrown if there is a failure displaying the json data
   */

  @Test
  public void testBroadbandRequestEmptyCounty() throws IOException {
    /////////// LOAD DATASOURCE ///////////
    // Set up the request, make the request
    HttpURLConnection loadConnection = tryRequest("broadband?state=california&county=");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, loadConnection.getResponseCode());
    // Get the expected response: a success
    Map<String, Object> responseBody =
        adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals("Error", responseBody.get("responseType"));

    assertEquals(
        "County and/or state does not exist. Please"
            + " ensure that state is exists and that only the county name is inputted"
            + " (e.g., \"orange\" for \"Orange County, California\").",
        responseBody.get("errorDescription"));

    loadConnection.disconnect();
  }

  /**
   * This tests that if a user enters a broadband request without a state and a county, like &state=&county=
   * That the user ensures they are putting in a valid county and state by displaying the associated json message
   * @throws IOException thrown if there is a failure displaying the json data
   */
  @Test
  public void testBroadbandRequestEmptyCountyEmptyState() throws IOException {
    /////////// LOAD DATASOURCE ///////////
    // Set up the request, make the request
    HttpURLConnection loadConnection = tryRequest("broadband?state=&county=");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, loadConnection.getResponseCode());
    // Get the expected response: a success
    Map<String, Object> responseBody =
        adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals("Error", responseBody.get("responseType"));

    assertEquals(
        "County and/or state does not exist. Please"
            + " ensure that state is exists and that only the county name is inputted"
            + " (e.g., \"orange\" for \"Orange County, California\").",
        responseBody.get("errorDescription"));

    loadConnection.disconnect();
  }
}
