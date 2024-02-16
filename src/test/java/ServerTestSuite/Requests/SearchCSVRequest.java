package ServerTestSuite.Requests;

import spark.Request;

public class SearchCSVRequest extends Request {
  private String toSearch;
  private String headerName;
  private String columnIndex;

  public SearchCSVRequest(String toSearch, String headerName, String columnIndex) {
    this.toSearch = toSearch;
    this.headerName = headerName;
    this.columnIndex = columnIndex;
  }

  @Override
  public String queryParams(String param) {
    if (param.equalsIgnoreCase("toSearch")) {
      return this.toSearch;
    } else if (param.equalsIgnoreCase("headerName")) {
      return this.headerName;
    } else if (param.equalsIgnoreCase("columnIndex")) {
      return this.columnIndex;
    }
    return null;
  }
}
