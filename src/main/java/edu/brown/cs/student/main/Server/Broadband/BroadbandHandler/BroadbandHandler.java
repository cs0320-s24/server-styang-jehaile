package edu.brown.cs.student.main.Server.Broadband.BroadbandHandler;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.Server.Broadband.BroadbandData;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * This is the broadband handler class which implements route. This class handles and sends request
 * using the broadband interface to get the data which is queried in this class.
 */

public class BroadbandHandler implements Route {

  private BroadbandDataSourceInterface state;

  /**
   * Constructor for the broadband handler takes in the broadband data source interface.
   *
   * @param state
   */

  public BroadbandHandler(BroadbandDataSourceInterface state) {
    this.state = state;
  }

  /**
   * This method queries the request made by the caller by getting the desired state and county
   * name. This method uses the Broadband failure response to display messages of invalid requests.
   * This method creates the response map of the data requested by the user and user the interface's
   * getter to retreive the data and uses the Broadband succcess response to serialize the java data
   * into a JSON string viewable by the user.
   *
   * @param request
   * @param response
   * @return
   */
  @Override
  public Object handle(Request request, Response response) {
    // Retrieve queries
    String targetState = request.queryParams("state");
    String targetCounty = request.queryParams("county");

    // Ensure all parameters present
    if (targetState == null || targetCounty == null) {
      return new BroadbandFailureResponse("Missing required query parameter: "
          + "input state and county.").serialize();
    }

    HashMap<String, BroadbandData> responseMap = new HashMap<>();

    BroadbandData broadbandData;
    try {
      broadbandData = this.state.getBroadbandData(targetState, targetCounty);
    } catch (URISyntaxException | IOException | InterruptedException e) {
      return new BroadbandFailureResponse("Error retrieving data").serialize();
    } catch (IllegalStateException e) {
      return new BroadbandFailureResponse("Error accessing state code.").serialize();
    } catch (Exception e) {
      return new BroadbandFailureResponse("County and/or state does not exist. Please "
          + "ensure that state is exists and that only the county name is inputted "
          + "(e.g., \"orange\" for \"Orange County, California\").").serialize();
    }


    responseMap.put("Broadband data:", broadbandData);

    return new BroadbandSuccessResponse(broadbandData).serialize();
  }

  /**
   * Record representing a successful interaction with broadband response.
   *
   * @param responseType
   * @param responseData
   */

  public record BroadbandSuccessResponse(
      String responseType, BroadbandData responseData) {

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
      JsonAdapter<BroadbandHandler.BroadbandSuccessResponse> adapter =
          moshi.adapter((BroadbandHandler.BroadbandSuccessResponse.class));
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
      JsonAdapter<BroadbandHandler.BroadbandFailureResponse> adapter =
          moshi.adapter(BroadbandHandler.BroadbandFailureResponse.class);
      return adapter.toJson(this);
    }
  }
}
