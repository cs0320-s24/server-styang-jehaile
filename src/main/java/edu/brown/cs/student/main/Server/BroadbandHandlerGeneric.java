package edu.brown.cs.student.main.Server;

import java.io.IOException;
import java.net.URISyntaxException;
import spark.Request;
import spark.Response;

public interface BroadbandHandlerGeneric {
  Object handle(Request request, Response response)
      throws URISyntaxException, IOException, InterruptedException;
}
