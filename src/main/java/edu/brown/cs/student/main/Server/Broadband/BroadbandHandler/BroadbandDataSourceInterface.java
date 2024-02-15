package edu.brown.cs.student.main.Server.Broadband.BroadbandHandler;

import edu.brown.cs.student.main.Server.Broadband.BroadbandData;
import java.io.IOException;
import java.net.URISyntaxException;
import spark.Request;
import spark.Response;

public interface BroadbandDataSourceInterface {
  BroadbandData getBroadbandData(String state, String county)
      throws URISyntaxException, IOException, InterruptedException;
}
