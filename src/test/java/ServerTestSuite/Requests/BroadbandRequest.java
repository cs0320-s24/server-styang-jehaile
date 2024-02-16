package ServerTestSuite.Requests;

import spark.Request;

public class BroadbandRequest extends Request {

  private final String state;
  private final String county;

  public BroadbandRequest(String state, String county) {
    this.state = state;
    this.county = county;
  }

  @Override
  public String queryParams(String param) {
    if (param.equalsIgnoreCase("state")) {
      return this.state;
    } else if (param.equalsIgnoreCase("county")) {
      return this.county;
    }
    return null;
  }
}
