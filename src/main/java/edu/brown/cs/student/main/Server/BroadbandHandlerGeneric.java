package edu.brown.cs.student.main.Server;

import java.util.Collection;
import spark.Request;
import spark.Response;

public interface BroadbandHandlerGeneric {
  Object handle(Request request, Response response);
}
