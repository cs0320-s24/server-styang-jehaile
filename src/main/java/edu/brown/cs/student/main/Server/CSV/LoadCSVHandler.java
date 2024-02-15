package edu.brown.cs.student.main.Server.CSV;

import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.Exceptions.FactoryFailureException;
import edu.brown.cs.student.main.Exceptions.MalformedRowsException;
import java.io.IOException;
import spark.Request;
import spark.Response;
import spark.Route;

public class LoadCSVHandler implements Route {

  private CSVDataSource dataSource;

  public LoadCSVHandler(CSVDataSource dataSource) {
    this.dataSource = dataSource;
  }

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

  public record LoadCSVSuccessResponse(String responseType) {

    public LoadCSVSuccessResponse() {
      this("Loaded successfully! :)");
    }

    String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      return moshi.adapter(LoadCSVSuccessResponse.class).toJson(this);
    }
  }

  public record LoadCSVFailureResponse(String responseType, String errorDescription) {

    public LoadCSVFailureResponse(String errorDescription) {
      this("Error", errorDescription);
    }

    String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      return moshi.adapter(LoadCSVFailureResponse.class).toJson(this);
    }
  }
}
