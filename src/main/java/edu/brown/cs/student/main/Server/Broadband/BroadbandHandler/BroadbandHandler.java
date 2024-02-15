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

public class BroadbandHandler implements Route {

  private BroadbandDataSourceInterface state;

  public BroadbandHandler(BroadbandDataSourceInterface state) {
    this.state = state;
  }

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
      return new BroadbandFailureResponse("Error retrieving data");
    } catch (NoSuchElementException e) {
      return new BroadbandFailureResponse("County and/or state does not exist. Please "
          + "ensure that state is exists and that only the county name is inputted "
          + "(e.g., \"orange\" for \"Orange County, California\").").serialize();
    } catch (IllegalStateException e) {
      return new BroadbandFailureResponse("Error accessing state code.");
    }

    responseMap.put("Broadband data:", broadbandData);

    return new BroadbandSuccessResponse(broadbandData).serialize();
}

public record BroadbandSuccessResponse(
    String responseType, String dateTime, BroadbandData responseData) {

  public BroadbandSuccessResponse(BroadbandData responseData) {
    this("Loaded successfully! :)", LocalDateTime.now().toString(), responseData);
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
