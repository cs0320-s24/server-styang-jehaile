package ServerTestSuite.Requests;

import spark.Request;

public class LoadCSVRequest extends Request {
  private String fileName;
  private String headers;

  public LoadCSVRequest(String fileName, String headers) {
    this.fileName = fileName;
    this.headers = headers;
  }

  @Override
  public String queryParams(String param) {
    if (param.equalsIgnoreCase("fileName")) {
      return this.fileName;
    } else if (param.equalsIgnoreCase("headers")) {
      return this.headers;
    }
    return null;
  }
}
