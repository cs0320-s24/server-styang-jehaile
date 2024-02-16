package ServerTestSuite;

import ServerTestSuite.Requests.LoadCSVRequest;
import ServerTestSuite.Requests.SearchCSVRequest;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.Server.CSV.CSVDataSource;
import edu.brown.cs.student.main.Server.CSV.LoadCSVHandler;
import edu.brown.cs.student.main.Server.CSV.SearchCSVHandler;
import edu.brown.cs.student.main.Server.CSV.SearchCSVHandler.SearchCSVSuccessResponse;
import edu.brown.cs.student.main.Server.CSV.ViewCSVHandler;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testng.Assert;



public class ServerCSVUnitTest {
  private CSVDataSource csvDataSource;
  private LoadCSVHandler loadCSVHandler;
  private ViewCSVHandler viewCSVHandler;
  private SearchCSVHandler searchCSVHandler;

  @BeforeEach
  public void setup() {
    this.csvDataSource = new CSVDataSource();
    this.loadCSVHandler = new LoadCSVHandler(this.csvDataSource);
    this.viewCSVHandler = new ViewCSVHandler(this.csvDataSource);
    this.searchCSVHandler = new SearchCSVHandler(this.csvDataSource);
  }

  @Test
  public void testLoadCSVHandlerSuccess() {
    LoadCSVRequest loadCSVRequest = new LoadCSVRequest("ten-star.csv", "true");
    Object response = this.loadCSVHandler.handle(loadCSVRequest, null);

    String successResponse = new LoadCSVSuccessResponse().serialize();
    Assert.assertEquals(response, successResponse);
  }

  @Test
  public void testLoadCSVHandlerFailureMalformed() {
    LoadCSVRequest loadCSVRequest = new LoadCSVRequest("malformed_signs.csv", "true");
    Object response = this.loadCSVHandler.handle(loadCSVRequest, null);

    String failureResponse = new LoadCSVFailureResponse("Malformed file. "
        + "Please ensure all rows contain the same number of columns.").serialize();
    Assert.assertEquals(response, failureResponse);
  }

  @Test
  public void testLoadCSVHandlerFailureNoFilename() {
    LoadCSVRequest loadCSVRequest = new LoadCSVRequest(null, "true");
    Object response = this.loadCSVHandler.handle(loadCSVRequest, null);

    String failureResponse = new LoadCSVFailureResponse("Please enter headers and fileName parameters.").serialize();
    Assert.assertEquals(response, failureResponse);
  }

  @Test
  public void testLoadCSVHandlerFailureFileNotFound() {
    LoadCSVRequest loadCSVRequest = new LoadCSVRequest("no.csv", "true");
    Object response = this.loadCSVHandler.handle(loadCSVRequest, null);

    String failureResponse = new LoadCSVFailureResponse("Error reading file").serialize();
    Assert.assertEquals(response, failureResponse);
  }

  @Test
  public void testLoadCSVHandlerNullHeaders() {
    LoadCSVRequest loadCSVRequest = new LoadCSVRequest("ten-star.csv", null);
    Object response = this.loadCSVHandler.handle(loadCSVRequest, null);

    String failureResponse = new LoadCSVFailureResponse("Please enter headers and fileName parameters.").serialize();
    Assert.assertEquals(response, failureResponse);
  }

  @Test
  public void testLoadCSVHandlerMalformedHeaders() {
    LoadCSVRequest loadCSVRequest = new LoadCSVRequest("ten-star.csv", "maybe");
    Object response = this.loadCSVHandler.handle(loadCSVRequest, null);

    String failureResponse = new LoadCSVFailureResponse("Please enter headers parameter as " + "\"true\" or \"false\"").serialize();
    Assert.assertEquals(response, failureResponse);
  }

  @Test
  public void testLoadCSVHandlerFailureNotAccessible() {
    LoadCSVRequest loadCSVRequest = new LoadCSVRequest("inaccessibledata.csv", "true");
    Object response = this.loadCSVHandler.handle(loadCSVRequest, null);

    String failureResponse = new LoadCSVFailureResponse("Error reading file").serialize();
    Assert.assertEquals(response, failureResponse);
  }

  @Test
  public void testSearchCSVSuccess() {
    LoadCSVRequest loadCSVRequest = new LoadCSVRequest("ten-star.csv", "true");
    this.loadCSVHandler.handle(loadCSVRequest, null);
    SearchCSVRequest searchCSVRequest = new SearchCSVRequest("Proxima", null, null);
    String response = this.searchCSVHandler.handle(searchCSVRequest, null);

    Assert.assertTrue(response.contains("Proxima Centauri"));
  }

  @Test
  public void testSearchCSVFailureColumnDoesNotExist() {
    LoadCSVRequest loadCSVRequest = new LoadCSVRequest("ten-star.csv", "true");
    this.loadCSVHandler.handle(loadCSVRequest, null);
    SearchCSVRequest searchCSVRequest = new SearchCSVRequest("Proxima", null, "5");
    String response = this.searchCSVHandler.handle(searchCSVRequest, null);

    String failureResponse = new SearchCSVFailureResponse("Inputted index is out of bounds").serialize();
    Assert.assertEquals(response, failureResponse);
  }

  @Test
  public void testSearchCSVFailureColumnIndexWrongInput() {
    LoadCSVRequest loadCSVRequest = new LoadCSVRequest("ten-star.csv", "true");
    this.loadCSVHandler.handle(loadCSVRequest, null);
    SearchCSVRequest searchCSVRequest = new SearchCSVRequest("Proxima", null, "Galaxy");
    String response = this.searchCSVHandler.handle(searchCSVRequest, null);

    String failureResponse = new SearchCSVFailureResponse("Column index inputted in incorrect format").serialize();
    Assert.assertEquals(response, failureResponse);
  }

  @Test
  public void testSearchCSVFailureColumnHeaderNotExist() {
    LoadCSVRequest loadCSVRequest = new LoadCSVRequest("ten-star.csv", "true");
    this.loadCSVHandler.handle(loadCSVRequest, null);
    SearchCSVRequest searchCSVRequest = new SearchCSVRequest("Proxima", "Galaxy", null);
    String response = this.searchCSVHandler.handle(searchCSVRequest, null);

    String failureResponse = new SearchCSVFailureResponse("Column does not exist.").serialize();
    Assert.assertEquals(response, failureResponse);
  }

  @Test
  public void testSearchCSVFailureColumnHeadersAndIndex() {
    LoadCSVRequest loadCSVRequest = new LoadCSVRequest("ten-star.csv", "true");
    this.loadCSVHandler.handle(loadCSVRequest, null);
    SearchCSVRequest searchCSVRequest = new SearchCSVRequest("Proxima", "ProperName", "1");
    String response = this.searchCSVHandler.handle(searchCSVRequest, null);

    Assert.assertTrue(response.contains("Proxima Centauri"));
  }


  public record LoadCSVSuccessResponse(String responseType) {

    public LoadCSVSuccessResponse() {
      this("Loaded successfully! :)");
    }

    /**
     * This method serializes the loadcsvsuccessresponse by instantiaiting an instance of the moshi
     * class Then building the JSON. This method returns a string which is returned from the json
     * adapter method for this record, serializing the current load file into the JSON string from a
     * java object, when data is successfully loaded.
     *
     * @return Returns a string stating the file was successfully loaded
     */
    String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      return moshi.adapter(ServerCSVUnitTest.LoadCSVSuccessResponse.class).toJson(this);
    }
  }

  /**
   * Record representing a failure response for loading a csv file.
   *
   * @param responseType String parameter representing the response type
   * @param errorDescription String parameter representing the error description upon failure to
   *     load
   */
  public record LoadCSVFailureResponse(String responseType, String errorDescription) {
    /**
     * Constructor of failure response for load
     *
     * @param errorDescription string representing the failure to load
     */
    public LoadCSVFailureResponse(String errorDescription) {
      this("Error", errorDescription);
    }

    /**
     * This is the serialize method for failure to load, it turns java data into a JSON String using
     * moshi.
     *
     * @return String returned describing the error loading
     */
    String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      return moshi.adapter(ServerCSVUnitTest.LoadCSVFailureResponse.class).toJson(this);
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
      JsonAdapter<ServerCSVUnitTest.SearchCSVSuccessResponse> adapter =
          moshi.adapter(ServerCSVUnitTest.SearchCSVSuccessResponse.class);
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
      JsonAdapter<ServerCSVUnitTest.SearchCSVFailureResponse> adapter =
          moshi.adapter(ServerCSVUnitTest.SearchCSVFailureResponse.class);
      return adapter.toJson(this);
    }
  }
}
