package edu.brown.cs.student.main.Server.Broadband.BroadbandHandler;

import edu.brown.cs.student.main.Server.Broadband.BroadbandData;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * This is our interface of the broadband data source, the purpose of this interface was to allow
 * another developer to create mock data classes like the mockbroadband source to expand the
 * functionality of this program. This interface is implemented by the broadband data source, mock
 * broadband data source and the caching broadband data source.
 */
public interface BroadbandDataSourceInterface {

  /**
   * This method is a getter to return the broadband data of a state and county. This meothod may
   * throw a URISyntaxException, IOException and InterruptedException which should be handled
   * accordingly when it is called.
   *
   * @param state  String of the state
   * @param county String of county
   * @return data of the broadband data type
   * @throws URISyntaxException   thrown if there is an error in interacting with the URI.
   * @throws IOException          if there are issues getting the data
   * @throws InterruptedException thrown if there is an interrupted while it is waiting
   */
  BroadbandData getBroadbandData(String state, String county)
      throws URISyntaxException, IOException, InterruptedException;
}
