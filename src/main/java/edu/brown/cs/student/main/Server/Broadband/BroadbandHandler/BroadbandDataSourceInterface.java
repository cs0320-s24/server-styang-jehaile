package edu.brown.cs.student.main.Server.Broadband.BroadbandHandler;

import edu.brown.cs.student.main.Server.Broadband.BroadbandData;
import java.io.IOException;
import java.net.URISyntaxException;
import spark.Request;
import spark.Response;

/**
 * This is our interface of the broadband data source, the purpose of this interface was to allow
 * another developer to create mock data classes like the mockbroadband source to expand the functionality of
 * this program. This interface is implemented by the broadband data source, mock broadband data source and the
 * caching broadband data source.
 */

public interface BroadbandDataSourceInterface {
  /**
   * This method is a getter to return the broadband data of a state and county. This meothod may throw
   * a URISyntaxException, IOException and InterruptedException which should be handled accordingly when it is called.
   * @param state
   * @param county
   * @return
   * @throws URISyntaxException
   * @throws IOException
   * @throws InterruptedException
   */
  BroadbandData getBroadbandData(String state, String county)
      throws URISyntaxException, IOException, InterruptedException;
}
