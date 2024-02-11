package edu.brown.cs.student.main.Server;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import spark.Request;
import spark.Response;
import spark.Route;

public class BroadbandHandler implements Route {

  public BroadbandHandler() {
    HttpRequest stateToCodeRequest =
        HttpRequest.newBuilder()
            .uri(new URI("https://api.census.gov/data/2010/dec/sf1?get=NAME&for=state:*"))
            .GET()
            .build();

    // Send that API request then store the response in this variable. Note the generic type.
    HttpResponse<String> sentBoredApiResponse =
        HttpClient.newBuilder()
            .build()
            .send(buildBoredApiRequest, HttpResponse.BodyHandlers.ofString());
  }
  @Override
  public Object handle(Request request, Response response) {
    String targetState = request.queryParams("state");
    String targetCounty = request.queryParams("county");



  }
