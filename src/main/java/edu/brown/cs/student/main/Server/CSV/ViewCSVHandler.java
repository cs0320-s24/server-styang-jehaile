package edu.brown.cs.student.main.Server.CSV;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * This is the viewcsv handler class which implememts route and hosts the functionality for queries the user's request
 * converting the data extracted from the csv data source class into JSON object which is viewable by the user.
 */

public class ViewCSVHandler implements Route {
  private final CSVDataSource dataSource;

  /**
   * Constructor for the viewcsv handler which takes in the data source class so that the return rows/ data from the csv file
   * could be used in this handler class such that the user can view the contents
   * @param dataSource Takes in the CSV data source which interacts with the csv data files to retrieve the data.
   */
  public ViewCSVHandler(CSVDataSource dataSource) {
    this.dataSource = dataSource;
  }

  /**
   * This method override's the route class handle method. The parameters include the user request and response.
   * This method also uses the boolean to confirm that the file was loaded using its respective handler before the user can
   * view it. This method returns either a successful or failed JSON response, by instantiating their respective records
   * and displaying a json string of the result. The response map when successful should contain the contents of the data
   * in a json string.
   * @param request Parameter of type Request which allows us to query the users inputs
   * @param response Represents the response to the user's query
   * @return returns the serialized data display the contents of the file to the user as a JSON string
   */
  @Override
  public Object handle(Request request, Response response) {
    Map<String, Object> responseMap = new HashMap<>();

    if (this.dataSource.isLoaded()) {
      responseMap.put("Data:", this.dataSource.viewCSV());
      return new ViewCSVSuccessResponse(responseMap).serialize();
    } else {
      return new ViewCSVFailureResponse().serialize();
    }
  }

  /**
   * Record class for when viewing was successful.
   * @param responseType  string representing a successful response
   * @param responseMap map representing the data if the file to be converted to json
   */
  public record ViewCSVSuccessResponse(String responseType, Map<String, Object> responseMap) {
    /**
     * Constructor for a successful viewing which takes in the response map.
     * @param responseMap a java map of the data viewed by the user to convert to json string
     */
    public ViewCSVSuccessResponse(Map<String, Object> responseMap) {
      this(
          "Success", responseMap);
    }

    /**
     * Serialize method which convert data into a JSON string using moshi to build then adapt the data to the
     * right format. This method enables the user to view the contents.
     * @return String representing the JSON string displayed to the user
     */

    String serialize() { // error? check gearup code
      Moshi moshi = new Moshi.Builder().build();
      JsonAdapter<ViewCSVSuccessResponse> adapter = moshi.adapter(ViewCSVSuccessResponse.class);
      return adapter.toJson(this);
    }
  }

  /**
   * Record class for when viewing a file was unsuccessful.
   * @param responseType string representing a failed attemtp to view
   */

  public record ViewCSVFailureResponse(String responseType) {
    /**
     * Constructor for viewcsv failure, includes a string which states viewing failed and suggests, loading
     * the file.
     */
    public ViewCSVFailureResponse() {

      this("Failed to view. Please load CSV file.");
    }

    /**
     * Serialize converts to viewable JSON string that the user can read by using moshi.
     * @return String representing the JSON string displayed to the user
     */

    String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      return moshi.adapter(ViewCSVHandler.ViewCSVFailureResponse.class).toJson(this);
    }
  }
}
