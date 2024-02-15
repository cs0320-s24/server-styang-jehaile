package edu.brown.cs.student.main.Server.CSV;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.CSVParser.CSVParser;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class ViewCSVHandler implements Route {
  private CSVDataSource dataSource;

  public ViewCSVHandler(CSVDataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public Object handle(Request request, Response response) {
    Map<String, Object> responseMap = new HashMap<>();

    if (this.dataSource.isLoaded()) {
      responseMap.put("Data:", this.dataSource.viewCSV());
      return new ViewCSVSuccessResponse(responseMap);
    } else {
      return new ViewCSVFailureResponse().serialize();
    }
  }

  public record ViewCSVSuccessResponse(String responseType, Map<String, Object> responseMap) {
    public ViewCSVSuccessResponse(Map<String, Object> responseMap) {
      this(
          "Success", responseMap);
    }

    String serialize() { // error? check gearup code
      Moshi moshi = new Moshi.Builder().build();
      JsonAdapter<ViewCSVSuccessResponse> adapter = moshi.adapter(ViewCSVSuccessResponse.class);
      return adapter.toJson(this);
    }
  }

  public record ViewCSVFailureResponse(String responseType) {
    public ViewCSVFailureResponse() {
      this("Failed to view. Please load CSV file.");
    }

    String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      return moshi.adapter(ViewCSVHandler.ViewCSVFailureResponse.class).toJson(this);
    }
  }
}
