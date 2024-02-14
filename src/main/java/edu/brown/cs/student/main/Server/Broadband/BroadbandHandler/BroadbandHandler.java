package edu.brown.cs.student.main.Server.Broadband.BroadbandHandler;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.Server.Broadband.BroadbandAPIUtilities;
import edu.brown.cs.student.main.Server.Broadband.BroadbandData;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class BroadbandHandler implements Route, BroadbandHandlerGeneric {

  private HashMap<String, String> stateToCode;

  public BroadbandHandler() {

      try {
        HttpResponse<String> stateToCode = BroadbandAPIUtilities.getAPIResponse(
            "https://api.census.gov/data/2010/dec/sf1?get=NAME&for=state:*");
        this.stateToCode = BroadbandAPIUtilities.deserializeStateMap(stateToCode);
      } catch (IOException | URISyntaxException | InterruptedException e) {
        // Assign as null so that web user can be alerted after using endpoint, and the other
        // endpoints remain functional
        this.stateToCode = null;
      }
  }

  @Override
  public Object handle(Request request, Response response) {
    // Check that map was properly initialized
    if (this.stateToCode == null) {
      return new BroadbandFailureResponse("Error retrieving state data.");
    }

    // Retrieve queries
    String targetState = request.queryParams("state");
    String targetCounty = request.queryParams("county");

    // Ensure all parameters present
    if (targetState == null || targetCounty == null) {
      return new BroadbandFailureResponse("Missing required query parameter: "
          + "input state and county.");
    }

    // Ensure state exists
    if (!this.stateToCode.containsKey(targetState)) {
      return new BroadbandFailureResponse("State does not exist.")
          .serialize();
    }

    String stateCode = this.stateToCode.get(targetState.toLowerCase());

    // Initialize response map
    Map<String, Object> responseMap = new HashMap<>();

    // Send API request and find county code
    try {
      HttpResponse<String> countyToCode = BroadbandAPIUtilities.getAPIResponse(
          "https://api.census.gov/data/2010/dec/sf1?get=NAME&for=county:*&in=state:"
              + stateCode);

      String countyCode = "";
      List<List<String>> countyList = BroadbandAPIUtilities.buildListAdapter(countyToCode.body());
      for (int i = 1; i < countyList.size(); i++) {
        if (countyList
            .get(i)
            .get(0)
            .toLowerCase()
            .contains(targetCounty.toLowerCase() + " county,")) {
          countyCode = countyList.get(i).get(2);
          break;
        }
      }

      // Inform if county is not found
      if (countyCode.isEmpty()) {
        return new BroadbandFailureResponse("Failed to find county. Please enter only "
            + "county name (e.g., \"orange\" for \"Orange County, California\")").serialize();
      }

      // Send request
      String dataResponse = this.sendRequest(stateCode, countyCode);
      BroadbandData broadbandData = BroadbandAPIUtilities.deserializeBroadbandData(dataResponse);
      responseMap.put("Broadband data:", broadbandData);

      return new BroadbandSuccessResponse(responseMap).serialize();

    } catch (URISyntaxException | IOException | InterruptedException e) {
      return new BroadbandFailureResponse("Error retrieving data.");
    }
  }

  private String sendRequest(String stateCode, String countyCode)
      throws URISyntaxException, IOException, InterruptedException {

    // Send that API request then store the response in this variable. Note the generic type.
    HttpResponse<String> sentDataResponse = BroadbandAPIUtilities.getAPIResponse(
        "https://api.census.gov/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03"
            + "_022E&for=county:"
            + countyCode
            + "&in=state:"
            + stateCode);

    return sentDataResponse.body();
  }

  public record BroadbandSuccessResponse(
      String responseType, String dateTime, Map<String, Object> responseMap) {

    public BroadbandSuccessResponse(Map<String, Object> responseMap) {
      this("Loaded successfully! :)", LocalDateTime.now().toString(), responseMap);
    }

    String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      JsonAdapter<BroadbandHandler.BroadbandSuccessResponse> adapter =
          moshi.adapter((BroadbandHandler.BroadbandSuccessResponse.class));
      return adapter.toJson(this);
    }
  }

  public record BroadbandFailureResponse(
      String responseType, String dateTime, String errorDescription) {

    public BroadbandFailureResponse(String errorDescription) {
      this("Error", LocalDateTime.now().toString(), errorDescription);
    }

    String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      JsonAdapter<BroadbandHandler.BroadbandFailureResponse> adapter =
          moshi.adapter(BroadbandHandler.BroadbandFailureResponse.class);
      return adapter.toJson(this);
    }
  }
}
