package edu.brown.cs.student.main.Server.CSV;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Class for search handler to handle the json information requested from the user by implementing
 * route and , calling the csvdatasource which will return a list of string of the rows from the
 * file. This handler class will convert that into JSON strings which will show the user the list of
 * rows with the value searched for.
 */
public class SearchCSVHandler implements Route {

  private final CSVDataSource dataSource;

  /**
   * This is the SearchCSVHandler constructor which takes in the CSV data source class which just
   * parses, and searches the csv file.
   *
   * @param csvDataSource Takes in the CSV data source which interacts with the csv data files to
   *     retrieve the data.
   */
  public SearchCSVHandler(CSVDataSource csvDataSource) {
    this.dataSource = csvDataSource;
  }

  /**
   * This is method is overriden from the route class which is implemented searchcsv handler. The
   * handle method takes in a request and response and has a return type of object. Within this
   * method, since our search allows us to search based on a value and the user may also include a
   * header name or column index this method queries those requests as well. Then handle
   * instantiates a map, which is later used in the method after ensuring that the value is not null
   * and the rows are found using the data source class that the response map is passed into the
   * success response which converts the data into a json file viewable by the user upon requests to
   * the server. This method also catches exception if an element does not exist, the index is out
   * of bounds, or the argument is illegal. These errors initiate our failure response which prints
   * out a json string indicating what went wrong. This method also uses the boolean to confirm that
   * the file was loaded using its respective handler before the user can search it
   *
   * @param request Parameter of type Request which allows us to query the users inputs, which for
   *     this would be the searched value header name and or column index
   * @param response Represents the response to the user's query
   * @return returns the serialized data display the contents of the file to the user as a JSON
   *     string
   */
  @Override
  public String handle(Request request, Response response) {
    String toSearch = request.queryParams("toSearch");
    String headerName = request.queryParams("headerName");
    String columnIndexString = request.queryParams("columnIndex");
    Map<String, Object> responseMap = new HashMap<>();

    if (this.dataSource.isLoaded()) {
      if (toSearch == null) {
        return new SearchCSVFailureResponse("Please enter term to search for").serialize();
      }
      try {
        List<List<String>> matches =
            this.dataSource.searchCSV(toSearch, headerName, columnIndexString);
        responseMap.put("Matches:", matches);
        return new SearchCSVSuccessResponse(responseMap).serialize();
      } catch (NoSuchElementException e) {
        return new SearchCSVFailureResponse("Column does not exist.").serialize();
      } catch (IndexOutOfBoundsException e) {
        return new SearchCSVFailureResponse("Inputted index is out of bounds").serialize();
      } catch (IllegalArgumentException e) {
        return new SearchCSVFailureResponse("Column index inputted in incorrect format").serialize();
      }

    } else {
      return new SearchCSVFailureResponse("CSV File not loaded.")
          .serialize(); // specify failure (not loaded)
    }
  }

  /**
   * Record to state the conversion from java data to JSON was successful and display the returned
   * string. This record is called above in the case where the handle ensures a successful search.
   *
   * @param responseType String representing the successful response type
   * @param responseMap String representing the response map from the CSV data to turn into a JSON
   *     string
   */
  public record SearchCSVSuccessResponse(String responseType, Map<String, Object> responseMap) {
    /**
     * Constructor which takes in a response map and has the string success along with the map
     *
     * @param responseMap Represents the response map from the CSV data
     */
    public SearchCSVSuccessResponse(Map<String, Object> responseMap) {
      this("Success", responseMap);
    }

    /**
     * This method converts the data into JSON strings using moshi and the adapter, successfully
     * displaying the map to the user.
     *
     * @return String representing the JSON string displayed to the user
     */
    String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      JsonAdapter<SearchCSVHandler.SearchCSVSuccessResponse> adapter =
          moshi.adapter(SearchCSVHandler.SearchCSVSuccessResponse.class);
      return adapter.toJson(this);
    }
  }

  /**
   * Record used when there was an error with searching through the data and there is no resulting
   * map from search to display. This record is called above in the handle method.
   *
   * @param responseType String representing a failure
   * @param errorDescription
   */
  public record SearchCSVFailureResponse(String responseType, String errorDescription) {
    /**
     * Constructor for the search failure which takes in a string describing the error.
     *
     * @param errorDescription String describing the error type how the failure occurred ie, column
     *     index out of bounds
     */
    public SearchCSVFailureResponse(String errorDescription) {
      this("Error", errorDescription);
    }

    /**
     * This method converts the java data into a json which will state the error message upon
     * searching failure as a json string viewable by the user.
     *
     * @return String representing the JSON string displayed to the user
     */
    String serialize() { // error? check gearup code
      Moshi moshi = new Moshi.Builder().build();
      JsonAdapter<SearchCSVHandler.SearchCSVFailureResponse> adapter =
          moshi.adapter(SearchCSVHandler.SearchCSVFailureResponse.class);
      return adapter.toJson(this);
    }
  }
}
