package edu.brown.cs.student.main.Server.Broadband.BroadbandHandler;

import edu.brown.cs.student.main.Server.Broadband.BroadbandAPIUtilities;
import edu.brown.cs.student.main.Server.Broadband.BroadbandData;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class BroadbandDataSource implements BroadbandDataSourceInterface{

  HashMap<String, String> stateToCode;

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

  private String sendRequest(String stateCode, String countyCode)
      throws URISyntaxException, IOException, InterruptedException {

    // Send that API request then store the response in this variable. Note the generic type.
    HttpResponse<String> sentDataResponse = BroadbandAPIUtilities.getAPIResponse(
        "https://api.census.gov/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03"
            + "_022E&for=county:"
            + countyCode
            + "&in=state:"
            + stateCode);

    return sentDataResponse.body();
  }
}


