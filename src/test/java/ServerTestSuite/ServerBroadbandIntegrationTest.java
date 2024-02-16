package ServerTestSuite;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ServerTestSuite.Mocks.MockBroadbandSource;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.Server.Broadband.BroadbandData;
import edu.brown.cs.student.main.Server.Broadband.BroadbandHandler.BroadbandDataSource;
import edu.brown.cs.student.main.Server.Broadband.BroadbandHandler.BroadbandDataSourceInterface;
import edu.brown.cs.student.main.Server.Broadband.BroadbandHandler.BroadbandHandler;
import edu.brown.cs.student.main.Server.Broadband.BroadbandHandler.CachingBroadbandDataSource;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Map;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testng.Assert;
import spark.Spark;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;


public class ServerBroadbandIntegrationTest {
  private final Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
  private JsonAdapter<Map<String, Object>> adapter;
  private JsonAdapter<BroadbandData> broadbandDataAdapter;

  @BeforeEach
  public void setup() {
    // Re-initialize parser, state, etc. for every test method

    // Use *MOCKED* data when in this test environment.
    BroadbandDataSourceInterface mockedSource = new MockBroadbandSource(new BroadbandData(LocalDateTime.of(2024, 2,15, 12, 13, 10).toString(), "california", "orange", 90.3));
    Spark.get("/broadband", new BroadbandHandler(new CachingBroadbandDataSource(mockedSource)));
    Spark.awaitInitialization(); // don't continue until the server is listening

    Moshi moshi = new Moshi.Builder().build();
    adapter = moshi.adapter(mapStringObject);
    this.broadbandDataAdapter = moshi.adapter(BroadbandData.class);
  }

  @AfterEach
  public void tearDown() {
    // Gracefully stop Spark listening on both endpoints
    Spark.unmap("/broadband");
    Spark.awaitStop(); // don't proceed until the server is stopped
  }

  private HttpURLConnection tryRequest(String apiCall) throws IOException {
    // Configure the connection (but don't actually send a request yet)
    URL requestURL = new URL("http://localhost:"+Spark.port()+"/"+apiCall);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();
    // The request body contains a Json object
    clientConnection.setRequestProperty("Content-Type", "application/json");
    // We're expecting a Json object in the response body
    clientConnection.setRequestProperty("Accept", "application/json");

    clientConnection.connect();
    return clientConnection;
  }

  @Test
  public void testBroadbandRequestSuccess() throws IOException {
    /////////// LOAD DATASOURCE ///////////
    // Set up the request, make the request
    HttpURLConnection loadConnection = tryRequest("broadband?state=california&county=orange");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, loadConnection.getResponseCode());
    // Get the expected response: a success
    Map<String, Object> responseBody = adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals("Loaded successfully! :)", responseBody.get("responseType"));

    assertEquals(
        new BroadbandData(LocalDateTime.of(2024, 2,15, 12, 13, 10).toString(), "california", "orange", 90.3).toString(),
        responseBody.get("responseData").toString());
    // Notice we had to do something strange above, because the map is
    // from String to *Object*. Awkward testing caused by poor API design...

    loadConnection.disconnect();
  }

  @Test
  public void testBroadbandRequestFailureNoState() throws IOException {
    /////////// LOAD DATASOURCE ///////////
    // Set up the request, make the request
    HttpURLConnection loadConnection = tryRequest("broadband?county=orange");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, loadConnection.getResponseCode());
    // Get the expected response: a success
    Map<String, Object> responseBody = adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals("Error", responseBody.get("responseType"));

    assertEquals(
        "Missing required query parameter: input state and county.",
        responseBody.get("errorDescription"));

    loadConnection.disconnect();
  }

  @Test
  public void testBroadbandRequestFailureNoCounty() throws IOException {
    /////////// LOAD DATASOURCE ///////////
    // Set up the request, make the request
    HttpURLConnection loadConnection = tryRequest("broadband?state=california");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, loadConnection.getResponseCode());
    // Get the expected response: a success
    Map<String, Object> responseBody = adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals("Error", responseBody.get("responseType"));

    assertEquals(
        "Missing required query parameter: input state and county.",
        responseBody.get("errorDescription"));

    loadConnection.disconnect();
  }

  @Test
  public void testBroadbandRequestFailureNoCountyNoState() throws IOException {
    /////////// LOAD DATASOURCE ///////////
    // Set up the request, make the request
    HttpURLConnection loadConnection = tryRequest("broadband");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, loadConnection.getResponseCode());
    // Get the expected response: a success
    Map<String, Object> responseBody = adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals("Error", responseBody.get("responseType"));

    assertEquals(
        "Missing required query parameter: input state and county.",
        responseBody.get("errorDescription"));

    loadConnection.disconnect();
  }

//  @Test
//  public void testBroadbandRequestDateTime() throws IOException {
//    /////////// LOAD DATASOURCE ///////////
//    // Set up the request, make the request
//    HttpURLConnection loadConnection = tryRequest("broadband?state=california&county=orange");
//    // Get an OK response (the *connection* worked, the *API* provides an error response)
//    assertEquals(200, loadConnection.getResponseCode());
//    // Get the expected response: a success
//    Map<String, Object> responseBody = adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
//    assertEquals("Loaded successfully! :)", responseBody.get("responseType"));
//
//    // Round time to several milliseconds
//    assertEquals(
//        LocalDateTime.now().toString().substring(0, 18),
//        responseBody.get("dateTime").toString().substring(0, 18));
//
//    loadConnection.disconnect();
//  }

//  @Test
//  public void testCachingFunctionality() throws IOException {
//    /////////// LOAD DATASOURCE ///////////
//    // Set up the request, make the request
//    HttpURLConnection loadConnection = tryRequest("broadband?state=california&county=orange");
//    // Get an OK response (the *connection* worked, the *API* provides an error response)
//    assertEquals(200, loadConnection.getResponseCode());
//    // Get the expected response: a success
//    Map<String, Object> responseBody = adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
//    assertEquals("Loaded successfully! :)", responseBody.get("responseType"));
//    // Round time to several milliseconds
//    String firstCallTime = responseBody.get("")
//
//    assertEquals(
//        new BroadbandData("california", "orange", 90.3).toString(),
//        responseBody.get("responseData").toString());
//
//    // Set up second request, make the request
//    HttpURLConnection loadConnectionAgain = tryRequest("broadband?state=california&county=orange");
//    // Get an OK response (the *connection* worked, the *API* provides an error response)
//    assertEquals(200, loadConnectionAgain.getResponseCode());
//    // Get the expected response: a success
//    Map<String, Object> responseBodyAgain = adapter.fromJson(new Buffer().readFrom(loadConnectionAgain.getInputStream()));
//    assertEquals("Loaded successfully! :)", responseBodyAgain.get("responseType"));
//
//    // Check that it was received previously
//    assertEquals(
//        firstCallTime.toString().substring(0, 18),
//        responseBodyAgain.get("dateTime").toString().substring(0, 18));
//
//    Assert.assertNotEquals(
//        LocalDateTime.now().toString().substring(0, 18),
//        responseBodyAgain.get("dateTime").toString().substring(0, 18)
//    );
//
//    assertEquals(
//        new BroadbandData("california", "orange", 90.3).toString(),
//        responseBodyAgain.get("responseData").toString());
//
//    loadConnection.disconnect();
//  }
//
}