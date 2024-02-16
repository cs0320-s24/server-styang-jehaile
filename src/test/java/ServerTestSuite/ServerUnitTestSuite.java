package ServerTestSuite;

import ServerTestSuite.Mocks.MockBroadbandSource;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.Server.Broadband.BroadbandHandler.BroadbandDataSource;
import edu.brown.cs.student.main.Server.Broadband.BroadbandHandler.BroadbandDataSourceInterface;
import edu.brown.cs.student.main.Server.Broadband.BroadbandHandler.BroadbandHandler;
import edu.brown.cs.student.main.Server.Broadband.BroadbandHandler.CachingBroadbandDataSource;
import edu.brown.cs.student.main.Server.CSV.CSVDataSource;
import edu.brown.cs.student.main.Server.CSV.LoadCSVHandler;
import edu.brown.cs.student.main.Server.CSV.LoadCSVHandler.LoadCSVFailureResponse;
import edu.brown.cs.student.main.Server.CSV.LoadCSVHandler.LoadCSVSuccessResponse;
import edu.brown.cs.student.main.Server.CSV.SearchCSVHandler;
import edu.brown.cs.student.main.Server.CSV.ViewCSVHandler;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

public class ServerUnitTestSuite {
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
  private void testLoadCSVHandlerSuccess() {

  }

  public record LoadCSVSuccessResponse(String responseType) {


    public LoadCSVSuccessResponse() {
      this("Loaded successfully! :)");
    }

    /**
     * This method serializes the loadcsvsuccessresponse by instantiaiting an instance of the moshi class
     * Then building the JSON. This method returns a string which is returned from the json adapter method for this record,
     * serializing the current load file into the JSON string from a java object, when data is successfully loaded.
     * @return Returns a string stating the file was successfully loaded
     */
    String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      return moshi.adapter(LoadCSVHandler.LoadCSVSuccessResponse.class).toJson(this);
    }
  }

  /**
   *Record representing a failure response for loading a csv file.
   * @param responseType String parameter representing the response type
   * @param errorDescription String parameter representing the error description upon failure to load
   */

  public record LoadCSVFailureResponse(String responseType, String errorDescription) {
    /**
     * Constructor of failure response for load
     * @param errorDescription string representing the failure to load
     */

    public LoadCSVFailureResponse(String errorDescription) {
      this("Error", errorDescription);
    }

    /**
     * This is the serialize method for failure to load, it turns java data into a JSON String using moshi.
     * @return String returned describing the error loading
     */
    String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      return moshi.adapter(LoadCSVHandler.LoadCSVFailureResponse.class).toJson(this);
    }
  }
}
