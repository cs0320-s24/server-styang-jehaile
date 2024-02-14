package edu.brown.cs.student.main.Server.CSV;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.CSVParser.CSVParser;
import edu.brown.cs.student.main.SearchUtility.Search;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import spark.Request;
import spark.Response;
import spark.Route;

public class SearchCSVHandler implements Route {

  private final LoadCSVHandler loadCSVHandler;

  public SearchCSVHandler(LoadCSVHandler loadCSVHandler) {
    this.loadCSVHandler = loadCSVHandler;
  }

  @Override
  public Object handle(Request request, Response response) {
    String toSearch = request.queryParams("toSearch");
    String headerName = request.queryParams("headerName");
    String columnIndexString = request.queryParams("columnIndex");
    Map<String, Object> responseMap = new HashMap<>();

    if (toSearch == null) {
      return new SearchCSVFailureResponse("Please enter term to search for");
    }

    if (this.loadCSVHandler.isLoaded()) {
      CSVParser<List<String>> csvParser =
          this.loadCSVHandler.getCSVParser();
      Search search = new Search(csvParser);

      if (columnIndexString != null) {
        int columnIndex;

        try {
          columnIndex = Integer.parseInt(columnIndexString);
        } catch (IllegalArgumentException e) {
          return new SearchCSVFailureResponse(
              "Index not entered as integer."); // specify failure (index not entered as number)
        }

        try {
          responseMap.put("Matching rows", search.searchCSV(toSearch, columnIndex));
        } catch (IndexOutOfBoundsException e) {
          return new SearchCSVFailureResponse(
              "Column does not exist."); // specify failure (index doesn't exist / less than 0.)
        }

        return new SearchCSVSuccessResponse(responseMap).serialize();
      }

      if (headerName != null) {
        try {
          responseMap.put("Matching rows", search.searchCSV(toSearch, headerName));
        } catch (NoSuchElementException e) {
          return new SearchCSVFailureResponse(
              "Header does not exist."); // specify failure (header doesn't exist)
        }

        return new SearchCSVSuccessResponse(responseMap).serialize();
      }

      responseMap.put("Matching rows", search.searchCSV(toSearch));

      return new SearchCSVSuccessResponse(responseMap).serialize();

    } else {
      return new SearchCSVFailureResponse("CSV File not loaded.")
          .serialize(); // specify failure (not loaded)
    }
  }

  public record SearchCSVSuccessResponse(String responseType, Map<String, Object> responseMap) {

    public SearchCSVSuccessResponse(Map<String, Object> responseMap) {
      this("Success", responseMap);
    }

    String serialize() { // error? check gearup code
      Moshi moshi = new Moshi.Builder().build();
      JsonAdapter<SearchCSVHandler.SearchCSVSuccessResponse> adapter =
          moshi.adapter(SearchCSVHandler.SearchCSVSuccessResponse.class);
      return adapter.toJson(this);
    }
  }

  public record SearchCSVFailureResponse(String responseType, String errorDescription) {

    public SearchCSVFailureResponse(String errorDescription) {
      this("Error", errorDescription);
    }

    String serialize() { // error? check gearup code
      Moshi moshi = new Moshi.Builder().build();
      JsonAdapter<SearchCSVHandler.SearchCSVFailureResponse> adapter =
          moshi.adapter(SearchCSVHandler.SearchCSVFailureResponse.class);
      return adapter.toJson(this);
    }
  }
}
