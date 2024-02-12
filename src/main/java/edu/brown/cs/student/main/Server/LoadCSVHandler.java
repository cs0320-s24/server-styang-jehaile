package edu.brown.cs.student.main.Server;

import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.CSVParser.CSVParser;
import edu.brown.cs.student.main.CSVParser.CreatorFromRow;
import edu.brown.cs.student.main.SearchUtility.SearchStrategy;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import spark.Request;
import spark.Response;
import spark.Route;

public class LoadCSVHandler implements Route {
  private CSVParser<List<String>> csvParser;
  private boolean isLoaded;
//  private String message;

  public LoadCSVHandler() {
    this.isLoaded = false;
//    this.message = "";
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {

    String fileName = request.queryParams("fileName");

    Boolean headers;
    try {

      headers = Boolean.valueOf(request.queryParams("headers")); // Could change to not wrapper
    } catch (IllegalArgumentException e) {
      return new LoadCSVFailureResponse("Please enter headers parameter as 'true' or 'false'")
          .serialize(); // Specify error reason (headers not 'True' or 'False'), refer to
      // SearchCSVHandler for how to do
    }

    try {
      String folderPath = "data";
      Path absoluteFolderPath = Paths.get(folderPath).toAbsolutePath();
      Path csvPath =
          Paths.get(
              absoluteFolderPath
                  + "/"
                  + fileName); // Add injection protection and more specific error messages
      FileReader fileReader = new FileReader(csvPath.toString());
      CreatorFromRow<List<String>> strategyObj = new SearchStrategy();

      this.csvParser = new CSVParser<>(fileReader, strategyObj, headers);
      this.csvParser.parse();
      this.isLoaded = true;
    } catch (IOException e) {
      return new LoadCSVFailureResponse("Error reading file").serialize();
    }
    LoadCSVSuccessResponse successResponse = new LoadCSVSuccessResponse();
    return successResponse.serialize();

//    return new LoadCSVSuccessResponse().serialize();
  }

  public record LoadCSVSuccessResponse(String responseType, String currentDateTime) {
    public LoadCSVSuccessResponse() {
      this("Loaded successfully! :)", LocalDateTime.now().toString());

//      this("Loaded successfully! :)");
//      DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss");
//      LocalDateTime now = LocalDateTime.now();
//      System.out.println(dtf.format(now));
//      this.message =  dtf.format(now);

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

  public boolean isLoaded() {
    return this.isLoaded;
  }

  public CSVParser<List<String>> getCSVParser() {
    return this.csvParser;
  }
}
