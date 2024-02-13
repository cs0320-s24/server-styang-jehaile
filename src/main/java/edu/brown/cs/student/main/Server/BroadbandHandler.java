package edu.brown.cs.student.main.Server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
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

  public BroadbandHandler() throws URISyntaxException, IOException, InterruptedException {
    HttpRequest stateToCodeRequest =
        HttpRequest.newBuilder()
            .uri(new URI("https://api.census.gov/data/2010/dec/sf1?get=NAME&for=state:*"))
            .GET()
            .build();

    HttpResponse<String> stateToCode =
        HttpClient.newBuilder()
            .build()
            .send(stateToCodeRequest, HttpResponse.BodyHandlers.ofString());

    //  (move to BroadbandAPIUtilities?)
    Moshi moshi = new Moshi.Builder().build();
    Type type = com.squareup.moshi.Types.newParameterizedType(List.class, List.class);
    JsonAdapter<List<List<String>>> adapter = moshi.adapter(type);
    List<List<String>> stateList = adapter.fromJson(stateToCode.body());
    HashMap<String, String> stateToCodeMap = new HashMap<>();
    for (int i = 1; i < stateList.size(); i++) {
      stateToCodeMap.put(stateList.get(i).get(0).toLowerCase(), stateList.get(i).get(1));
    }
    this.stateToCode = stateToCodeMap;
  }

  @Override
  public Object handle(Request request, Response response)
      throws URISyntaxException, IOException, InterruptedException {
    String targetState = request.queryParams("state");
    String targetCounty = request.queryParams("county");
    Map<String, Object> responseMap = new HashMap<>();

    // get state code from map
    // Add failure response if state not in map
    if (!this.stateToCode.containsKey(targetState)) {
      return new BroadbandFailureResponse("State does not exist", LocalDateTime.now().toString()).serialize();
    }
    String stateCode = this.stateToCode.get(targetState.toLowerCase());

    // get county code from county
    HttpRequest stateToCodeRequest =
        HttpRequest.newBuilder()
            .uri(
                new URI(
                    "https://api.census.gov/data/2010/dec/sf1?get=NAME&for=county:*&in=state:"
                        + stateCode))
            .GET()
            .build();

    // Send that API request then store the response in this variable. Note the generic type.
    // HANDLE EXCEPTIONS
    HttpResponse<String> countyToCode =
        HttpClient.newBuilder()
            .build()
            .send(stateToCodeRequest, HttpResponse.BodyHandlers.ofString());

    String countyCode = "";
    // (move to BroadbandAPIUtilities?)
    Moshi moshi = new Moshi.Builder().build();
    Type type = com.squareup.moshi.Types.newParameterizedType(List.class, List.class);
    JsonAdapter<List<List<String>>> adapter = moshi.adapter(type);
    List<List<String>> countyList = adapter.fromJson(countyToCode.body());
    for (int i = 1; i < countyList.size(); i++) {
      if (countyList.get(i).get(0).equalsIgnoreCase(targetCounty)) {
        countyCode = countyList.get(i).get(3);
        break;
      }
    }
    if (countyCode.isEmpty()) {
      return new BroadbandFailureResponse("failed to find county", "current").serialize();
    }


    // send request
    String dataResponse = this.sendRequest(stateCode, countyCode);
    BroadbandData broadbandData = BroadbandAPIUtilities.deserializeBroadbandData(dataResponse);
    // must add time/date and state/county data to broadband data
    responseMap.put("Broadband data:", broadbandData);

    return new BroadbandSuccessResponse(responseMap).serialize();

  }

  private String sendRequest(String stateCode, String countyCode)
      throws URISyntaxException, IOException, InterruptedException { // figure out error handling

    HttpRequest buildDataRequest =
        HttpRequest.newBuilder()
            .uri(
                new URI(
                    "https://api.census.gov/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03"
                        + "_022E&for=county:"
                        + countyCode
                        + "&in=state:"
                        + stateCode))
            .GET()
            .build();

    // Send that API request then store the response in this variable. Note the generic type.
    HttpResponse<String> sentDataResponse =
        HttpClient.newBuilder()
            .build()
            .send(buildDataRequest, HttpResponse.BodyHandlers.ofString());

    return sentDataResponse.body();
  }

  public record BroadbandSuccessResponse(String responseType, String dateTime,Map<String, Object> responseMap){
    public BroadbandSuccessResponse(Map<String, Object> responseMap){
      this("Loaded successfully! :)", LocalDateTime.now().toString(), responseMap);
    }
    String serialize(){
      Moshi moshi = new Moshi.Builder().build();
      JsonAdapter<BroadbandHandler.BroadbandSuccessResponse> adapter = moshi.adapter((BroadbandHandler.BroadbandSuccessResponse.class));
      return adapter.toJson(this);
    }
  }
  public record BroadbandFailureResponse(String responseType, String errorDescription, String dateTime){
    public BroadbandFailureResponse(String errorDescription, String dateTime) {

      this("Error", errorDescription, "@" + LocalDateTime.now().toString());

    }
    String serialize(){
      Moshi moshi = new Moshi.Builder().build();
      JsonAdapter<BroadbandHandler.BroadbandFailureResponse> adapter = moshi.adapter(BroadbandHandler.BroadbandFailureResponse.class);
      return adapter.toJson(this);
    }

  }
}


//Acceptance Criteria: the response should include the date and time that all data was retrieved from the ACS API by
// your API server, as well as the state and county names your server received. Whoever calls your API can use this to
// (e.g.) help debug problems on their end.