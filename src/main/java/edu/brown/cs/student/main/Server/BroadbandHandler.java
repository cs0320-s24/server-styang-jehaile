package edu.brown.cs.student.main.Server;

import spark.Request;
import spark.Response;
import spark.Route;

public class BroadbandHandler implements Route {

  @Override
  public Object handle(Request request, Response response) {
    String targetState = request.queryParams("state");
    String targetCounty = request.queryParams("county");
  }
