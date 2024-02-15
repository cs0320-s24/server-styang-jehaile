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

  private final CSVDataSource dataSource;

  public SearchCSVHandler(CSVDataSource csvDataSource) {
    this.dataSource = csvDataSource;
  }

  @Override
  public Object handle(Request request, Response response) {
    String toSearch = request.queryParams("toSearch");
    String headerName = request.queryParams("headerName");
    String columnIndexString = request.queryParams("columnIndex");
    Map<String, Object> responseMap = new HashMap<>();


    if (this.dataSource.isLoaded()) {
      if (toSearch == null) {
        return new SearchCSVFailureResponse("Please enter term to search for");
      }
      try {
        List<List<String>> matches = this.dataSource.searchCSV(toSearch, headerName, columnIndexString);
        responseMap.put("Matches:", matches);
        return new SearchCSVSuccessResponse(responseMap);
      } catch (NoSuchElementException e) {
        return new SearchCSVFailureResponse("Column does not exist.");
      } catch (IndexOutOfBoundsException e) {
        return new SearchCSVFailureResponse("Inputted index is out of bounds");
      } catch (IllegalArgumentException e) {
        return new SearchCSVFailureResponse("Column index inputted in incorrect format");
      }

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
