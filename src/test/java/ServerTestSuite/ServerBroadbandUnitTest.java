package ServerTestSuite;

import ServerTestSuite.Mocks.MockBroadbandSource;
import ServerTestSuite.Requests.BroadbandRequest;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.Server.Broadband.BroadbandData;
import edu.brown.cs.student.main.Server.Broadband.BroadbandHandler.BroadbandDataSource;
import edu.brown.cs.student.main.Server.Broadband.BroadbandHandler.BroadbandDataSourceInterface;
import edu.brown.cs.student.main.Server.Broadband.BroadbandHandler.BroadbandHandler;
import edu.brown.cs.student.main.Server.Broadband.BroadbandHandler.CachingBroadbandDataSource;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testng.Assert;

public class ServerBroadbandUnitTest {

  private CachingBroadbandDataSource cachingSource;
  private BroadbandDataSourceInterface mockedSource;
  private BroadbandDataSource webSource;
  private BroadbandHandler broadbandHandler;

  @BeforeEach
  public void setup() {
    mockedSource = new MockBroadbandSource("california", "orange", 90.3);
    cachingSource = new CachingBroadbandDataSource(mockedSource);
    webSource = new BroadbandDataSource();
    broadbandHandler = new BroadbandHandler(mockedSource);
  }

  @Test
  public void testCachingFunctionalityTimeExpiration() throws IOException, InterruptedException {
    BroadbandData initialResponse = cachingSource.getBroadbandData("california", "orange");

    Thread.sleep(2500);

    // Make the same request again and ensure that the response is different from the initial
    // response
    BroadbandData cachedResponse = cachingSource.getBroadbandData("california", "orange");

    // Assert that the cached response is not equal to the initial response
    Assert.assertNotEquals(initialResponse.toString(), cachedResponse.toString());
  }

  @Test
  public void testCachingFunctionalityTimeNonExpiration() throws InterruptedException {

    BroadbandData initialResponse = cachingSource.getBroadbandData("california", "orange");

    Thread.sleep(1000);

    // Make the same request again and ensure that the response is the same as the initial response
    BroadbandData cachedResponse = cachingSource.getBroadbandData("california", "orange");

    // Assert that the cached response is not equal to the initial response
    Assert.assertEquals(initialResponse.toString(), cachedResponse.toString());
  }

  @Test
  public void testCachingFunctionalityCacheLimit() throws InterruptedException {

    BroadbandData initialResponse = cachingSource.getBroadbandData("california", "orange");
    BroadbandData secondResponse = cachingSource.getBroadbandData("wisconsin", "dane");

    Thread.sleep(1000);

    // Make the same request again and ensure that the response is different from the initial
    // response
    BroadbandData cachedResponse = cachingSource.getBroadbandData("california", "orange");

    // Assert that the cached response is not equal to the initial response
    Assert.assertNotEquals(initialResponse.toString(), cachedResponse.toString());
  }

  @Test
  public void testGetResponseWebSource()
      throws URISyntaxException, IOException, InterruptedException {
    BroadbandData response = webSource.getBroadbandData("california", "orange");

    BroadbandData expectedResponse =
        new BroadbandData(
            LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString(),
            "California",
            "Orange County",
            93.0);

    Assert.assertEquals(response.toString(), expectedResponse.toString());
  }

  @Test
  public void testGetResponseNoSuchElement()
      throws URISyntaxException, IOException, InterruptedException {
    Assert.assertThrows(
        NoSuchElementException.class,
        () -> {
          webSource.getBroadbandData("switzerland", "orange");
        });

    Assert.assertThrows(
        NoSuchElementException.class,
        () -> {
          webSource.getBroadbandData("", "orange");
        });

    Assert.assertThrows(
        NoSuchElementException.class,
        () -> {
          webSource.getBroadbandData("california", "switzerland");
        });

    Assert.assertThrows(
        NoSuchElementException.class,
        () -> {
          webSource.getBroadbandData("california", "");
        });
  }

  @Test
  public void testBroadbandHandlerSuccess() {
    Object successResponse =
        broadbandHandler.handle(new BroadbandRequest("california", "orange"), null);
    String expectedSuccessResponse =
        new BroadbandSuccessResponse(
                new BroadbandData(
                    LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString(),
                    "california",
                    "orange",
                    90.3))
            .serialize();
    Assert.assertEquals(successResponse.toString(), expectedSuccessResponse);
  }

  @Test
  public void testBroadbandHandlerFailureNoCounty() {
    Object failureResponse = broadbandHandler.handle(new BroadbandRequest("california", ""), null);
    Assert.assertTrue(failureResponse.toString().contains("County and/or state does not exist."));
  }

  @Test
  public void testBroadbandHandlerFailureNoState() {
    Object failureResponse = broadbandHandler.handle(new BroadbandRequest("", "orange"), null);
    Assert.assertTrue(failureResponse.toString().contains("County and/or state does not exist."));
  }

  @Test
  public void testBroadbandHandlerFailureNoStateNoCounty() {
    Object failureResponse = broadbandHandler.handle(new BroadbandRequest("", ""), null);
    Assert.assertTrue(failureResponse.toString().contains("County and/or state does not exist."));
  }

  @Test
  public void testBroadbandHandlerFailureNullState() {
    Object failureResponse = broadbandHandler.handle(new BroadbandRequest(null, ""), null);
    Assert.assertTrue(failureResponse.toString().contains("Missing required query parameter"));
  }

  @Test
  public void testBroadbandHandlerFailureNullCounty() {
    Object failureResponse = broadbandHandler.handle(new BroadbandRequest("", null), null);
    Assert.assertTrue(failureResponse.toString().contains("Missing required query parameter"));
  }

  @Test
  public void testBroadbandHandlerFailureNullCountyNullState() {
    Object failureResponse = broadbandHandler.handle(new BroadbandRequest(null, null), null);
    Assert.assertTrue(failureResponse.toString().contains("Missing required query parameter"));
  }

  /**
   * Record representing a successful interaction with broadband response.
   *
   * @param responseType
   * @param responseData
   */
  public record BroadbandSuccessResponse(String responseType, BroadbandData responseData) {

    /**
     * Constructor which takes in the broadband data class.
     *
     * @param responseData
     */
    public BroadbandSuccessResponse(BroadbandData responseData) {
      this("Loaded successfully! :)", responseData);
    }

    /**
     * Serialize method uses moshi to turn the java data into a JSON string accessible by the viewer
     * when the request and response were successful this is called in the handle method.
     *
     * @return
     */
    String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      JsonAdapter<ServerBroadbandUnitTest.BroadbandSuccessResponse> adapter =
          moshi.adapter((ServerBroadbandUnitTest.BroadbandSuccessResponse.class));
      return adapter.toJson(this);
    }
  }

  /**
   * Record representing a failure to response to the broadband request.
   *
   * @param responseType
   * @param dateTime
   * @param errorDescription
   */
  public record BroadbandFailureResponse(
      String responseType, String dateTime, String errorDescription) {

    /**
     * Constructor for the broadband failure response, which takes an a string to describe the
     * error.
     *
     * @param errorDescription
     */
    public BroadbandFailureResponse(String errorDescription) {
      this("Error", LocalDateTime.now().toString(), errorDescription);
    }

    /**
     * The serialization for a failed broadband response, which converts the java data into a JSON
     * string describing what went wrong when handling the request. This method is called in the
     * handle method upon invalid requests and errors.
     *
     * @return
     */
    String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      JsonAdapter<ServerBroadbandUnitTest.BroadbandFailureResponse> adapter =
          moshi.adapter(ServerBroadbandUnitTest.BroadbandFailureResponse.class);
      return adapter.toJson(this);
    }
  }
}
