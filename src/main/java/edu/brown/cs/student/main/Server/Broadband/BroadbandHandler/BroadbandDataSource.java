package edu.brown.cs.student.main.Server.Broadband.BroadbandHandler;

import edu.brown.cs.student.main.Server.Broadband.BroadbandAPIUtilities;
import edu.brown.cs.student.main.Server.Broadband.BroadbandData;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Class that builds and requests queries from the census API, passing them to APIUtilities class to deserialize, and
 * returning the response as a BroadBand.
 */

public class BroadbandDataSource implements BroadbandDataSourceInterface{

  HashMap<String, String> stateToCode;

  /**
   * This is the constructor for the broadbanddatasource class. Inside the constructor there is a try catch
   * which tries to call on the api to get the response and deserialize the state map. If this is unsuccessful and any of the
   * errors (IOException, URISyntax Exception, Interrupted Exception are caught then the map is set to null.
   */
  public BroadbandDataSource() {
    try {
      HttpResponse<String> stateToCode = BroadbandAPIUtilities.getAPIResponse(
          "https://api.census.gov/data/2010/dec/sf1?get=NAME&for=state:*");
      this.stateToCode = BroadbandAPIUtilities.deserializeStateMap(stateToCode);
    } catch (IOException | URISyntaxException | InterruptedException e) {
      // Set map to null so that code remains functional, throw error once endpoint is
      // actually queried
      this.stateToCode = null;
    }
  }

  /**
   * This method is a getter which takes in the state and county then has a return type of broadband data. First the method null
   * checks the state code map and throws an illegal state exception if null. Then it ensures that the state is in the map.
   * Next we make all the state names lower case to ensure that capitalization does not intefere with finding the data.
   * We then have a for loop to build the list and extract the county code for the requested county. Lastly, this
   * method sends a request based on the state code and county code requested by the caller and returns the deserialized data.
   * @param state representing the state as a string
   * @param county representing the county as a string
   * @return the broadband data for that state and county
   * @throws URISyntaxException  if there is an error in interacting with the URI
   * @throws IOException if I/O error occurs
   * @throws InterruptedException  if a thread is interrupted while waiting
   * @throws NoSuchElementException thrown if the data requested does not exist where it is searched, initiating a failure response
   */
  public BroadbandData getBroadbandData(String state, String county)
      throws URISyntaxException, IOException, InterruptedException, NoSuchElementException {
    if (this.stateToCode == null) {
      throw new IllegalStateException();
    }

    if (!this.stateToCode.containsKey(state)) {
      throw new NoSuchElementException();
    }

    String stateCode = this.stateToCode.get(state.toLowerCase());

    // Send API request and find county code
      HttpResponse<String> countyToCode = BroadbandAPIUtilities.getAPIResponse(
          "https://api.census.gov/data/2010/dec/sf1?get=NAME&for=county:*&in=state:"
              + stateCode);

      String countyCode = "";
      List<List<String>> countyList = BroadbandAPIUtilities.buildListAdapter(countyToCode.body());
      for (int i = 1; i < countyList.size(); i++) {
        if (countyList
            .get(i)
            .get(0)
            .toLowerCase()
            .contains(county.toLowerCase() + " county,")) {
          countyCode = countyList.get(i).get(2);
          break;
        }
      }

      // Inform if county is not found
      if (countyCode.isEmpty()) {
        throw new NoSuchElementException();
      }

      // Send request
      String dataResponse = this.sendRequest(stateCode, countyCode);
    return BroadbandAPIUtilities.deserializeBroadbandData(dataResponse);
  }

  /**
   * This method is used to send the request to the api to return a string of the results for the state, county broadband data
   * by using the HttpResponse class and the api utilities class which gets the api response based on the
   * state code and county code passed into this method.
   * @param stateCode  representing the state as a string
   * @param countyCode representing the county as a string
   * @return a string representing the response from the API request
   * @throws URISyntaxException if there is an error in interacting with the URI
   * @throws IOException if a thread is interrupted while waiting
   * @throws InterruptedException  if a thread is interrupted while waiting
   */
  private String sendRequest(String stateCode, String countyCode)
      throws URISyntaxException, IOException, InterruptedException {

    // Send that API request then store the response in this variable.
    HttpResponse<String> sentDataResponse = BroadbandAPIUtilities.getAPIResponse(
        "https://api.census.gov/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03"
            + "_022E&for=county:"
            + countyCode
            + "&in=state:"
            + stateCode);

    return sentDataResponse.body();
  }
}


