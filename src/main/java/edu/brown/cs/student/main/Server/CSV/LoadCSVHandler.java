package edu.brown.cs.student.main.Server.CSV;

import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.Exceptions.FactoryFailureException;
import edu.brown.cs.student.main.Exceptions.MalformedRowsException;
import java.io.IOException;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * This method is the handler for the loadcsv endpoint. This class implements the Route java class and  overrides
 * the handle method. This also handles the LoadCSVFailure and LoadCSVSuccess records as well.
 */

public class LoadCSVHandler implements Route {

  private CSVDataSource dataSource;

  /**
   * Constructor for the LoadCSVHandler, takes in the CSVDataSource class.
   * @param dataSource
   */

  public LoadCSVHandler(CSVDataSource dataSource) {
    this.dataSource = dataSource;
  }

  /**
   * This method is implemented from the Route class, it takes in a request and a response. This method
   * uses the query of the file name, error checks it if null. This method handles the loading of a csv file.
   * This prints a  response that states whether headers are present, whether errors occured and if a file loaded sucessfully
   * This method calls on the CSVDataSource class to load the data using parse in a try catch which catches the exception
   * thrown in this method. Lastly handle calls the serialize method on the success response.
   * @param request
   * @param response
   * @return
   */
  @Override
  public Object handle(Request request, Response response) {

    String fileName = request.queryParams("fileName");

    if (fileName == null) {
      return new LoadCSVFailureResponse("Please enter fileName as parameter.");
    }

    boolean headers;
    try {
      headers = Boolean.parseBoolean(request.queryParams("headers")); // Could change to not wrapper
    } catch (IllegalArgumentException e) {
      return new LoadCSVFailureResponse("Please enter headers parameter as "
          + "\"true\" or \"false\"").serialize(); // Specify error reason (headers not 'True' or 'False')
    }

    try {
      this.dataSource.loadCSVData(fileName, headers);
    } catch (IOException | FactoryFailureException e) {
      return new LoadCSVFailureResponse("Error reading file").serialize();
    } catch (MalformedRowsException e) {
      return new LoadCSVFailureResponse("Malformed file. "
          + "Please ensure all rows contain the same number of columns.");
    }
    return new LoadCSVSuccessResponse().serialize();
  }

  /**
   * Record representing a successful response for loading a CSV file.
   * @param responseType
   */

  public record LoadCSVSuccessResponse(String responseType) {


    public LoadCSVSuccessResponse() {
      this("Loaded successfully! :)");
    }

    /**
     * This method serializes the loadcsvsuccessresponse by instantiaiting an instance of the moshi class
     * Then building the JSON. This method returns a string which is returned from the json adapter method for this record,
     * serializing the current load file into the JSON string from a java object, when data is successfully loaded.
     * @return
     */
    String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      return moshi.adapter(LoadCSVSuccessResponse.class).toJson(this);
    }
  }

  /**
   *Record representing a failure response for loading a csv file.
   * @param responseType
   * @param errorDescription
   */

  public record LoadCSVFailureResponse(String responseType, String errorDescription) {
    /**
     * Constructor of failure response for load
     * @param errorDescription
     */

    public LoadCSVFailureResponse(String errorDescription) {
      this("Error", errorDescription);
    }

    /**
     * This is the serialize method for failure to load, it turns java data into a JSON String using moshi.
     * @return
     */
    String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      return moshi.adapter(LoadCSVFailureResponse.class).toJson(this);
    }
  }
}
