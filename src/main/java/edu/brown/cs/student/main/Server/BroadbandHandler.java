package edu.brown.cs.student.main.Server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class BroadbandHandler implements Route {

  public BroadbandHandler() throws URISyntaxException, IOException, InterruptedException {
    HttpRequest stateToCodeRequest =
        HttpRequest.newBuilder()
            .uri(new URI("https://api.census.gov/data/2010/dec/sf1?get=NAME&for=state:*"))
            .GET()
            .build();

    // Send that API request then store the response in this variable. Note the generic type.
    HttpResponse<String> stateToCode =
        HttpClient.newBuilder()
            .build()
            .send(stateToCodeRequest, HttpResponse.BodyHandlers.ofString());

    Moshi moshi = new Moshi.Builder().build();

    JsonAdapter<StateList> adapter = moshi.adapter(StateList.class);
  }

  @Override
  public Object handle(Request request, Response response) {
    String targetState = request.queryParams("state");
    String targetCounty = request.queryParams("county");


  }
  public static Map<String, Integer> parseStateToCodeMap(String[][] jsonResponse){
    Map<String, Integer> response = new HashMap<>();
    for(String[] entry : data)
//    jsonResponse = jsonResponse.substring(1, jsonResponse.length()-1);
//    String[] keyValuePairs = jsonResponse.split(",");
//    for(String pair: keyValuePairs){
//      String[] entry = pair.split(":");
//      String key = entry[0].trim().replaceAll("\"", "");
//      Integer value = Integer.parseInt(entry[1].trim());
//      response.put(key, value);
//    }
    return response;
  }
}
